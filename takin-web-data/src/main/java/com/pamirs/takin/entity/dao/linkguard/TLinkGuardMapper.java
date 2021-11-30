package com.pamirs.takin.entity.dao.linkguard;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.LinkGuardEntity;
import com.pamirs.takin.entity.domain.query.LinkGuardQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 慕白
 * @date 2020-03-05 10:16
 */

@Mapper
public interface TLinkGuardMapper {

    long insertSelective(LinkGuardEntity guardEntity);

    void update(LinkGuardEntity guardEntity);

    List<LinkGuardEntity> selectByExample(@Param("param") LinkGuardQueryParam param,@Param("userIds") List<Long> userIds);

    Long selectCountByExample(@Param("param") LinkGuardQueryParam param);

    void deleteById(Long id);

    Long countGuardNum();

    LinkGuardEntity selectById(Long id);

    List<LinkGuardEntity> getAllEnabledGuard(@Param("applicationId") String applicationId);
}
