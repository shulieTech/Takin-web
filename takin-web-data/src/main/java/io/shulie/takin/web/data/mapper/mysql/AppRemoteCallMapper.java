package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.AppRemoteCallEntity;

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
}