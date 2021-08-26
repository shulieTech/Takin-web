package com.pamirs.takin.entity.dao.settle;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.settle.AccountBook;
import org.apache.ibatis.annotations.Param;

public interface TAccountBookMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(AccountBook record);

    AccountBook selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AccountBook record);

    AccountBook selectOneByUserId(@Param("userId") Long userId);

    List<AccountBook> selectByUserIds(@Param("userIds") List<Long> userIds);
}
