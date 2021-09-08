package io.shulie.takin.web.biz.convert.db.parser;

import cn.hutool.core.convert.Convert;
import com.pamirs.attach.plugin.dynamic.Type;
import com.pamirs.attach.plugin.dynamic.template.Template;
import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import io.shulie.takin.web.amdb.bean.result.application.AppShadowDatabaseDTO;
import io.shulie.takin.web.biz.pojo.response.application.ShadowDetailResponse;
import io.shulie.takin.web.data.dao.application.ApplicationDsCacheManageDAO;
import io.shulie.takin.web.data.result.application.ApplicationDsCacheManageDetailResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/9/7 1:54 下午
 */
@Service
@Slf4j
public class RedisTemplateParser extends AbstractTemplateParser {

    @Autowired
    private ApplicationDsCacheManageDAO dsCacheManageDAO;

    /**
     * 返回对应的模板对象
     *
     * @return
     */
    @Override
    public Class<?> getSupperClass() {
        return null;
    }

    /**
     * 返回解析后的模板对象
     *
     * @param dto
     * @return
     */
    @Override
    public <T extends Template> T convertTemplate(AppShadowDatabaseDTO dto) {
        return null;
    }

    /**
     * 将影子数据映射成模板数据 并设置页面样式
     * druid
     *
     * @param T
     * @return
     */
    @Override
    public ShadowDetailResponse convertDetailByTemplate(Long recordId) {
        ApplicationDsCacheManageDetailResult convert = dsCacheManageDAO.selectOneById(recordId);

        ShadowDetailResponse shadowDetailResponse = new ShadowDetailResponse();
        shadowDetailResponse.setId(convert.getId());
        shadowDetailResponse.setApplicationId(convert.getApplicationId());
        shadowDetailResponse.setMiddlewareType(Type.MiddleWareType.CACHE.value());
        shadowDetailResponse.setDsType(convert.getDsType());
        shadowDetailResponse.setUrl(convert.getColony());
        shadowDetailResponse.setPassword(convert.getPwd());
        shadowDetailResponse.setUsername(convert.getUserName());

        List<ShadowDetailResponse.ShadowProgrammeInfo> extInfo = Lists.newArrayList();
        if(DsTypeEnum.SHADOW_REDIS_CLUSTER.getCode().equals(convert.getDsType())){
            extInfo.add(new ShadowDetailResponse.ShadowProgrammeInfo("shadowUrl", "影子集群", "input", ""));
            extInfo.add(new ShadowDetailResponse.ShadowProgrammeInfo("shadowPwd", "密码", "password", ""));
        }
        shadowDetailResponse.setExtInfo(extInfo);
        return shadowDetailResponse;
    }

}
