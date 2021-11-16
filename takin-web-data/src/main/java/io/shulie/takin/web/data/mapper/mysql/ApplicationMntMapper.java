package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pamirs.takin.entity.domain.vo.application.NodeNumParam;
import io.shulie.takin.web.data.model.mysql.ApplicationMntEntity;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import org.apache.ibatis.annotations.Param;

public interface ApplicationMntMapper extends BaseMapper<ApplicationMntEntity> {

    /**
     * 批量更新应用节点数
     *
     * @param paramList 参数集合
     */
    void batchUpdateAppNodeNum(@Param("list") List<NodeNumParam> paramList, @Param("tenantId") Long tenantId);

    /**
     * 获取租户应用数据
     * @param ext
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ApplicationMntEntity> getAllTenantApp(TenantCommonExt ext);
}
