package io.shulie.takin.web.data.dao.script.impl;

import java.util.Arrays;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.common.enums.script.ScriptDebugStatusEnum;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.common.vo.script.ScriptDeployFinishDebugVO;
import io.shulie.takin.web.data.dao.script.ScriptDebugDAO;
import io.shulie.takin.web.data.mapper.mysql.ScriptDebugMapper;
import io.shulie.takin.web.data.model.mysql.ScriptDebugEntity;
import io.shulie.takin.web.data.param.scriptmanage.PageScriptDebugParam;
import io.shulie.takin.web.data.param.scriptmanage.SaveOrUpdateScriptDebugParam;
import io.shulie.takin.web.data.result.scriptmanage.ScriptDebugListResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Service;

/**
 * 脚本调试表(ScriptDebug)表数据库 dao
 *
 * @author liuchuan
 * @since 2021-05-10 16:58:55
 */
@Service
public class ScriptDebugDAOImpl
    extends ServiceImpl<ScriptDebugMapper, ScriptDebugEntity>
    implements ScriptDebugDAO, MPUtil<ScriptDebugEntity> {

    @Override
    public boolean hasUnfinished(Long scriptDeployId) {
        // 还有不是成功或者失败的, (调试完成的), 就是还有未完成的
        return this.count(this.unfinishedWrapper(scriptDeployId)) > 0;
    }

    @Override
    public IPage<ScriptDebugEntity> pageByScriptDeployIdAndStatusList(PageScriptDebugParam pageScriptDebugParam) {
        return this.page(this.setPage(pageScriptDebugParam),
            this.getLambdaQueryWrapper().select(ScriptDebugEntity::getId, ScriptDebugEntity::getCreatedAt, ScriptDebugEntity::getRemark,
                ScriptDebugEntity::getStatus, ScriptDebugEntity::getRequestNum, ScriptDebugEntity::getCloudReportId)
                .eq(ScriptDebugEntity::getScriptDeployId, pageScriptDebugParam.getScriptDeployId())
                .in(ScriptDebugEntity::getStatus, pageScriptDebugParam.getStatusList())
                .orderByDesc(ScriptDebugEntity::getId));
    }

    @Override
    public List<ScriptDeployFinishDebugVO> listScriptDeployFinishDebugResult(List<Long> scriptDeployIds) {
        return this.getBaseMapper().selectScriptDeployFinishDebugResultList(scriptDeployIds);
    }

    @Override
    public List<ScriptDebugListResult> listUnfinished(Long scriptDeployId) {
        return DataTransformUtil.list2list(this.list(this.unfinishedWrapper(scriptDeployId)
            .select(ScriptDebugEntity::getId, ScriptDebugEntity::getCloudSceneId)), ScriptDebugListResult.class);
    }

    /**
     * 未完成的条件
     *
     * @param scriptDeployId 脚本实例id
     * @return 未完成的条件
     */
    private LambdaQueryWrapper<ScriptDebugEntity> unfinishedWrapper(Long scriptDeployId) {
        return this.getLambdaQueryWrapper().eq(ScriptDebugEntity::getScriptDeployId, scriptDeployId)
            .notIn(ScriptDebugEntity::getStatus, Arrays.asList(ScriptDebugStatusEnum.SUCCESS.getCode(),
                ScriptDebugStatusEnum.FAILED.getCode()));
    }

    @Override
    public boolean updateById(SaveOrUpdateScriptDebugParam saveOrUpdateScriptDebugParam) {
        return this.updateById(BeanUtil.copyProperties(saveOrUpdateScriptDebugParam, ScriptDebugEntity.class));
    }

}
