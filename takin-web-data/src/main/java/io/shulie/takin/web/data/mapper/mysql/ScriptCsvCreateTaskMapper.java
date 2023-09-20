package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.ScriptCsvCreateTaskEntity;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ScriptCsvCreateTaskMapper extends BaseMapper<ScriptCsvCreateTaskEntity> {
    int updateBatch(List<ScriptCsvCreateTaskEntity> list);

    int updateBatchSelective(List<ScriptCsvCreateTaskEntity> list);

    int batchInsert(@Param("list") List<ScriptCsvCreateTaskEntity> list);

    int insertOrUpdate(ScriptCsvCreateTaskEntity record);

    int insertOrUpdateSelective(ScriptCsvCreateTaskEntity record);

    int deleteByPrimaryKey(Long id);

    int insert(ScriptCsvCreateTaskEntity record);

    int insertSelective(ScriptCsvCreateTaskEntity record);

    ScriptCsvCreateTaskEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ScriptCsvCreateTaskEntity record);

    int updateByPrimaryKey(ScriptCsvCreateTaskEntity record);
}