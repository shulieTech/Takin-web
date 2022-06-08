package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;

import com.pamirs.takin.entity.domain.query.ApplicationApiParam;

import io.shulie.takin.web.data.model.mysql.ApplicationApiManageEntity;
import io.shulie.takin.web.data.param.application.ApplicationApiQueryParam;

public interface ApplicationApiManageMapper extends BaseMapper<ApplicationApiManageEntity> {

    int insertSelective(ApplicationApiManageEntity record);

    ApplicationApiManageEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ApplicationApiManageEntity record);

    int updateByPrimaryKey(ApplicationApiManageEntity record);

    List<ApplicationApiManageEntity> query();

    @InterceptorIgnore(tenantLine = "true")
    List<ApplicationApiManageEntity> querySimple(ApplicationApiParam apiParam);

    List<ApplicationApiManageEntity> querySimpleWithTenant(ApplicationApiParam apiParam);

    List<ApplicationApiManageEntity> selectBySelective(@Param("record") ApplicationApiQueryParam record, @Param("userIds") List<Long> userIds);

    int insertBatch(@Param("list") List<ApplicationApiManageEntity> list);

    void deleteByAppName(@Param("appName") String appName);

    ApplicationApiManageEntity queryManage(@Param("applicationName") String applicationName,@Param("method")  String method, @Param("api") String api);
}
