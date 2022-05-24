package io.shulie.takin.web.data.dao;

import io.shulie.takin.web.data.mapper.mysql.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDAOImpl implements UserDAO {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Long getIdByName(String userName) {
        return userMapper.getIdByName(userName);
    }
}
