package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.FileManageEntity;

import java.util.List;

public interface FileManageMapper extends BaseMapper<FileManageEntity> {

    int updateBatch(List<FileManageEntity> list);
}