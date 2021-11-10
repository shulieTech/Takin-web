package com.pamirs.takin.entity.dao.apimanage;

import java.util.List;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.pamirs.takin.entity.domain.entity.ApplicationApiManage;
import com.pamirs.takin.entity.domain.query.ApplicationApiParam;
import io.shulie.takin.web.common.annocation.DataAuth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TApplicationApiManageMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ApplicationApiManage record);

    int insertSelective(ApplicationApiManage record);

    ApplicationApiManage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ApplicationApiManage record);

    int updateByPrimaryKey(ApplicationApiManage record);

    List<ApplicationApiManage> query();

    @InterceptorIgnore(tenantLine = "true")
    List<ApplicationApiManage> querySimple(ApplicationApiParam apiParam);

    List<ApplicationApiManage> querySimpleWithTenant(ApplicationApiParam apiParam);

    @DataAuth()
    List<ApplicationApiManage> selectBySelective(ApplicationApiManage record);

    int insertBatch(@Param("list") List<ApplicationApiManage> list);

    void deleteByAppName(@Param("appName") String appName);
}
