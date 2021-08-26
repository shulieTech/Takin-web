package com.pamirs.takin.entity.dao.settle;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.settle.AccountBalance;
import com.pamirs.takin.entity.domain.vo.settle.AccountBalanceQueryVO;

public interface TAccountBalanceMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(AccountBalance record);

    AccountBalance selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AccountBalance record);

    List<AccountBalance> getPageList(AccountBalanceQueryVO queryVO);
}
