package io.shulie.takin.web.biz.convert.db.parser;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson.JSON;
import com.pamirs.attach.plugin.dynamic.Converter;
import com.pamirs.attach.plugin.dynamic.Type;
import com.pamirs.attach.plugin.dynamic.template.Template;
import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import com.pamirs.takin.entity.domain.vo.dsmanage.DataSource;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationBizTableDTO;
import io.shulie.takin.web.biz.convert.db.parser.style.StyleTemplate;
import io.shulie.takin.web.biz.pojo.response.application.ShadowDetailResponse;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDsDbManageDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDsDbTableDAO;
import io.shulie.takin.web.data.dao.application.ConnectpoolConfigTemplateDAO;
import io.shulie.takin.web.data.model.mysql.ApplicationDsDbManageEntity;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationDsDbManageDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationDsDbTableDetailResult;
import io.shulie.takin.web.data.result.application.ConnectpoolConfigTemplateDetailResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @Author: 南风
 * @Date: 2021/9/13 4:15 下午
 */
@Service
public class DbTemplateParser extends AbstractTemplateParser {

    @Autowired
    private ApplicationDsDbManageDAO dsDbManageDAO;

    @Autowired
    private ApplicationClient applicationClient;

    @Autowired
    private ApplicationDsDbTableDAO dsDbTableDAO;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ConnectpoolConfigTemplateDAO connectpoolConfigTemplateDAO;


    /**
     * 将影子方案信息按对应模版解析
     *
     * @param dsType
     * @return
     */
    @Override
    public List<? extends StyleTemplate> convertShadowMsgWithTemplate(Integer dsType, Boolean isNewData, String cacheType, Converter.TemplateConverter.TemplateEnum templateEnum) {
        List list = Lists.newArrayList();

        if (DsTypeEnum.SHADOW_TABLE.getCode().equals(dsType)) {
            list.add(new ListStyle());
        } else {
            List<String> attributeArray;
            list.add(new InputStyle(INPUT_FILE_NAME_USER_NAME, INPUT_FILE_NAME_USER_NAME_CONTEXT, StyleEnums.INPUT.getCode()));
            list.add(new InputStyle(INPUT_FILE_NAME_URL, INPUT_FILE_NAME_URL_CONTEXT, StyleEnums.INPUT.getCode()));
            list.add(new InputStyle(PWD_FILE_NAME, PWD_FILE_NAME_CONTEXT, StyleEnums.PWD_INPUT.getCode()));
            if (Objects.nonNull(isNewData) && BooleanUtil.isFalse(isNewData)) {
                attributeArray = this.reflex();
            } else {
                attributeArray = this.getAttributeArray(templateEnum);
            }
            attributeArray.forEach(key -> {
                List<InputWithSelectStyle.NodeDetail> dataSource = new ArrayList<>();
                dataSource.add(new InputWithSelectStyle.NodeDetail(key1, "1"));
                dataSource.add(new InputWithSelectStyle.NodeDetail(key2, "2"));

                List<String> keys = Arrays.asList(key3, key4);
                InputWithSelectStyle.NodeInfo nodeInfo = new InputWithSelectStyle.NodeInfo(keys, dataSource);
                InputWithSelectStyle selectStyle = new InputWithSelectStyle(key, Objects.equals(DRIVER_CLASSNAME, key) ? DRIVER_CLASSNAME_SHOW_NAME : key,
                        StyleEnums.SELECT_WITH_INPUT.getCode(), nodeInfo);
                list.add(selectStyle);
            });

        }
        return list;
    }

    /**
     * 将影子数据映射成模板数据
     *
     * @param recordId
     * @return
     */
    @Override
    public ShadowDetailResponse convertDetailByTemplate(Long recordId) {
        ApplicationDsDbManageDetailResult convert = dsDbManageDAO.selectOneById(recordId);

        ShadowDetailResponse shadowDetailResponse = new ShadowDetailResponse();
        shadowDetailResponse.setId(convert.getId());
        shadowDetailResponse.setApplicationId(String.valueOf(convert.getApplicationId()));
        shadowDetailResponse.setMiddlewareType(Type.MiddleWareType.LINK_POOL.value());
        shadowDetailResponse.setDsType(convert.getDsType() == 100 ? DsTypeEnum.SHADOW_REDIS_SERVER.getCode() : convert.getDsType());
        shadowDetailResponse.setUrl(convert.getUrl());
        shadowDetailResponse.setUsername(convert.getUserName());
        shadowDetailResponse.setPassword(convert.getPwd());

        String shaDowFileExtedn = convert.getShaDowFileExtedn();
        if (StringUtils.isBlank(convert.getShaDowFileExtedn())
                || DsTypeEnum.SHADOW_TABLE.getCode().equals(convert.getDsType())) {
            shaDowFileExtedn = this.convertData(convert.getFileExtedn(), convert.getConnPoolName());
        }
        shadowDetailResponse.setShadowInfo(shaDowFileExtedn);
        shadowDetailResponse.setConnectionPool(convert.getConnPoolName());
        List<ShadowDetailResponse.TableInfo> tableInfos = this.buildTableData(convert.getApplicationId(),
                convert.getUrl(), convert.getUserName());
        shadowDetailResponse.setTables(tableInfos);
        return shadowDetailResponse;
    }

    public List<ShadowDetailResponse.TableInfo> buildTableData(Long appId, String url, String userName) {
        ApplicationDetailResult detailResult = applicationDAO.getApplicationById(appId);
        if (Objects.isNull(detailResult)) {
            return Collections.emptyList();
        }
        List<ApplicationBizTableDTO> amdbTableDatas = applicationClient.getApplicationTable(detailResult.getApplicationName(), url, userName);
        List<ShadowDetailResponse.TableInfo> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(amdbTableDatas)) {
            list.addAll(amdbTableDatas
                    .stream()
                    .map(x -> new ShadowDetailResponse.TableInfo(x.getBizDatabase(), x.getTableName()))
                    .collect(Collectors.toList()));
        }

        List<ApplicationDsDbTableDetailResult> results = dsDbTableDAO.getList(url, appId, userName);
        if (CollectionUtils.isEmpty(results)) {
            return list;
        }

        List<ShadowDetailResponse.TableInfo> resultsConvert = results
                .stream()
                .map(x -> new ShadowDetailResponse.TableInfo(x.getBizDataBase(), x.getBizTable(), x.getManualTag(), x.getShadowTable(), x.getIsCheck()))
                .collect(Collectors.toList());

        Map<String, ShadowDetailResponse.TableInfo> map = new HashMap<>();
        list.forEach(x -> {
            map.put(x.getBizDatabase() + "_" + x.getBizTableName(), x);
        });
        resultsConvert.forEach(x -> {
            map.put(x.getBizDatabase() + "_" + x.getBizTableName(), x);
        });

        return new ArrayList<>(map.values());
    }


    /**
     * 填充第一次的动态数据
     *
     * @param str
     * @return
     */
    public String convertData(String str, String connPoolName) {

        Map<String, Object> convertMap = new HashMap<>();
        if (StringUtils.isBlank(str)) {
            Converter.TemplateConverter.TemplateEnum templateEnum = super.convert(connPoolName);
            if (Objects.nonNull(templateEnum)) {
                List<String> attributeArray = this.getAttributeArray(templateEnum);
                attributeArray.forEach(protect -> {
                    Map<String, Object> innerMap = new HashMap<>();
                    innerMap.put(key3, "1");
                    innerMap.put(key5, "");
                    convertMap.put(protect, innerMap);
                });
            }
        } else {
            Map<String, Object> bizMap = JSON.parseObject(str, HashMap.class);
            bizMap.forEach((k, v) -> {
                Map<String, Object> innerMap = new HashMap<>();
                innerMap.put(key3, "1");
                innerMap.put(key5, String.valueOf(v));
                convertMap.put(k, innerMap);
            });
        }

        convertMap.put(PWD_FILE_NAME, "");
        convertMap.put(INPUT_FILE_NAME_USER_NAME, "");
        convertMap.put(INPUT_FILE_NAME_URL, "");
        return JSON.toJSONString(convertMap);
    }

    /**
     * 删除记录
     *
     * @param recordId
     */
    @Override
    public void deletedRecord(Long recordId) {
        ApplicationDsDbManageDetailResult detailResult = dsDbManageDAO.selectOneById(recordId);
        if (Objects.nonNull(detailResult) && detailResult.getSource() == 1) {
            dsDbManageDAO.removeRecord(recordId);
        }
    }

    @Override
    public void enable(Long recordId, Integer status) {
        ApplicationDsDbManageEntity entity = new ApplicationDsDbManageEntity();
        entity.setStatus(status);
        dsDbManageDAO.updateById(recordId, entity);
    }

    /**
     * 获取需要动态展示的属性集合
     *
     * @return
     */

    private List<String> getAttributeArray(Converter.TemplateConverter.TemplateEnum templateEnum) {
        List<String> attributeArray = Lists.newArrayList();
        try {
            if(Objects.isNull(templateEnum)){
                return  attributeArray;
            }
            Object t = templateEnum.getaClass().newInstance();
            if (Template.class.isAssignableFrom(t
                    .getClass())) {
                Template tem = (Template) t;
                ConnectpoolConfigTemplateDetailResult template = connectpoolConfigTemplateDAO.queryOne(tem.getName());
                if (Objects.nonNull(template) && StringUtils.isNotBlank(template.getShadowdbAttribute())) {
                    String shadowdbAttribute = template.getShadowdbAttribute();
                    attributeArray = Arrays.asList(shadowdbAttribute.split(","));
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return attributeArray;
    }

    private List<String> reflex() {
        Field[] f = DataSource.class.getDeclaredFields();
        Map<String, String> fieldMap = new HashMap<>();
        for (Field field : f) {
            fieldMap.put(field.getName(), field.getName());
        }
        fieldMap.remove("id");
        fieldMap.remove("url");
        fieldMap.remove("username");
        fieldMap.remove("schema");
        fieldMap.remove("password");
        fieldMap.remove("driverClassName");

        return (List<String>) fieldMap.values();
    }

}
