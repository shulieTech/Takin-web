package io.shulie.takin.web.data.mapper.mysql;

import org.springframework.data.repository.query.Param;

public interface UserMapper {
    Long getIdByName(@Param("userName") String userName);
}
