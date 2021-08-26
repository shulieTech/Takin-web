package io.shulie.takin.web.data.dao.leakverify;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.shulie.takin.web.amdb.api.DataLeakAnalysisClient;
import io.shulie.takin.web.amdb.bean.result.leakverify.LeakVerifyDeployDTO;
import io.shulie.takin.web.amdb.bean.result.leakverify.LeakVerifyDeployDetailDTO;
import io.shulie.takin.web.data.converter.leakverify.LeakVerifyDeployConverter;
import io.shulie.takin.web.data.mapper.mysql.LeakVerifyDeployDetailMapper;
import io.shulie.takin.web.data.mapper.mysql.LeakVerifyDeployMapper;
import io.shulie.takin.web.data.model.mysql.LeakVerifyDeploy;
import io.shulie.takin.web.data.model.mysql.LeakVerifyDeployDetail;
import io.shulie.takin.web.data.result.leakverify.LeakVerifyDeployDetailResult;
import io.shulie.takin.web.data.result.leakverify.LeakVerifyDeployResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhaoyong
 */
@Component
@Deprecated
public class LeakVerifyDeployDAOImpl implements LeakVerifyDeployDAO{

    @Autowired
    private LeakVerifyDeployMapper leakVerifyDeployMapper;

    @Autowired
    private LeakVerifyDeployDetailMapper leakVerifyDeployDetailMapper;

    @Autowired
    private DataLeakAnalysisClient dataLeakAnalysisClient;


    @Override
    public List<LeakVerifyDeployResult> listLeakVerifyDeploy(Long leakVerifyId, boolean isCurrent) {
        if (isCurrent){
            List<LeakVerifyDeployDTO> dataLeakLists = dataLeakAnalysisClient.getDataLeakLists(leakVerifyId);
            return LeakVerifyDeployConverter.INSTANCE.ofListLeakVerifyDeployResultByDTO(dataLeakLists);
        }
        LambdaQueryWrapper<LeakVerifyDeploy> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
                LeakVerifyDeploy::getId,
                LeakVerifyDeploy::getApplicationName,
                LeakVerifyDeploy::getBizLeakCount,
                LeakVerifyDeploy::getBizRequestCount,
                LeakVerifyDeploy::getEntryName,
                LeakVerifyDeploy::getGmtCreate,
                LeakVerifyDeploy::getGmtUpdate,
                LeakVerifyDeploy::getIsDeleted,
                LeakVerifyDeploy::getLeakVerifyId,
                LeakVerifyDeploy::getPressureLeakCount,
                LeakVerifyDeploy::getPressureRequestCount);
        wrapper.eq(LeakVerifyDeploy::getLeakVerifyId,leakVerifyId);
        List<LeakVerifyDeploy> leakVerifyDeploys = leakVerifyDeployMapper.selectList(wrapper);
        return LeakVerifyDeployConverter.INSTANCE.ofListLeakVerifyDeployResult(leakVerifyDeploys);
    }

    @Override
    public List<LeakVerifyDeployDetailResult> queryLeakVerifyDeployDetail(Long leakVerifyDeployId, Long leakVerifyId, String applicationName, String entryName) {
        //为空查询实况数据
        if (leakVerifyDeployId == null){
            List<LeakVerifyDeployDetailDTO> dataLeakDetails = dataLeakAnalysisClient.getDataLeakDetails(leakVerifyId, applicationName, entryName);
            return LeakVerifyDeployConverter.INSTANCE.ofListLeakVerifyDeployDetailResultByDTO(dataLeakDetails);
        }
        LambdaQueryWrapper<LeakVerifyDeployDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(
                LeakVerifyDeployDetail::getId,
                LeakVerifyDeployDetail::getFeature,
                LeakVerifyDeployDetail::getGmtCreate,
                LeakVerifyDeployDetail::getGmtUpdate,
                LeakVerifyDeployDetail::getLeakContent,
                LeakVerifyDeployDetail::getLeakCount,
                LeakVerifyDeployDetail::getLeakType,
                LeakVerifyDeployDetail::getLeakVerifyDeployId);
        wrapper.eq(LeakVerifyDeployDetail::getLeakVerifyDeployId,leakVerifyDeployId);
        List<LeakVerifyDeployDetail> leakVerifyDeployDetails = leakVerifyDeployDetailMapper.selectList(wrapper);
        return LeakVerifyDeployConverter.INSTANCE.ofListLeakVerifyDeployDetailResult(leakVerifyDeployDetails);
    }
}
