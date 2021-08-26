package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import io.shulie.takin.web.common.vo.script.ScriptDeployFinishDebugVO;
import io.shulie.takin.web.data.model.mysql.ScriptDebugEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 脚本调试表(ScriptDebug)表数据库 mapper
 *
 * @author liuchuan
 * @since 2021-05-10 16:58:11
 */
public interface ScriptDebugMapper extends BaseMapper<ScriptDebugEntity> {

    /**
     * 根据脚本发布ids, 查询对应的调试是否结束
     *
     * @param scriptDeployIds 脚本发布ids
     * @return 调试结束结果列表
     */
    List<ScriptDeployFinishDebugVO> selectScriptDeployFinishDebugResultList(@Param("scriptDeployIds") List<Long> scriptDeployIds);

}
