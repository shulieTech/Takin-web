package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.ScriptCsvDataSetEntity;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ScriptCsvDataSetMapper extends BaseMapper<ScriptCsvDataSetEntity> {
    int updateBatch(List<ScriptCsvDataSetEntity> list);

    int updateBatchSelective(List<ScriptCsvDataSetEntity> list);

    int batchInsert(@Param("list") List<ScriptCsvDataSetEntity> list);

    int insertOrUpdate(ScriptCsvDataSetEntity record);

    int insertOrUpdateSelective(ScriptCsvDataSetEntity record);

    int deleteByPrimaryKey(Long id);

    int insert(ScriptCsvDataSetEntity record);

    int insertSelective(ScriptCsvDataSetEntity record);

    ScriptCsvDataSetEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ScriptCsvDataSetEntity record);

    int updateByPrimaryKey(ScriptCsvDataSetEntity record);

    List<ScriptCsvDataSetEntity> listByBusinessFlowId(@Param("businessFlowId") Long businessFlowId);
}