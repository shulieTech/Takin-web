package io.shulie.takin.web.biz.convert.leakverify;

import java.util.ArrayList;
import java.util.List;

import io.shulie.takin.web.biz.pojo.output.leakverify.LeakVerifyDeployDetailOutput;
import io.shulie.takin.web.biz.pojo.output.leakverify.LeakVerifyDeployOutput;
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
public interface LeakVerifyDeployOutputConverter {
    LeakVerifyDeployOutputConverter INSTANCE = Mappers.getMapper(LeakVerifyDeployOutputConverter.class);

    /**
     * 列表转换
     *
     * @param leakVerifyDeployResults
     * @return
     */
    List<LeakVerifyDeployOutput> ofListLeakVerifyDeployOutput(List<LeakVerifyDeployResult> leakVerifyDeployResults);

    /**
     * 实例详情列表转换
     *
     * @param leakVerifyDeployDetailResults
     * @return
     */
    default List<LeakVerifyDeployDetailOutput> ofListLeakVerifyDeployDetailOutput(
        List<LeakVerifyDeployDetailResult> leakVerifyDeployDetailResults) {
        if (CollectionUtils.isEmpty(leakVerifyDeployDetailResults)) {
            return null;
        }
        List<LeakVerifyDeployDetailOutput> leakVerifyDeployDetailOutputs = new ArrayList<>();
        for (LeakVerifyDeployDetailResult leakVerifyDeployDetailResult : leakVerifyDeployDetailResults) {
            LeakVerifyDeployDetailOutput leakVerifyDeployDetailOutput = ofLeakVerifyDeployDetailOutput(
                leakVerifyDeployDetailResult);
            String leakContent = getLeakContent(leakVerifyDeployDetailResult.getLeakObjectResults());
            leakVerifyDeployDetailOutput.setLeakContent(leakContent);
            leakVerifyDeployDetailOutputs.add(leakVerifyDeployDetailOutput);
        }
        return leakVerifyDeployDetailOutputs;
    }

    /**
     * 实例转换
     *
     * @param leakVerifyDeployDetailResult
     * @return
     */
    @Mapping(target = "leakContent", source = "")
    LeakVerifyDeployDetailOutput ofLeakVerifyDeployDetailOutput(
        LeakVerifyDeployDetailResult leakVerifyDeployDetailResult);

    /**
     * 实例内容转换为前端需要的字符串
     *
     * @param leakObjectResults
     * @return
     */
    default String getLeakContent(List<LeakObjectResult> leakObjectResults) {
        if (CollectionUtils.isEmpty(leakObjectResults)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        leakObjectResults.forEach(leakObjectResult -> {
            sb.append(leakObjectResult.getName()).append("(").append(leakObjectResult.getCount()).append(")").append(
                "、");
        });
        return sb.deleteCharAt(sb.lastIndexOf(",")).toString();
    }
}
