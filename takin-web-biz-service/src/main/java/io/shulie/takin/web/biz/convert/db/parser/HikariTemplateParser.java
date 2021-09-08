package io.shulie.takin.web.biz.convert.db.parser;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONObject;
import com.pamirs.attach.plugin.dynamic.Type;
import com.pamirs.attach.plugin.dynamic.template.AbstractTemplate;
import com.pamirs.attach.plugin.dynamic.template.HikariTemplate;
import com.pamirs.attach.plugin.dynamic.template.Template;
import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import io.shulie.takin.web.amdb.bean.result.application.AppShadowDatabaseDTO;
import io.shulie.takin.web.biz.pojo.response.application.ShadowDetailResponse;
import io.shulie.takin.web.data.dao.application.ApplicationDsDbManageDAO;
import io.shulie.takin.web.data.result.application.ApplicationDsDbManageDetailResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * @Author: 南风
 * @Date: 2021/8/30 4:33 下午
 */
@Slf4j
@Service
public class HikariTemplateParser extends AbstractTemplateParser {

    @Autowired
    private ApplicationDsDbManageDAO dsDbManageDAO;
    /**
     * 返回对应的模板class
     *
     * @return
     */
    @Override
    public Class<?> getSupperClass() {
        return HikariTemplate.class;
    }


    /**
     * 返回解析后的模板对象
     *
     * @param dto
     * @return
     */
    @Override
    public <T extends Template> T convertTemplate(AppShadowDatabaseDTO dto) {
        String attachment = dto.getAttachment();
        HikariTemplate hikariTemplate = JSONObject.parseObject(attachment, HikariTemplate.class);
        return (T) hikariTemplate;
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
        ApplicationDsDbManageDetailResult convert = dsDbManageDAO.selectOneById(recordId);
        String shaDowFileExtedn = convert.getShaDowFileExtedn();
        HikariTemplate hikariTemplate = Convert.convert(HikariTemplate.class, shaDowFileExtedn);


        ShadowDetailResponse shadowDetailResponse = new ShadowDetailResponse();
        shadowDetailResponse.setId(convert.getId());
        shadowDetailResponse.setApplicationId(convert.getApplicationId());
        shadowDetailResponse.setMiddlewareType(Type.MiddleWareType.LINK_POOL.value());
        shadowDetailResponse.setDsType(convert.getDsType());
        shadowDetailResponse.setUrl(convert.getUrl());
        shadowDetailResponse.setUsername(convert.getUserName());
        shadowDetailResponse.setPassword(convert.getPwd());

        List<ShadowDetailResponse.ShadowProgrammeInfo> extInfo = Lists.newArrayList();
        if(DsTypeEnum.SHADOW_TABLE.getCode().equals(convert.getDsType())){
            extInfo.add(new ShadowDetailResponse.ShadowProgrammeInfo("tableList", "影子表", "list", convert.getShaDowFileExtedn()));
        }else{
            List<String> protectArray = this.getProtectArray(HikariTemplate.class);
            ShadowDetailResponse.ShadowProgrammeInfo info = null;
            Field field = null;
            for (String s : protectArray) {
                info = new ShadowDetailResponse.ShadowProgrammeInfo();
                info.setName(Objects.equals(s, "driverClassName") ? "驱动" : s);
                info.setType("selectWithInput");
                info.setKey(s);
                try {
                    field = hikariTemplate.getClass().getDeclaredField(s);
                    field.setAccessible(true);
                    info.setValue((String) field.get(hikariTemplate));
                } catch (Exception e) {
                    log.error("");//todo nf
                }
                extInfo.add(info);
            }
            extInfo.add(new ShadowDetailResponse.ShadowProgrammeInfo("shadowUserName", "影子数据源用户名", "input", ""));
            extInfo.add(new ShadowDetailResponse.ShadowProgrammeInfo("shadowUrl", "影子数据源", "input", ""));
            extInfo.add(new ShadowDetailResponse.ShadowProgrammeInfo("shadowPwd", "影子数据源密码", "password", ""));
        }
        shadowDetailResponse.setExtInfo(extInfo);
        return shadowDetailResponse;
    }


    /**
     * 获取protect属性集合
     *
     * @param cla
     * @return
     */
    @Override
    public List<String> getProtectArray(Class<? extends AbstractTemplate> cla) {
        return super.getProtectArray(cla);
    }
}
