package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.AppRemoteCallEntity;
import org.apache.ibatis.annotations.Param;

/**
* @author 何仲奇
* @date 2021/5/29 上午12:12
*/
public interface AppRemoteCallMapper extends BaseMapper<AppRemoteCallEntity> {
    /**
     * 订正修改
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<AppRemoteCallEntity> getListWithOutTenant();

    /**
     * 订正修改
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    void updateWithOutTenant(List<AppRemoteCallEntity> entities);
    @InterceptorIgnore(tenantLine = "true")
    List<AppRemoteCallEntity> getMockListByApplicationId(@Param(value = "applicationId") Long applicationId);
}