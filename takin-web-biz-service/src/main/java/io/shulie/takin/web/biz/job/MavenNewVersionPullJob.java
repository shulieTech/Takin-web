package io.shulie.takin.web.biz.job;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.job.entity.Docs;
import io.shulie.takin.web.biz.job.entity.OrgMavenResponse;
import io.shulie.takin.web.biz.service.application.MiddlewareJarService;
import io.shulie.takin.web.biz.service.application.MiddlewareSummaryService;
import io.shulie.takin.web.common.enums.application.ApplicationMiddlewareStatusEnum;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.model.mysql.MiddlewareJarEntity;
import io.shulie.takin.web.data.model.mysql.MiddlewareSummaryEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * maven 拉取新版本job
 *
 * @author liqiyu
 */
@Component
@ElasticSchedulerJob(jobName = "MavenNewVersionPullJob", cron = "0 0 3 * * ? *", description = "定时查询阿里云maven仓库是否有新的maven版本")
@Slf4j
public class MavenNewVersionPullJob implements SimpleJob {

    @Autowired
    private MiddlewareJarService middlewareJarService;

    @Autowired
    private MiddlewareSummaryService middlewareSummaryService;

    private static final String ALIYUN_MAVEN_URL = "https://maven.aliyun.com/artifact/aliyunMaven/searchArtifactByGav?groupId=%s" + "&artifactId=%s&version=&repoId=all&_input_charset=utf-8";

    private static final String SEARCH_MAVEN_URL_ORG = "https://search.maven.org/solrsearch/select?q=g:%s+AND+a:%s&core=gav&start=0&rows=99999";

    @Value("${maven.pull.job.enable:true}")
    private boolean mavenenable;

    @Override
    public void execute(ShardingContext shardingContext) {
        if (!mavenenable) {
            return;
        }
        final List<MiddlewareSummaryEntity> middlewareSummaryEntityList = middlewareSummaryService.list();
        middlewareSummaryEntityList.stream().map(middlewareSummaryEntity -> {
            String url = "";
            final long start = System.currentTimeMillis();
            try {
                url = String.format(SEARCH_MAVEN_URL_ORG, middlewareSummaryEntity.getGroupId(), middlewareSummaryEntity.getArtifactId());
                final String httpResponseStr = HttpUtil.get(url, 60 * 1000);
                OrgMavenResponse httpResponse;
                try {
                    httpResponse = JSONObject.parseObject(httpResponseStr, OrgMavenResponse.class, Feature.IgnoreNotMatch);
                } catch (Exception e) {
                    log.error(String.format("响应信息解析失败。response：%s,groupId:%s,artifactId:%s", httpResponseStr, middlewareSummaryEntity.getGroupId(), middlewareSummaryEntity.getArtifactId()), e);
                    return null;
                }
                if (httpResponse.getResponse().getNumFound() > 0) {
                    final List<Docs> collect = httpResponse.getResponse().getDocs().parallelStream().filter(jarInfo -> "jar".equals(jarInfo.getP())).filter(jarInfo -> !"unknown".equalsIgnoreCase(jarInfo.getV())).collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> CommonUtil.joinAgv(o.getA(), o.getG(), o.getV())))), ArrayList::new));
                    if (collect.size() == middlewareSummaryEntity.getTotalNum()) {
                        return null;
                    } else {
                        return new Object[]{middlewareSummaryEntity, collect};
                    }
                } else {
                    log.warn("请求数量为0，url:" + url);
                }
            } catch (Exception e) {
                log.error(String.format("请求出错，耗时：%d 秒,url:%s", (System.currentTimeMillis() - start) / 1000, url), e);
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList()).forEach(objects -> {
            MiddlewareSummaryEntity middlewareSummaryEntity = (MiddlewareSummaryEntity) objects[0];
            List<Docs> jarInfoList = (List<Docs>) objects[1];

            final QueryWrapper<MiddlewareJarEntity> queryWrapper = new QueryWrapper<>();
            final LambdaQueryWrapper<MiddlewareJarEntity> lambda = queryWrapper.lambda();
            lambda.eq(MiddlewareJarEntity::getArtifactId, middlewareSummaryEntity.getArtifactId());
            lambda.eq(MiddlewareJarEntity::getGroupId, middlewareSummaryEntity.getGroupId());
            final List<MiddlewareJarEntity> middlewareJarEntityList = middlewareJarService.list(queryWrapper);
            final Map<String, MiddlewareJarEntity> versionMap = middlewareJarEntityList.parallelStream().collect(Collectors.toMap(MiddlewareJarEntity::getVersion, Function.identity()));
            final List<MiddlewareJarEntity> collect = jarInfoList.parallelStream().map(jarInfo -> {
                final String agv = CommonUtil.joinAgv(middlewareSummaryEntity.getArtifactId(), middlewareSummaryEntity.getGroupId(), jarInfo.getV());
                // 查到了的就不用管了
                if (versionMap.containsKey(jarInfo.getV())) {
                    return null;
                }
                final MiddlewareJarEntity middlewareJarEntity = new MiddlewareJarEntity();
                middlewareJarEntity.setArtifactId(jarInfo.getA());
                middlewareJarEntity.setGroupId(jarInfo.getG());
                middlewareJarEntity.setVersion(jarInfo.getV());
                middlewareJarEntity.setAgv(agv);
                middlewareJarEntity.setGmtCreate(new Date());
                middlewareJarEntity.setGmtUpdate(new Date());
                middlewareJarEntity.setName(middlewareSummaryEntity.getName());
                middlewareJarEntity.setType(middlewareSummaryEntity.getType());
                // 如果汇总信息是已支持。则新增的设置为 不支持,其它的状态的设置的同汇总信息一致
                if (ApplicationMiddlewareStatusEnum.SUPPORTED.getCode().equals(middlewareSummaryEntity.getStatus())) {
                    middlewareJarEntity.setStatus(ApplicationMiddlewareStatusEnum.NOT_SUPPORTED.getCode());
                } else {
                    middlewareJarEntity.setStatus(middlewareSummaryEntity.getStatus());
                }
                return middlewareJarEntity;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            middlewareJarService.saveBatch(collect);
            middlewareJarService.reCompute(middlewareSummaryEntity);
            middlewareSummaryService.updateById(middlewareSummaryEntity);
            log.info(String.format("中间件：groupId:%s artifactId:%s 发现新版本 %d个", middlewareSummaryEntity.getGroupId(), middlewareSummaryEntity.getArtifactId(), collect.size()));
        });
    }

}
