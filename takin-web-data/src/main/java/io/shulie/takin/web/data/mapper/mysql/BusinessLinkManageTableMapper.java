package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BusinessLinkManageTableMapper extends BaseMapper<BusinessLinkManageTableEntity> {
    List<BusinessLinkManageTableEntity> findActivityAppName(@Param("appName") String appName, @Param("entrace") String entrace,@Param("customerId") Long customerId);
}
