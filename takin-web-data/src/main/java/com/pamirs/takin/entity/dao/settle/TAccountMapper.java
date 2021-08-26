package com.pamirs.takin.entity.dao.settle;

import com.pamirs.takin.entity.domain.entity.settle.Account;

public interface TAccountMapper {

    int deleteByPrimaryKey(Long id);

    int insertSelective(Account record);

    Account selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Account record);

}
