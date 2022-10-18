package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateMqConsumerEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface PressureResourceRelateMqConsumerMapper
        extends BaseMapper<PressureResourceRelateMqConsumerEntity> {
    @InterceptorIgnore(tenantLine = "true")
    @Insert("<script>" +
            "insert into t_pressure_resource_relate_shadow_mq_consumer(" +
            "resource_id,detail_id,topic,group,mq_type,comsumer_type,is_cluster,application_name,consumer_tag," +
            "feature,type,tenant_id,env_code,gmt_create)" +
            "values " +
            "(#{item.resourceId},#{item.detailId},#{item.topic},#{item.group},#{item.mqType},#{item.comsumerType}," +
            "#{item.isCluster},#{item.applicationName},#{item.consumerTag},#{item.feature},#{item.type}," +
            "#{item.tenantId},#{item.envCode},#{item.gmtCreate})" +
            " ON DUPLICATE KEY UPDATE gmt_modified=now()" +
            "</script>")
    void saveOrUpdate(@Param("item") PressureResourceRelateMqConsumerEntity item);
}
