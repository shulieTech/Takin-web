package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.PradarZkConfigEntity;
import io.shulie.takin.web.data.result.pradarzkconfig.PradarZKConfigResult;
import org.apache.ibatis.annotations.Param;

public interface PradarZkConfigMapper extends BaseMapper<PradarZkConfigEntity> {

    /**
     * 通过 zk path 获得配置, 有自己的， 有全局的
     *
     * @param zkPath zk路径
     * @param tenantId 租户id
     * @param envCode 环境
     * @return 配置
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PradarZKConfigResult> selectListByZkPath(@Param("zkPath") String zkPath, @Param("tenantId") Long tenantId,
        @Param("envCode") String  envCode);

}
