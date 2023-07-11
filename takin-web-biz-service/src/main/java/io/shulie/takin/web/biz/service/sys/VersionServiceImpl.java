package io.shulie.takin.web.biz.service.sys;

import javax.annotation.Resource;

import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.data.dao.sys.VersionDAO;
import io.shulie.takin.web.data.model.mysql.VersionEntity;
import io.shulie.takin.web.data.result.system.VersionVo;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class VersionServiceImpl implements VersionService {

    @Value("${takin.web.version:}")
    private String version;

    @Value("${takin.web.upgrade.ignore-snapshot:true}")
    private boolean ignoreSnapshot;

    @Resource
    private VersionDAO versionDAO;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 发布新版本，先更新一下旧版本信息
     *
     * @param entity 版本信息
     * @return true-发布版本成功
     */
    @Override
    public boolean publish(VersionEntity entity) {
        boolean publishSuccess = versionDAO.publish(entity);
        afterPublish(publishSuccess, entity);
        return publishSuccess;
    }

    @Override
    public VersionVo selectVersion() {
        ValueOperations opsForValue = redisTemplate.opsForValue();
        String cacheKey = getCacheKey();
        String version = (String)opsForValue.get(cacheKey);
        Long userId = WebPluginUtils.traceUserId();
        if (userId == null || userId < 0) {
            userId = 0L;
        }
        boolean show = !(ignore() || opsForValue.getBit(getConfirmKey(), userId));
        if (StringUtils.isNotBlank(version)) {
            VersionVo vo = JsonHelper.json2Bean(version, VersionVo.class);
            vo.setShow(show);
            return vo;
        }
        VersionVo vo = new VersionVo();
        vo.setHasNew(false);
        VersionEntity least = versionDAO.selectLeast();
        if (least != null) {
            VersionEntity preVersion = versionDAO.selectLeastOnVersionCondition(false, least.getVersion());
            vo.setPreVersion(preVersion);
        }
        vo.setCurVersion(least);
        opsForValue.set(cacheKey, JsonHelper.bean2Json(vo));
        vo.setShow(show);
        return vo;
    }

    @Override
    public void confirm() {
        redisTemplate.opsForValue().setBit(getConfirmKey(), WebPluginUtils.traceUserId(), true);
    }

    @Override
    public boolean ignore() {
        return StringUtils.isBlank(version) || (ignoreSnapshot && StringUtils.endsWithIgnoreCase(version, "SNAPSHOT"));
    }

    // 更新版本信息缓存
    private void afterPublish(boolean success, VersionEntity entity) {
        ValueOperations opsForValue = redisTemplate.opsForValue();
        String version = entity.getVersion();
        VersionVo vo = new VersionVo();
        vo.setHasNew(success);
        // 不考虑数据库插入异常情况
        vo.setCurVersion(versionDAO.selectLeastOnVersionCondition(true, version));
        vo.setPreVersion(versionDAO.selectLeastOnVersionCondition(false, version));
        opsForValue.set(getCacheKey(), JsonHelper.bean2Json(vo));
    }

    private String getCacheKey() {
        return "tro:version";
    }

    private String getConfirmKey() {
        return getCacheKey() + ":confirm:" + version;
    }
}