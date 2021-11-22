package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.SceneLinkRelateEntity;
import org.apache.ibatis.annotations.Param;

public interface SceneLinkRelateMapper extends BaseMapper<SceneLinkRelateEntity> {

    /**
     * 查询链路个数
     * @param techLinkIds
     * @return
     */
    int countByTechLinkIds(@Param("list") List<String> techLinkIds);
}
