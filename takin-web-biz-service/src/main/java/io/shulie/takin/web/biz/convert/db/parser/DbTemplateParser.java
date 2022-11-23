package io.shulie.takin.web.biz.convert.db.parser;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.pamirs.attach.plugin.dynamic.one.Converter;
import com.pamirs.attach.plugin.dynamic.one.Type;
import com.pamirs.attach.plugin.dynamic.one.template.Template;
import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import com.pamirs.takin.entity.domain.vo.dsmanage.DataSource;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationBizTableDTO;
import io.shulie.takin.web.biz.convert.db.parser.style.StyleTemplate;
import io.shulie.takin.web.biz.pojo.response.application.ShadowDetailResponse;
import io.shulie.takin.web.biz.service.dsManage.DsService;
import io.shulie.takin.web.biz.service.dsManage.impl.DsServiceImpl;
import io.shulie.takin.web.common.util.JsonUtil;
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

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
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

    @Autowired
    private DsService dsService;

    /**
     * 将影子方案信息按对应模版解析
     *
     * @param dsType
     * @return
     */
    @Override
    public List<? extends StyleTemplate> convertShadowMsgWithTemplate(Integer dsType, Boolean isNewData,
                                                                      String cacheType, Converter.TemplateConverter.TemplateEnum templateEnum,
                                                                      ShadowTemplateSelect select) {
        List list = Lists.newArrayList();
        if (DsTypeEnum.SHADOW_TABLE.getCode().equals(dsType)) {
            list.add(new ListStyle());
            return list;
        }

        List<String> attributeArray;
        list.add(new InputStyle(INPUT_FILE_NAME_URL, INPUT_FILE_NAME_URL_CONTEXT, StyleEnums.INPUT.getCode()));
        if (select.isNewVersion()) {
            List<InputWithSelectStyle.NodeDetail> dataSource_username = new ArrayList<>();
            dataSource_username.add(new InputWithSelectStyle.NodeDetail(key7, "1"));
            dataSource_username.add(new InputWithSelectStyle.NodeDetail(key2, "2"));
            List<String> keys_username = Arrays.asList(key3, key4);
            InputWithSelectStyle.NodeInfo nodeInfo_username = new InputWithSelectStyle.NodeInfo(keys_username, dataSource_username);
            InputWithSelectStyle selectStyle_username = new InputWithSelectStyle(INPUT_FILE_NAME_USER_NAME, INPUT_FILE_NAME_USER_NAME_CONTEXT, StyleEnums.SELECT_WITH_INPUT.getCode(), nodeInfo_username);
            list.add(selectStyle_username);

            List<InputWithSelectStyle.NodeDetail> dataSource_pwd = new ArrayList<>();
            dataSource_pwd.add(new InputWithSelectStyle.NodeDetail(key8, "1"));
            dataSource_pwd.add(new InputWithSelectStyle.NodeDetail(key2, "2"));
            dataSource_pwd.add(new InputWithSelectStyle.NodeDetail(key9, "3"));

            List<String> keys_pwd = Arrays.asList(key3, key4);
            InputWithSelectStyle.NodeInfo nodeInfo_pwd = new InputWithSelectStyle.NodeInfo(keys_pwd, dataSource_pwd);
            InputWithSelectStyle selectStyle_pwd = new InputWithSelectStyle(PWD_FILE_NAME, PWD_FILE_NAME_CONTEXT, StyleEnums.SELECT_WITH_INPUT_PWD.getCode(), nodeInfo_pwd);
            list.add(selectStyle_pwd);
        } else {
            list.add(new InputStyle(INPUT_FILE_NAME_USER_NAME, INPUT_FILE_NAME_USER_NAME_CONTEXT, StyleEnums.INPUT.getCode()));
            list.add(new InputStyle(PWD_FILE_NAME, PWD_FILE_NAME_CONTEXT, StyleEnums.PWD_INPUT.getCode()));
        }
        attributeArray = Objects.nonNull(isNewData) && BooleanUtil.isFalse(isNewData) ? this.reflex() : this.getAttributeArray(templateEnum);

        attributeArray.forEach(key -> {
            List<InputWithSelectStyle.NodeDetail> dataSource = new ArrayList<>();
            dataSource.add(new InputWithSelectStyle.NodeDetail(key1, "1"));
            dataSource.add(new InputWithSelectStyle.NodeDetail(key2, "2"));

            List<String> keys = Arrays.asList(key3, key4);
            InputWithSelectStyle.NodeInfo nodeInfo = new InputWithSelectStyle.NodeInfo(keys, dataSource);
            InputWithSelectStyle selectStyle = new InputWithSelectStyle(key, Objects.equals(DRIVER_CLASSNAME, key) ? DRIVER_CLASSNAME_SHOW_NAME : key, StyleEnums.SELECT_WITH_INPUT.getCode(), nodeInfo);
            list.add(selectStyle);
        });
        return list;
    }

    /**
     * 将影子数据映射成模板数据
     *
     * @param recordId
     * @return
     */
    @Override
    public ShadowDetailResponse convertDetailByTemplate(Long recordId, String appName) {
        ApplicationDsDbManageDetailResult convert = dsDbManageDAO.selectOneById(recordId);

        ShadowDetailResponse shadowDetailResponse = new ShadowDetailResponse();
        shadowDetailResponse.setId(convert.getId());
        shadowDetailResponse.setApplicationId(String.valueOf(convert.getApplicationId()));
        shadowDetailResponse.setMiddlewareType(Type.MiddleWareType.LINK_POOL.value());
        shadowDetailResponse.setDsType(convert.getDsType() == 100 ? DsTypeEnum.SHADOW_REDIS_SERVER.getCode() : convert.getDsType());
        shadowDetailResponse.setUrl(convert.getUrl());
        shadowDetailResponse.setUsername(StringUtils.isBlank(convert.getUserName()) ? "-" : convert.getUserName());
        shadowDetailResponse.setPassword(convert.getPwd());
        shadowDetailResponse.setIsManual(convert.getSource());
        String shaDowFileExtedn = convert.getShaDowFileExtedn();
        if (StringUtils.isBlank(shaDowFileExtedn)
                || DsTypeEnum.SHADOW_TABLE.getCode().equals(convert.getDsType())) {
            shaDowFileExtedn = this.convertData(convert.getFileExtedn(), convert.getConnPoolName());
        }
        if (StringUtils.isNotBlank(shaDowFileExtedn) &&
                (DsTypeEnum.SHADOW_REDIS_SERVER.getCode().equals(convert.getDsType())
                        || DsTypeEnum.SHADOW_DB.getCode().equals(convert.getDsType()))) {
            shaDowFileExtedn = buildNewShadow(shaDowFileExtedn, appName);
        }
        shadowDetailResponse.setShadowInfo(shaDowFileExtedn);
        shadowDetailResponse.setConnectionPool(convert.getConnPoolName());
        List<ShadowDetailResponse.TableInfo> tableInfos = this.buildTableData(convert.getApplicationId(),
                convert.getUrl(), convert.getUserName());
        shadowDetailResponse.setTables(tableInfos);
        return shadowDetailResponse;
    }

    /**
     * 查询的时候，把历史数据拼接回去
     *
     * @param shaDowFileExtedn
     * @return
     */
    public String buildNewShadow(String shaDowFileExtedn, String appName) {
        JSONObject extObj = Optional.ofNullable(JSONObject.parseObject(shaDowFileExtedn)).orElse(new JSONObject());
        // 获取写入时候的标记字段，是否为新版本,先判断标记
        String extFlag = extObj.getString(DsServiceImpl.EXT_FLAG);
        if (StringUtils.isBlank(extFlag)) {
            // 根据应用去判断下当前应用版本是否最新，把旧数据的展现形式也调整下
            ShadowTemplateSelect select = dsService.processSelect(appName);
            if (!select.isNewVersion()) {
                // 返回默认
                return shaDowFileExtedn;
            }
        }
        String shadowUserNameStr = extObj.getString("shadowUserName");
        Map<String, Object> userNameMap = Maps.newHashMap();
        if (StringUtils.isBlank(shadowUserNameStr)) {
            userNameMap.put("tag", "1");
        } else {
            userNameMap.put("tag", "2");
            userNameMap.put("context", shadowUserNameStr);
        }
        extObj.put("shadowUserName", userNameMap);

        Map<String, Object> pwdMap = Maps.newHashMap();
        String shadowPwdStr = extObj.getString("shadowPwd");
        if (StringUtils.isBlank(shadowPwdStr)) {
            pwdMap.put("tag", "1");
        }

        if(extFlag == null){
            pwdMap.put("tag", "2");
            pwdMap.put("context", shadowPwdStr);
        } else if("true".equals(extFlag)){
            pwdMap.put("tag", "2");
            pwdMap.put("context", shadowPwdStr);
        } else if("3".equals(extFlag)){
            pwdMap.put("tag", "3");
            pwdMap.put("context", shadowPwdStr);
        }
        extObj.put("shadowPwd", pwdMap);
        return JSON.toJSONString(extObj);
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
        if (StringUtils.isBlank(str) || !JsonUtil.isJson(str)) {
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
        entity.setGmtUpdate(new Date());
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
            if (Objects.isNull(templateEnum) || Converter.TemplateConverter.TemplateEnum._default.equals(templateEnum)) {
                return attributeArray;
            }
            Object t = templateEnum.getaClass().newInstance();
            if (Template.class.isAssignableFrom(t.getClass())) {
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
        fieldMap.remove("password");

        return new ArrayList<>(fieldMap.values());
    }

}
