package io.shulie.takin.web.biz.service.dsManage.impl.v2;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pamirs.attach.plugin.dynamic.Converter;
import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import io.shulie.takin.web.biz.convert.db.parser.DbTemplateParser;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsCreateInputV2;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInputV2;
import io.shulie.takin.web.biz.pojo.response.application.ShadowDetailResponse;
import io.shulie.takin.web.biz.service.dsManage.AbstractShaDowManageService;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.application.ApplicationDsDbManageDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDsDbTableDAO;
import io.shulie.takin.web.data.model.mysql.ApplicationDsDbManageEntity;
import io.shulie.takin.web.data.result.application.ApplicationDsDbManageDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationDsDbTableDetailResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: 南风
 * @Date: 2021/9/2 5:51 下午
 *
 */
@Service
public class ShaDowDbServiceImpl extends AbstractShaDowManageService {

    @Autowired
    private ApplicationDsDbManageDAO  dbManageDAO;

    @Autowired
    private ApplicationDsDbTableDAO dsDbTableDAO;

    @Autowired
    private DbTemplateParser templateParser;

    /**
     * 更新影子配置方案
     *
     * @param inputV2
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateShadowProgramme(ApplicationDsUpdateInputV2 inputV2) {
        ApplicationDsDbManageEntity entity = this.buildEntity(inputV2,false);
        dbManageDAO.updateById(inputV2.getId(),entity);
        if(DsTypeEnum.SHADOW_TABLE.getCode().equals(entity.getDsType())){
            this.saveShadowTable(inputV2.getId(),entity.getShaDowFileExtedn());
        }
    }


    /**
     * 创建影子配置方案
     *
     * @param inputV2
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createShadowProgramme(ApplicationDsCreateInputV2 inputV2,Boolean isJson) {
        //去重校验
        ApplicationDsDbManageDetailResult result = dbManageDAO.selectOne(inputV2.getApplicationName(),
                inputV2.getUrl(), inputV2.getUserName(),inputV2.getConnectionPool());
        if(Objects.nonNull(result)){
            throw new TakinWebException(TakinWebExceptionEnum.SHADOW_CONFIG_CREATE_ERROR,"业务数据源已存在");
        }
        ApplicationDsDbManageEntity entity = this.buildEntity(inputV2,true);
        entity.setId(null); //去除原id
        dbManageDAO.saveOne(entity);
        if(DsTypeEnum.SHADOW_TABLE.getCode().equals(entity.getDsType())){
            this.saveShadowTable(entity.getId(),entity.getShaDowFileExtedn());
        }
    }

    private ApplicationDsDbManageEntity buildEntity(ApplicationDsCreateInputV2 inputV2,Boolean isCreate){
        ApplicationDsDbManageEntity entity = new ApplicationDsDbManageEntity();
        WebPluginUtils.fillUserData(inputV2);
        BeanUtils.copyProperties(inputV2,entity);
        entity.setDsType(inputV2.getDsType());
        entity.setShaDowUrl(inputV2.getShaDowUrl());
        entity.setShaDowUserName(inputV2.getShaDowUserName());
        entity.setShaDowPwd(inputV2.getShaDowPassword());
        entity.setUrl(inputV2.getUrl());
        entity.setUserName(Objects.equals("-",inputV2.getUsername())?"":inputV2.getUsername());
        entity.setConnPoolName(inputV2.getConnectionPool());
        entity.setStatus(0);
        String extInfo = inputV2.getExtInfo();
        if(DsTypeEnum.SHADOW_TABLE.getCode().equals(inputV2.getDsType())){
            extInfo = JSONObject.parseObject(inputV2.getExtInfo()).get("shaDowTaleInfo").toString();
        }
        entity.setShaDowFileExtedn(extInfo);

        if(isCreate){
            entity.setDbName("");
            entity.setPwd("");
            entity.setConfigJson("");
            entity.setSource(1);
            entity.setFileExtedn(inputV2.getParseConfig());
            entity.setConfigJson(inputV2.getIsOld()?"老转新":"");
            if("other".equals(entity.getConnPoolName())){
                entity.setAgentSourceType( Converter.TemplateConverter.TemplateEnum._default.getKey());
            }else if("兼容老版本(影子表)".equals(entity.getConnPoolName())
                    || "兼容老版本(影子库)".equals(entity.getConnPoolName())){
                entity.setAgentSourceType( Converter.TemplateConverter.TemplateEnum._2.getKey());
            }
            else{
                Converter.TemplateConverter.TemplateEnum templateEnum = templateParser.convert(entity.getConnPoolName());
                if (templateEnum != null) {
                    entity.setAgentSourceType(templateEnum.getKey());
                }
            }
        }
        return entity;
    }

    private void saveShadowTable(Long recordId,String str){
        if(StringUtils.isBlank(str)){
            return;
        }
        ApplicationDsDbManageDetailResult detailResult = dbManageDAO.selectOneById(recordId);
        if(Objects.isNull(detailResult)){
            return;
        }
        List<ApplicationDsDbTableDetailResult> list = dsDbTableDAO.getList(detailResult.getUrl(), detailResult.getApplicationId(),
                detailResult.getUserName());
        if(CollectionUtils.isNotEmpty(list)){
            dsDbTableDAO.batchDeleted(list);
        }
        List<ShadowDetailResponse.TableInfo> tableInfos = JSONArray.parseArray(str, ShadowDetailResponse.TableInfo.class);
        List<ApplicationDsDbTableDetailResult> collect = tableInfos.stream().map(x -> {
            ApplicationDsDbTableDetailResult result = new ApplicationDsDbTableDetailResult();
            result.setAppId(detailResult.getApplicationId());
            result.setUrl(detailResult.getUrl());
            result.setUserName( detailResult.getUserName());
            result.setBizDataBase(x.getBizDatabase());
            result.setBizTable(x.getBizTableName());
            result.setShadowTable(x.getShaDowTableName());
            result.setManualTag(x.getIsManual() ? 1 : 0);
            result.setIsCheck(x.getIsCheck()?1:0);
            return result;
        }).collect(Collectors.toList());
        dsDbTableDAO.batchSave(collect);
    }

}
