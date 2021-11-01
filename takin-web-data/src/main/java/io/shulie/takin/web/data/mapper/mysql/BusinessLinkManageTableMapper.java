package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import org.apache.ibatis.annotations.Param;

public interface BusinessLinkManageTableMapper extends BaseMapper<BusinessLinkManageTableEntity> {
    List<BusinessLinkManageTableEntity> findActivityAppName(@Param("appName") String appName, @Param("entrace") String entrace);
}
