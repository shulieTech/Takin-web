package io.shulie.takin.web.biz.service.linkmanage;

import java.util.List;

import com.pamirs.takin.entity.domain.dto.EntranceSimpleDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.*;
import com.pamirs.takin.entity.domain.dto.linkmanage.linkstatistics.LinkHistoryInfoDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.linkstatistics.LinkRemarkDto;
import com.pamirs.takin.entity.domain.entity.linkmanage.statistics.StatisticsQueryVo;
import com.pamirs.takin.entity.domain.vo.linkmanage.BusinessFlowVo;
import com.pamirs.takin.entity.domain.vo.linkmanage.MiddleWareEntity;
import com.pamirs.takin.entity.domain.vo.linkmanage.queryparam.BusinessQueryVo;
import com.pamirs.takin.entity.domain.vo.linkmanage.queryparam.SceneQueryVo;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.response.linkmanage.*;
import io.shulie.takin.web.common.common.Response;

/**
 * @author vernon
 * @date 2019/11/29 14:43
 */
public interface LinkManageService {
    /////////////技术链路
    //Result 使用业务中台的返回
    //从图数据库拉取数据 入参：入口名字，出参：Json

    TechLinkResponse fetchLink(String applicationName, String entrance);

    TopologicalGraphVo fetchGraph(String body);

    /**
     * 业务链路查询
     *
     * @param vo 入参
     * @return -
     */
    Response getBussisnessLinks(BusinessQueryVo vo);

    /**
     * 场景删除接口
     *
     * @param sceneId 场景名集合
     * @return -
     */
    Response deleteScene(String sceneId);

    /**
     * 场景查询接口
     *
     * @param vo 入参
     * @return -
     */
    Response<List<SceneDto>> getScenes(SceneQueryVo vo);

    Response getMiddleWareInfo(StatisticsQueryVo vo);

    LinkRemarkDto getstatisticsInfo();

    LinkHistoryInfoDto getChart();

    List<MiddleWareEntity> getAllMiddleWareTypeList();

    List<SystemProcessIdAndNameDto> ggetAllSystemProcess(String systemProcessName);

    List<SystemProcessIdAndNameDto> getAllSystemProcessCanrelateBusiness(String systemProcessName);

    List<String> entranceFuzzSerach(String entrance);

    /**
     * 根据业务活动名称, 查询登录用户下
     * 所有业务链路的名称和id
     *
     * @param businessActiveName 业务活动名称
     * @return 所有业务链路的名称和id
     */
    List<BusinessActiveIdAndNameDto> businessActiveNameFuzzSearch(String businessActiveName);

    BusinessLinkResponse getBussisnessLinkDetail(String id);

    List<MiddleWareEntity> businessProcessMiddleWares(List<String> ids);

    BusinessFlowDto getBusinessFlowDetail(String id);

    void modifyBusinessFlow(BusinessFlowVo vo) throws Exception;

    void addBusinessFlow(BusinessFlowVo vo) throws Exception;

    List<BusinessFlowIdAndNameDto> businessFlowIdFuzzSearch(String businessFlowName) throws Exception;

    List<MiddleWareNameDto> cascadeMiddleWareNameAndVersion(String middleWareType) throws Exception;

    List<MiddleWareNameDto> getDistinctMiddleWareName();

    List<EntranceSimpleDto> getEntranceByAppName(String applicationName);

    List<MiddleWareResponse> getMiddleWareResponses(String applicationName);

    /**
     * 根据业务流程id查询业务活动id和名称
     *
     * @param businessFlowId 业务流程主键
     * @return 业务活动id和名称
     */
    List<BusinessActivityNameResponse> getBusinessActiveByFlowId(Long businessFlowId);

    BusinessFlowDetailResponse parseScriptAndSave(BusinessFlowParseRequest businessFlowParseRequest);

    BusinessFlowDetailResponse getBusinessFlowDetail(Long id);

    BusinessFlowDetailResponse uploadDataFile(BusinessFlowDataFileRequest businessFlowDataFileRequest);

    BusinessFlowDetailResponse getThreadGroupDetail(Long id, String xpathMd5);

    BusinessFlowMatchResponse autoMatchActivity(Long id);
}
