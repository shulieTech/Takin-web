package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.PressureMachineEntity;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineStatisticsDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PressureMachineMapper extends BaseMapper<PressureMachineEntity> {

    @Select(" select * from  t_pressure_machine where ip = #{ip}")
    PressureMachineEntity getByIp(@Param("ip") String ip);

    @Select("select status ,count(1) as `count`  from  t_pressure_machine where is_deleted=0  group by status ")
    List<PressureMachineStatisticsDTO> statistics();
}
