package io.shulie.takin.web.data.converter.leakverify;

import java.util.ArrayList;
import java.util.List;

import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.amdb.bean.result.leakverify.LeakVerifyDeployDTO;
import io.shulie.takin.web.amdb.bean.result.leakverify.LeakVerifyDeployDetailDTO;
import io.shulie.takin.web.data.model.mysql.LeakVerifyDeploy;
import io.shulie.takin.web.data.model.mysql.LeakVerifyDeployDetail;
import io.shulie.takin.web.data.result.leakverify.LeakObjectResult;
import io.shulie.takin.web.data.result.leakverify.LeakVerifyDeployDetailResult;
import io.shulie.takin.web.data.result.leakverify.LeakVerifyDeployResult;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author zhaoyong
 */
@Mapper
public interface LeakVerifyDeployConverter {
    LeakVerifyDeployConverter INSTANCE = Mappers.getMapper(LeakVerifyDeployConverter.class);

    List<LeakVerifyDeployResult> ofListLeakVerifyDeployResult(List<LeakVerifyDeploy> leakVerifyDeploys);

    List<LeakVerifyDeployResult> ofListLeakVerifyDeployResultByDTO(List<LeakVerifyDeployDTO> dataLeakLists);

    /**
     * amdb数据转换为result数据
     * @param dataLeakDetails
     * @return
     */
    List<LeakVerifyDeployDetailResult> ofListLeakVerifyDeployDetailResultByDTO(List<LeakVerifyDeployDetailDTO> dataLeakDetails);

    /**
     * 数据库数据转换为result
     * @param leakVerifyDeployDetails
     * @return
     */
    default List<LeakVerifyDeployDetailResult> ofListLeakVerifyDeployDetailResult(List<LeakVerifyDeployDetail> leakVerifyDeployDetails){
        if (CollectionUtils.isEmpty(leakVerifyDeployDetails)){
            return null;
        }
        List<LeakVerifyDeployDetailResult> leakVerifyDeployDetailResults = new ArrayList<>();
        for (LeakVerifyDeployDetail leakVerifyDeployDetail : leakVerifyDeployDetails){
            LeakVerifyDeployDetailResult leakVerifyDeployDetailResult = ofLeakVerifyDeployDetailResult(leakVerifyDeployDetail);
            List<LeakObjectResult> leakObjectResults = getLeakObjectResults(leakVerifyDeployDetail.getLeakContent());
            leakVerifyDeployDetailResult.setLeakObjectResults(leakObjectResults);
            leakVerifyDeployDetailResults.add(leakVerifyDeployDetailResult);
        }
        return leakVerifyDeployDetailResults;
    }

    /**
     * 单个转换
     * @param leakVerifyDeployDetail
     * @return
     */
    @Mapping(target = "leakObjectResults", source = "")
    LeakVerifyDeployDetailResult ofLeakVerifyDeployDetailResult(LeakVerifyDeployDetail leakVerifyDeployDetail);

    /**
     * 将数据库存储的json转为对象
     * @param leakObjectResults
     * @return
     */
    default List<LeakObjectResult> getLeakObjectResults(String leakObjectResults){
        if (StringUtil.isBlank(leakObjectResults)){
            return null;
        }
        return JsonHelper.json2List(leakObjectResults,LeakObjectResult.class);
    }
}
