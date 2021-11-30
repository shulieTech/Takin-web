package io.shulie.takin.web.data.mapper.mysql.dashboard;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.dashboard.QuickAccessEntity;
import org.apache.ibatis.annotations.Param;

/**
 * @author 张天赐
 */
public interface QuickAccessMapper extends BaseMapper<QuickAccessEntity> {

    List<QuickAccessEntity> queryList(@Param("tenantId") Long tenantId,@Param("envCode") String envCode);
}