package io.shulie.takin.web.biz.service;

import io.shulie.takin.web.data.dao.UserDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDAO userDAO;

    @Override
    public Long getIdByName(String userName) {
        return userDAO.getIdByName(userName);
    }
}
