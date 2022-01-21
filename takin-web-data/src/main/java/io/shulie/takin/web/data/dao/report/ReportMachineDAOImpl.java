package io.shulie.takin.web.data.dao.report;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.data.mapper.mysql.ReportMachineMapper;
import io.shulie.takin.web.data.model.mysql.ReportMachineEntity;
import io.shulie.takin.web.data.param.report.ReportLocalQueryParam;
import io.shulie.takin.web.data.param.report.ReportMachineUpdateParam;
import io.shulie.takin.web.data.result.report.ReportMachineResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.data.dao.report
 * @ClassName: ReportMachineDao
 * @Description: TODO
 * @Date: 2021/11/23 10:13
 */
@Component
public class ReportMachineDAOImpl  extends ServiceImpl<ReportMachineMapper, ReportMachineEntity>
    implements ReportMachineDAO, MPUtil<ReportMachineEntity> {
    @Override
    public void insert(ReportMachineUpdateParam param) {
        ReportMachineEntity entity = new ReportMachineEntity();
        BeanUtils.copyProperties(param,entity);
        this.save(entity);
    }

    /**
     *   on duplicate key update machine_base_config=#{machineBaseConfig,jdbcType=VARCHAR},
     *                                  agent_id=#{agentId,jdbcType=VARCHAR},
     *                                  risk_value=#{riskValue,jdbcType=DECIMAL},
     *                                  tenant_id=#{tenantId,jdbcType=BIGINT},
     *                                  env_code=#{envCode,jdbcType=VARCHAR}
     * @param param
     */
    @Override
    public void insertOrUpdate(ReportMachineUpdateParam param) {
        // 先查询
        LambdaQueryWrapper<ReportMachineEntity> queryWrapper = this.getLambdaQueryWrapper();
        queryWrapper.eq(ReportMachineEntity::getReportId,param.getReportId());
        queryWrapper.eq(ReportMachineEntity::getMachineIp,param.getMachineIp());
        queryWrapper.eq(ReportMachineEntity::getApplicationName,param.getApplicationName());
        ReportMachineEntity one = this.getOne(queryWrapper);
        if(one != null) {
            one.setMachineBaseConfig(param.getMachineBaseConfig());
            one.setAgentId(param.getAgentId());
            one.setRiskValue(param.getRiskValue());
            this.updateById(one);
        }else {
            this.insert(param);
        }

    }

    /**
     * select
     *         <include refid="Simple_Column_List"/>
     *         from t_report_machine
     *         where report_id = #{reportId,jdbcType=BIGINT}
     *         <if test="applicationName != null">
     *             and application_name = #{applicationName,jdbcType=VARCHAR}
     *         </if>
     *         <if test="riskFlag != null">
     *             and risk_flag = #{riskFlag,jdbcType=TINYINT}
     *         </if>
     *         order by risk_value desc
     * @param queryParam
     * @return
     */
    @Override
    public List<ReportMachineResult> selectSimpleByExample(ReportLocalQueryParam queryParam) {
        LambdaQueryWrapper<ReportMachineEntity> queryWrapper = this.getReportMachineEntityLambdaQueryWrapper(queryParam);
        List<ReportMachineEntity> list = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        return DataTransformUtil.list2list(list,ReportMachineResult.class);
    }

    /**
     *  select
     *         <include refid="Base_Column_List"/>
     *         from t_report_machine
     *         where report_id = #{reportId,jdbcType=BIGINT}
     *         and application_name = #{applicationName,jdbcType=VARCHAR}
     *         and machine_ip = #{machineIp,jdbcType=VARCHAR}
     *         limit 1
     * @param queryParam
     * @return
     */
    @Override
    public ReportMachineResult selectOneByParam(ReportLocalQueryParam queryParam) {
        LambdaQueryWrapper<ReportMachineEntity> queryWrapper = this.getReportMachineEntityLambdaQueryWrapper(queryParam);
        List<ReportMachineEntity> list = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(list)) {
            return null;
        }
        return DataTransformUtil.list2list(list,ReportMachineResult.class).get(0);
    }

    /**
     * select
     *         <include refid="Base_Column_List"/>
     *         from t_report_machine
     *         where report_id = #{reportId,jdbcType=BIGINT}
     *         and application_name = #{applicationName,jdbcType=VARCHAR}
     * @param queryParam
     * @return
     */

    @Override
    public List<ReportMachineResult> selectListByParam(ReportLocalQueryParam queryParam) {
        LambdaQueryWrapper<ReportMachineEntity> queryWrapper = this.getReportMachineEntityLambdaQueryWrapper(queryParam);
        List<ReportMachineEntity> list = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        return DataTransformUtil.list2list(list,ReportMachineResult.class);
    }

    /**
     * update t_report_machine
     *         set machine_tps_target_config = #{machineTpsTargetConfig,jdbcType=LONGVARCHAR}
     *         where report_id = #{reportId,jdbcType=BIGINT}
     *           and application_name = #{applicationName,jdbcType=VARCHAR}
     *           and machine_ip = #{machineIp,jdbcType=VARCHAR}
     * @param reportId
     * @return
     */
    @Override
    public List<Map<String, Object>> selectCountByReport(Long reportId) {
        return this.baseMapper.selectCountByReport(reportId);
    }

    @Override
    public int updateTpsTargetConfig(ReportMachineUpdateParam param) {
        ReportMachineEntity entity = new ReportMachineEntity();
        BeanUtils.copyProperties(param,entity);
        return this.baseMapper.updateTpsTargetConfig(entity);
    }

    @Override
    public int updateRiskContent(ReportMachineUpdateParam param) {
        ReportMachineEntity entity = new ReportMachineEntity();
        BeanUtils.copyProperties(param,entity);
        return this.baseMapper.updateRiskContent(entity);
    }

    /**
     *   delete
     *         from t_report_machine
     *         where report_id = #{reportId,jdbcType=BIGINT}
     * @param reportId
     */
    @Override
    public void deleteByReportId(Long reportId) {
        LambdaQueryWrapper<ReportMachineEntity> queryWrapper = this.getLambdaQueryWrapper();
        queryWrapper.eq(ReportMachineEntity::getReportId, reportId);
        this.remove(queryWrapper);
    }

    private LambdaQueryWrapper<ReportMachineEntity> getReportMachineEntityLambdaQueryWrapper(ReportLocalQueryParam queryParam) {
        LambdaQueryWrapper<ReportMachineEntity> queryWrapper = this.getLambdaQueryWrapper();
        queryWrapper.eq(ReportMachineEntity::getReportId, queryParam.getReportId());
        if(StringUtils.isNotBlank(queryParam.getApplicationName())) {
            queryWrapper.eq(ReportMachineEntity::getApplicationName, queryParam.getApplicationName());
        }
        if(queryParam.getRiskFlag() != null) {
            queryWrapper.eq(ReportMachineEntity::getRiskFlag, queryParam.getRiskFlag());
        }
        if(StringUtils.isNotBlank(queryParam.getMachineIp())) {
            queryWrapper.eq(ReportMachineEntity::getMachineIp, queryParam.getMachineIp());
        }
        queryWrapper.orderByDesc(ReportMachineEntity::getRiskValue);
        return queryWrapper;
    }
}
