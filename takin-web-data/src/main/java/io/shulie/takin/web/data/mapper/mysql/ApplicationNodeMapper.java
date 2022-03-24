package io.shulie.takin.web.data.mapper.mysql;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApplicationNodeMapper {
    List<String> getAllNodesByApplicationName(@Param("applicationName") String applicationName);
}
