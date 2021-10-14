package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pamirs.takin.entity.domain.vo.application.NodeNumParam;
import io.shulie.takin.web.data.model.mysql.ApplicationMntEntity;
import org.apache.ibatis.annotations.Param;

public interface ApplicationMntMapper extends BaseMapper<ApplicationMntEntity> {

    /**
     * 批量更新应用节点数
     *
     * @param paramList 参数集合
     */
    void batchUpdateAppNodeNum(@Param("list") List<NodeNumParam> paramList, @Param("customerId") Long customerId);

}
