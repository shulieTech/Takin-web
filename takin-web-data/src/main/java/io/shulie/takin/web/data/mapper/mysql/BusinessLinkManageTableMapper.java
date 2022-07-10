package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.shulie.takin.web.data.model.mysql.ApplicationMntEntity;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.data.param.application.QueryApplicationParam;
import io.shulie.takin.web.data.result.application.ApplicationListResult;
import org.apache.ibatis.annotations.Param;

public interface BusinessLinkManageTableMapper extends BaseMapper<BusinessLinkManageTableEntity> {
    List<BusinessLinkManageTableEntity> findActivityAppName(@Param("appName") String appName, @Param("entrace") String entrace);

    @InterceptorIgnore(tenantLine = "true")
    IPage<BusinessLinkManageTableEntity> selectEntrancePageIgnoreInterceptorByType(
            @Param("page") IPage<BusinessLinkManageTableEntity> page, @Param("type") Integer type);

    @InterceptorIgnore(tenantLine = "true")
    int updateIgnoreInterceptorById(@Param("param") BusinessLinkManageTableEntity param);

    void clearCategory(List<Long> categoryIds);
}
