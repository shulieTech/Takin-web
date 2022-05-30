package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.adapter.api.model.response.cloud.resources.Resource;
import io.shulie.takin.web.data.model.mysql.ActivityNodeState;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface CloudResourcesMapper extends BaseMapper<Resource> {
    Map getResourceStatus(@Param("resourceId") String resourceId);
}
