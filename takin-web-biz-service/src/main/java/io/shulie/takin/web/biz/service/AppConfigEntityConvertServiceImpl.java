package io.shulie.takin.web.biz.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Lists;
import com.pamirs.takin.common.constant.AppConfigSheetEnum;
import com.pamirs.takin.entity.domain.entity.simplify.TShadowJobConfig;
import com.pamirs.takin.entity.domain.vo.guardmanage.LinkGuardVo;
import io.shulie.takin.cloud.common.constants.Constants;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerCreateInput;
import io.shulie.takin.web.biz.pojo.input.whitelist.WhitelistImportFromExcelInput;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.enums.excel.BooleanEnum;
import io.shulie.takin.web.common.enums.shadow.ShadowMqConsumerType;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.common.util.application.RemoteCallUtils;
import io.shulie.takin.web.common.util.whitelist.WhitelistUtil;
import io.shulie.takin.web.data.dao.application.AppRemoteCallDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.param.application.AppRemoteCallQueryParam;
import io.shulie.takin.web.data.param.application.AppRemoteCallUpdateParam;
import io.shulie.takin.web.data.param.application.ApplicationDsCreateParam;
import io.shulie.takin.web.data.param.blacklist.BlacklistCreateNewParam;
import io.shulie.takin.web.data.result.application.AppRemoteCallResult;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mubai
 * @date 2021-02-25 10:30
 */

@Service
public class AppConfigEntityConvertServiceImpl implements AppConfigEntityConvertService {

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private AppRemoteCallDAO appRemoteCallDAO;

    private Integer number;

    @PostConstruct
    public void init() {
        number = ConfigServerHelper.getWrapperIntegerValueByKey(ConfigServerKeyEnum.TAKIN_WHITE_LIST_NUMBER_LIMIT);
    }

    /**
     * 将数组转化为entity
     *
     * @param sourceList
     * @return
     */

    @Override
    public List<ApplicationDsCreateParam> convertDsSheet(ArrayList<ArrayList<String>> sourceList) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return null;
        }
        List<ApplicationDsCreateParam> result = new ArrayList<>();
        for (int i = 0; i < sourceList.size(); i++) {
            List<String> propertyList = sourceList.get(i);
            if (CollectionUtils.isEmpty(propertyList)) {
                continue;
            }
            ApplicationDsCreateParam createParam = new ApplicationDsCreateParam();
            createParam.setDbType(Integer.valueOf(propertyList.get(0)));
            createParam.setDsType(Integer.valueOf(propertyList.get(1)));
            createParam.setUrl(propertyList.get(2));
            createParam.setConfig(propertyList.get(3));
            createParam.setParseConfig(propertyList.get(4));
            createParam.setStatus(Integer.valueOf(propertyList.get(5)));
            result.add(createParam);
        }

        return result;
    }

    @Override
    public List<LinkGuardVo> convertGuardSheet(ArrayList<ArrayList<String>> sourceList) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return null;
        }
        List<LinkGuardVo> result = new ArrayList<>();
        for (ArrayList<String> arrayList : sourceList) {
            if (CollectionUtils.isEmpty(arrayList)) {
                continue;
            }
            LinkGuardVo guardVo = new LinkGuardVo();
            guardVo.setMethodInfo(arrayList.get(0));
            guardVo.setGroovy(arrayList.get(1));
            guardVo.setIsEnable(Boolean.valueOf(arrayList.get(2)));
            if (arrayList.size() > 3) {
                guardVo.setRemark(String.valueOf(arrayList.get(3)));
            } else {
                guardVo.setRemark("");
            }
            result.add(guardVo);
        }
        return result;
    }

    @Override
    public List<TShadowJobConfig> convertJobSheet(ArrayList<ArrayList<String>> sourceList) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return null;
        }
        List<TShadowJobConfig> result = new ArrayList<>();
        for (int i = 0; i < sourceList.size(); i++) {
            ArrayList<String> arrayList = sourceList.get(i);
            if (CollectionUtils.isEmpty(arrayList)) {
                continue;
            }
            TShadowJobConfig job = new TShadowJobConfig();
            job.setName(arrayList.get(0));
            job.setType(Integer.valueOf(arrayList.get(1)));
            job.setConfigCode(arrayList.get(2));
            job.setStatus(Integer.valueOf(arrayList.get(3)));
            job.setActive(Integer.valueOf(arrayList.get(4)));
            if (arrayList.size() > 5) {
                job.setRemark(String.valueOf(arrayList.get(5)));
            } else {
                job.setRemark("");
            }
            result.add(job);
        }
        return result;
    }

    @Override
    public List<WhitelistImportFromExcelInput> converWhiteList(ArrayList<ArrayList<String>> importWhiteLists) {
        if (CollectionUtils.isEmpty(importWhiteLists)) {
            return null;
        }
        List<WhitelistImportFromExcelInput> result = new ArrayList<>();
        for (ArrayList<String> arrayList : importWhiteLists) {
            if (CollectionUtils.isEmpty(arrayList)) {
                continue;
            }
            WhitelistImportFromExcelInput input = new WhitelistImportFromExcelInput();
            // 白名单校验
            if (WhitelistUtil.checkWhiteFormatError(arrayList.get(0), number)) {
                continue;
            }
            input.setInterfaceName(arrayList.get(0).trim());
            input.setInterfaceType(Integer.valueOf(arrayList.get(1)));
            input.setDictType(arrayList.get(2));
            input.setUseYn(Integer.valueOf(arrayList.get(3)));
            input.setIsGlobal(BooleanEnum.getByDesc(arrayList.get(4)));
            input.setIsHandwork(BooleanEnum.getByDesc(arrayList.get(5)));
            if (arrayList.size() == 7) {
                input.setEffectAppNames(StringUtil.isNotEmpty(arrayList.get(6)) ?
                    Lists.newArrayList(arrayList.get(6).split(",")) : Lists.newArrayList());
            } else {
                input.setEffectAppNames(Lists.newArrayList());
            }
            result.add(input);
        }

        return result;
    }

    @Override
    public List<BlacklistCreateNewParam> converBlackList(ArrayList<ArrayList<String>> sourceList, Long applicationId) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }
        ApplicationDetailResult result = applicationDAO.getApplicationById(applicationId);
        List<BlacklistCreateNewParam> params = Lists.newArrayList();
        for (ArrayList<String> arrayList : sourceList) {
            if (CollectionUtils.isEmpty(arrayList)) {
                continue;
            }
            if (arrayList.size() != AppConfigSheetEnum.BLACK.getColumnNum()) {
                throw new RuntimeException("黑名单数据列不正确，请确认！导入的excel需要时从控制台导出的配置文件。异常数据：" + arrayList.toString());
            }
            BlacklistCreateNewParam newParam = new BlacklistCreateNewParam();
            newParam.setRedisKey(String.valueOf(arrayList.get(0)).trim());
            newParam.setType(0);
            newParam.setUseYn(Integer.valueOf(arrayList.get(1)));
            newParam.setApplicationId(applicationId);
            if (result != null) {
                newParam.setTenantId(result.getTenantId());
                newParam.setUserId(result.getUserId());
            }
            params.add(newParam);
        }
        return params;
    }

    @Override
    public List<ShadowConsumerCreateInput> converComsumerList(ArrayList<ArrayList<String>> sourceList) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }
        List<ShadowConsumerCreateInput> result = new ArrayList<>();
        for (ArrayList<String> arrayList : sourceList) {
            if (CollectionUtils.isEmpty(arrayList)) {
                continue;
            }
            if (arrayList.size() != 3) {
                throw new RuntimeException("影子消费者数据列不正确，请确认！导入的excel需要时从控制台导出的配置文件。异常数据：" + arrayList.toString());
            }
            ShadowConsumerCreateInput request = new ShadowConsumerCreateInput();
            request.setTopicGroup(arrayList.get(0));
            request.setType(ShadowMqConsumerType.of(arrayList.get(1)));
            request.setStatus(Integer.valueOf(arrayList.get(2)));
            result.add(request);
        }
        return result;
    }

    @Override
    public List<AppRemoteCallUpdateParam> converRemoteCall(ArrayList<ArrayList<String>> sourceList, Long applicationId) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return null;
        }
        ApplicationDetailResult applicationDetailResult = applicationDAO.getApplicationById(applicationId);
        if (applicationDetailResult == null) {
            return null;
        }
        AppRemoteCallQueryParam param = new AppRemoteCallQueryParam();
        param.setApplicationId(applicationId);
        List<AppRemoteCallResult> results = appRemoteCallDAO.getList(param);
        Map<String, List<AppRemoteCallResult>> map = results.stream().collect(Collectors.groupingBy(result ->
            RemoteCallUtils.buildImportRemoteCallName(result.getInterfaceName(), result.getInterfaceType())));
        List<AppRemoteCallUpdateParam> result = Lists.newArrayList();
        for (ArrayList<String> arrayList : sourceList) {
            if (CollectionUtils.isEmpty(arrayList)) {
                continue;
            }
            AppRemoteCallUpdateParam input = new AppRemoteCallUpdateParam();
            input.setInterfaceName(arrayList.get(0));
            input.setInterfaceType(Integer.valueOf(arrayList.get(1)));
            input.setServerAppName(arrayList.get(2).equals(Constants.NULL_SIGN) ? "" : arrayList.get(2));
            input.setType(Integer.valueOf(arrayList.get(3)));
            input.setMockReturnValue(arrayList.get(4).equals(Constants.NULL_SIGN) ? "" : arrayList.get(4));
            input.setApplicationId(applicationId);
            input.setAppName(applicationDetailResult.getApplicationName());
            input.setTenantId(applicationDetailResult.getTenantId());
            input.setUserId(applicationDetailResult.getUserId());
            input.setGmtCreate(new Date());
            String buildImportRemoteCallName = RemoteCallUtils.buildImportRemoteCallName(input.getInterfaceName(), input.getInterfaceType());
            List<AppRemoteCallResult> callResults = map.get(buildImportRemoteCallName);
            if (CollectionUtils.isNotEmpty(callResults)) {
                AppRemoteCallResult appRemoteCallResult = callResults.get(0);
                input.setId(appRemoteCallResult.getId());
            }
            result.add(input);
        }
        return result;
    }
}
