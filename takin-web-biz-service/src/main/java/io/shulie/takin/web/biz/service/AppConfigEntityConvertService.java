package io.shulie.takin.web.biz.service;

import java.util.ArrayList;
import java.util.List;

import com.pamirs.takin.entity.domain.entity.simplify.TShadowJobConfig;
import com.pamirs.takin.entity.domain.vo.guardmanage.LinkGuardVo;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerCreateInput;
import io.shulie.takin.web.biz.pojo.input.whitelist.WhitelistImportFromExcelInput;
import io.shulie.takin.web.data.param.application.AppRemoteCallUpdateParam;
import io.shulie.takin.web.data.param.application.ApplicationDsCreateParam;
import io.shulie.takin.web.data.param.blacklist.BlacklistCreateNewParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;

/**
 * @author mubai
 * @date 2021-03-01 11:00
 */
public interface AppConfigEntityConvertService {

    List<ApplicationDsCreateParam> convertDsSheet(ArrayList<ArrayList<String>> sourceList);

    List<LinkGuardVo> convertGuardSheet(ArrayList<ArrayList<String>> sourceList);

    List<TShadowJobConfig> convertJobSheet(ArrayList<ArrayList<String>> sourceList);

    List<WhitelistImportFromExcelInput> converWhiteList(ArrayList<ArrayList<String>> sourceList);

    /**
     * 转换黑名单
     *
     * @return
     */
    List<BlacklistCreateNewParam> converBlackList(ArrayList<ArrayList<String>> sourceList, Long applicationId);

    List<ShadowConsumerCreateInput> converComsumerList(ArrayList<ArrayList<String>> sourceList);

    /**
     * 远程调用转化
     *
     * @return
     */
    List<AppRemoteCallUpdateParam> converRemoteCall(ArrayList<ArrayList<String>> sourceList, ApplicationDetailResult detailResult);
}
