package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import io.shulie.takin.web.data.model.mysql.ApplicationDsDbTableEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;

/**
 * 业务数据库表(ApplicationDsDbTable)表数据库 mapper
 *
 * @author 南风
 * @date 2021-09-15 17:21:41
 */
public interface ApplicationDsDbTableMapper extends BaseMapper<ApplicationDsDbTableEntity> {
}

