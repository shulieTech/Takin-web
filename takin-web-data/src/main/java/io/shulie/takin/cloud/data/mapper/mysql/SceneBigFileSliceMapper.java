package io.shulie.takin.cloud.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.cloud.data.model.mysql.SceneBigFileSliceEntity;

/**
 * 场景大文件切片
 *
 * @author 赵勇
 */
public interface SceneBigFileSliceMapper extends BaseMapper<SceneBigFileSliceEntity> {
    /**
     * 批量更新信息
     *
     * @param list 批信息
     * @return 更新结果
     */
    int updateBatch(List<SceneBigFileSliceEntity> list);
}