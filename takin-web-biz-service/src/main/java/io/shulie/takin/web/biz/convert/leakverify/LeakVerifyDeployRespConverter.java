package io.shulie.takin.web.biz.convert.leakverify;

import java.util.List;

import io.shulie.takin.web.biz.pojo.output.leakverify.LeakVerifyDeployDetailOutput;
import io.shulie.takin.web.biz.pojo.output.leakverify.LeakVerifyDeployOutput;
import io.shulie.takin.web.biz.pojo.response.leakverify.LeakVerifyDeployDetailResponse;
import io.shulie.takin.web.biz.pojo.response.leakverify.LeakVerifyDeployResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhaoyong
 */
@Mapper
public interface LeakVerifyDeployRespConverter {

    LeakVerifyDeployRespConverter INSTANCE = Mappers.getMapper(LeakVerifyDeployRespConverter.class);

    /**
     * 前端返回值转换
     *
     * @param leakVerifyDeployOutputs
     * @return
     */
    List<LeakVerifyDeployResponse> ofListLeakVerifyDeployResponse(List<LeakVerifyDeployOutput> leakVerifyDeployOutputs);

    /**
     * 转换
     *
     * @param leakVerifyDeployDetailOutputs
     * @return
     */
    List<LeakVerifyDeployDetailResponse> ofListLeakVerifyDeployDetailResponse(
        List<LeakVerifyDeployDetailOutput> leakVerifyDeployDetailOutputs);
}
