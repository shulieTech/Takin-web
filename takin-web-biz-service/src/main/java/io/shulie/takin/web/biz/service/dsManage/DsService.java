package io.shulie.takin.web.biz.service.dsManage;

import java.util.List;

import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import com.pamirs.takin.entity.domain.entity.DsModelWithBLOBs;
import com.pamirs.takin.entity.domain.entity.simplify.AppBusinessTableInfo;
import com.pamirs.takin.entity.domain.query.agent.AppBusinessTableQuery;
import com.pamirs.takin.entity.domain.vo.dsmanage.DsAgentVO;
import com.pamirs.takin.entity.domain.vo.dsmanage.DsServerVO;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.biz.convert.db.parser.ShadowTemplateSelect;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsCreateInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsDeleteInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsEnableInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsCreateInputV2;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInputV2;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationDsDetailOutput;
import io.shulie.takin.web.biz.pojo.output.application.ShadowServerConfigurationOutput;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationDsV2Response;
import io.shulie.takin.web.common.common.Response;

/**
 * @author fanxx
 * @date 2020/3/12 下午3:35
 */
public interface DsService {
    ShadowTemplateSelect processSelect(String appName);

    String parseShadowDbUrl(String config);

    Response dsAdd(ApplicationDsCreateInput createRequest);

    Response dsUpdate(ApplicationDsUpdateInput updateRequest);

    Response dsQuery(Long applicationId);

    /**
     * 影子详情
     *
     * @param dsId         dsId
     * @param isOldVersion 是否是老版本
     * @return 影子详情示例
     */
    Response<ApplicationDsDetailOutput> dsQueryDetail(Long dsId, boolean isOldVersion);

    Response enableConfig(ApplicationDsEnableInput enableRequest);

    Response dsDelete(ApplicationDsDeleteInput dsDeleteRequest);

    List<DsAgentVO> getConfigs(String appName);

    List<ShadowServerConfigurationOutput> getShadowServerConfigs(String appName);

    void addBusiness(AppBusinessTableInfo info);

    Response queryPageBusiness(AppBusinessTableQuery query);

    List<DsModelWithBLOBs> getAllEnabledDbConfig(Long applicationId);

    /**
     * 安全初始化
     *
     * @return
     */
    Response secureInit();

    /**
     * 查询影子配置数据
     *
     * @param namespace         命令空间
     * @param shadowHbaseServer 影子枚举类型
     * @return
     */
    List<DsServerVO> getShadowDsServerConfigs(String namespace, DsTypeEnum shadowHbaseServer);

    List<ApplicationDsV2Response> dsQueryV2(Long applicationId);

    Response dsQueryDetailV2(Long applicationId, Long id, String middlewareType, Boolean isNewData);

    /**
     * 查询中间件支持的隔离方案
     *
     * @param middlewareType 中间件类型
     * @param engName        中间件英文名
     * @return
     */
    List<SelectVO> queryDsType(String middlewareType, String engName);

    Response dsUpdateConfig(ApplicationDsUpdateInputV2 updateRequestV2);

    Response dsQueryConfigTemplate(String agentSourceType, Integer dsType,
                                   Boolean isNewData, String cacheType, String connectionPool, String applicationName);

    /**
     * 删除
     *
     * @param id
     * @param middlewareType
     * @param isNewData
     * @return
     */
    Response dsDeleteV2(Long id, String middlewareType, Boolean isNewData, Long applicationId);

    Response dsCreateConfig(ApplicationDsCreateInputV2 createRequestV2);

    /**
     * 获取中间件支持的版本
     *
     * @param middlewareType
     * @return
     */
    List<SelectVO> querySupperName(String middlewareType);

    /**
     * 获取缓存支持的模式
     *
     * @return
     */
    List<SelectVO> queryCacheType();

    Response enableConfigV2(Long id, String middlewareType, Boolean isNewData, Long applicationId, Integer status);
}
