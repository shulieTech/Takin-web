package io.shulie.takin.web.biz.convert.db.parser;

import com.pamirs.attach.plugin.dynamic.one.Converter;
import com.pamirs.attach.plugin.dynamic.one.Type;
import com.pamirs.attach.plugin.dynamic.one.template.Info;
import com.pamirs.attach.plugin.dynamic.one.template.RedisTemplate;
import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.convert.db.parser.style.StyleTemplate;
import io.shulie.takin.web.biz.pojo.response.application.ShadowDetailResponse;
import io.shulie.takin.web.biz.service.BaseConfigService;
import io.shulie.takin.web.data.dao.application.ApplicationDsCacheManageDAO;
import io.shulie.takin.web.data.model.mysql.ApplicationDsCacheManageEntity;
import io.shulie.takin.web.data.result.application.ApplicationDsCacheManageDetailResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: 南风
 * @Date: 2021/9/7 1:54 下午
 */
@Service
@Slf4j
public class RedisTemplateParser extends AbstractTemplateParser {

    @Autowired
    private ApplicationDsCacheManageDAO dsCacheManageDAO;

    @Autowired
    private BaseConfigService baseConfigService;


    /**
     * 将影子数据映射成模板数据 并设置页面样式
     * druid
     *
     * @param recordId
     * @return
     */
    @Override
    public ShadowDetailResponse convertDetailByTemplate(Long recordId,String appName) {
        ApplicationDsCacheManageDetailResult convert = dsCacheManageDAO.selectOneById(recordId);
        if (Objects.isNull(convert)) {
            return null;
        }
        ShadowDetailResponse shadowDetailResponse = new ShadowDetailResponse();
        shadowDetailResponse.setId(convert.getId());
        shadowDetailResponse.setApplicationId(String.valueOf(convert.getApplicationId()));
        shadowDetailResponse.setMiddlewareType(Type.MiddleWareType.CACHE.value());
        shadowDetailResponse.setDsType(convert.getDsType());
        shadowDetailResponse.setUrl(convert.getColony());
        shadowDetailResponse.setPassword(convert.getPwd());
        shadowDetailResponse.setUsername(convert.getUserName());
        shadowDetailResponse.setConnectionPool(convert.getCacheName());
        shadowDetailResponse.setShadowInfo(convert.getShaDowFileExtedn());
        shadowDetailResponse.setCacheType(convert.getType());
        shadowDetailResponse.setIsManual(convert.getSource());
        if (StringUtils.isNotBlank(convert.getColony())){
            Map<String, String> bizCacheMap = JsonHelper.json2Map(convert.getColony(), String.class, String.class);
            shadowDetailResponse.setCacheDatabase(bizCacheMap.get("database"));
            shadowDetailResponse.setCacheMaster(bizCacheMap.get("master"));
            shadowDetailResponse.setCacheNodes(bizCacheMap.get("nodes"));
        }
        if (StringUtils.isNotBlank(convert.getShaDowFileExtedn())){
            Map<String, String> shadowCacheMap = JsonHelper.json2Map(convert.getShaDowFileExtedn(), String.class, String.class);
            shadowDetailResponse.setCacheShadowDatabase(shadowCacheMap.get("database"));
            shadowDetailResponse.setCacheShadowMaster(shadowCacheMap.get("master"));
            shadowDetailResponse.setCacheShadowNodes(shadowCacheMap.get("nodes"));
            shadowDetailResponse.setCacheShadowPassword(shadowCacheMap.get("password"));
        }
        return shadowDetailResponse;
    }

    /**
     * 将影子方案按对应模版解析成页面样式
     *
     * @param dsType
     * @return
     */
    @Override
    public List<? extends StyleTemplate> convertShadowMsgWithTemplate(Integer dsType, Boolean isNewData, String cacheType, Converter.TemplateConverter.TemplateEnum templateEnum, ShadowTemplateSelect select) {
        List list = Lists.newArrayList();
        list.add(new InputStyle("cacheNodes", "业务nodes", StyleEnums.TEXT_INPUT.getCode(), true));
        if ("哨兵模式".equals(cacheType) || "主从模式".equals(cacheType)){
            list.add(new InputStyle("cacheMaster", "业务master", StyleEnums.TEXT_INPUT.getCode(), true));
        }
        list.add(new InputStyle("cacheDatabase", "业务database", StyleEnums.TEXT_INPUT.getCode(), false));
        if (DsTypeEnum.SHADOW_REDIS_CLUSTER.getCode().equals(dsType)) {
            list.add(new InputStyle("cacheShadowNodes", "影子nodes", StyleEnums.TEXT_INPUT.getCode(), true));
            if ("哨兵模式".equals(cacheType) || "主从模式".equals(cacheType)){
                list.add(new InputStyle("cacheShadowMaster", "影子master", StyleEnums.TEXT_INPUT.getCode(), true));
            }
            list.add(new InputStyle("cacheShadowPassword", "影子password", StyleEnums.PWD_INPUT.getCode(), true));
            list.add(new InputStyle("cacheShadowDatabase", "影子database", StyleEnums.TEXT_INPUT.getCode(), false));
        }
        return list;
    }


    /**
     * 删除记录
     *
     * @param recordId
     */
    @Override
    public void deletedRecord(Long recordId) {
        ApplicationDsCacheManageDetailResult detailResult = dsCacheManageDAO.selectOneById(recordId);
        if (Objects.nonNull(detailResult) && detailResult.getSource() == 1) {
            dsCacheManageDAO.removeRecord(recordId);
        }
    }

    @Override
    public void enable(Long recordId, Integer status) {
        ApplicationDsCacheManageEntity entity = new ApplicationDsCacheManageEntity();
        entity.setStatus(status);
        entity.setGmtUpdate(new Date());
        dsCacheManageDAO.updateById(recordId, entity);
    }

    /**
     * 获取缓存支持的模式
     *
     * @return
     */
    @Override
    public List<SelectVO> queryCacheType() {
        List<SelectVO> vos = Arrays.stream(RedisTemplate.MODEL.values()).map(model -> {
            try {
                Field declaredField = RedisTemplate.MODEL.class.getDeclaredField(model.toString());
                String describe = declaredField.getAnnotation(Info.class).describe();
                return new SelectVO(describe, describe);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());

        return vos;
    }


    private Map<String, String> generateTips() {
        Map<String, String> tipsMap = new HashMap<>();
        Arrays.stream(RedisTemplate.MODEL.values()).forEach(
                model -> {
                    try {
                        Field declaredField = RedisTemplate.MODEL.class.getDeclaredField(model.toString());
                        String name = declaredField.getName();
                        String describe = declaredField.getAnnotation(Info.class).describe();
                        TBaseConfig tBaseConfig = baseConfigService.queryByConfigCode(key6 + name);
                        if (Objects.nonNull(tBaseConfig)) {
                            tipsMap.put(describe, tBaseConfig.getConfigValue());
                        }
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
        );

        return tipsMap;
    }


    public Map<String, String> convertModel() {
        Map<String, String> modelMap = new HashMap<>();
        Arrays.stream(RedisTemplate.MODEL.values()).forEach(
                model -> {
                    try {
                        Field declaredField = RedisTemplate.MODEL.class.getDeclaredField(model.toString());
                        String name = declaredField.getName();
                        String describe = declaredField.getAnnotation(Info.class).describe();
                        modelMap.put(describe, name);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
        );
        return modelMap;
    }


}
