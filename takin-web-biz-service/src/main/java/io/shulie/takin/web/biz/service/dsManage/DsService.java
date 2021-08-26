package io.shulie.takin.web.biz.service.dsManage;

import java.util.List;

import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import com.pamirs.takin.entity.domain.entity.DsModelWithBLOBs;
import com.pamirs.takin.entity.domain.entity.simplify.AppBusinessTableInfo;
import com.pamirs.takin.entity.domain.query.agent.AppBusinessTableQuery;
import com.pamirs.takin.entity.domain.vo.dsmanage.DsAgentVO;
import com.pamirs.takin.entity.domain.vo.dsmanage.DsServerVO;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsCreateInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsDeleteInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsEnableInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationDsDetailOutput;
import io.shulie.takin.web.biz.pojo.output.application.ShadowServerConfigurationOutput;
import io.shulie.takin.web.common.common.Response;

/**
 * @author fanxx
 * @date 2020/3/12 下午3:35
 */
public interface DsService {

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
}
