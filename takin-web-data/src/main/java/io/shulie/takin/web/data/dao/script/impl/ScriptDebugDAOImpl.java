package io.shulie.takin.web.data.dao.script.impl;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.common.enums.script.ScriptDebugStatusEnum;
import io.shulie.takin.web.common.vo.script.ScriptDeployFinishDebugVO;
import io.shulie.takin.web.data.dao.script.ScriptDebugDAO;
import io.shulie.takin.web.data.mapper.mysql.ScriptDebugMapper;
import io.shulie.takin.web.data.model.mysql.ScriptDebugEntity;
import io.shulie.takin.web.data.param.scriptmanage.PageScriptDebugParam;
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
        return this.count(this.getLambdaQueryWrapper()
            .eq(ScriptDebugEntity::getScriptDeployId, scriptDeployId)
            .notIn(ScriptDebugEntity::getStatus, Arrays.asList(ScriptDebugStatusEnum.SUCCESS.getCode(),
                ScriptDebugStatusEnum.FAILED.getCode()))) > 0;
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

}
