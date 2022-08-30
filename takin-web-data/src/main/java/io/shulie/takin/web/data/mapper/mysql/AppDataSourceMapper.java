package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.PressureResourceAppDataSourceEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppDataSourceMapper
        extends BaseMapper<PressureResourceAppDataSourceEntity> {
    @Insert("<script>" +
            "insert into t_pressure_resouce_app_database(" +
            "app_name,data_source,shadow_data_source,db_name,table_user,password,middleware_type," +
            "connection_pool,type,ext_info,attachment,unique_key,user_app_key,tenant_id,env_code,gmt_create)" +
            "values " +
            "<foreach collection='scoresList' item='item' index='index' separator=','>" +
            "(#{item.appName},#{item.dataSource},#{item.shadowDataSource},#{item.dbName},#{item.tableUser}," +
            "#{item.password},#{item.middlewareType},#{item.connectionPool},#{item.type},#{item.extInfo}," +
            "#{item.attachment},#{item.uniqueKey},#{item.userAppKey},#{item.tenantId},#{item.envCode},#{item.gmtCreate}" +
            "</foreach>" +
            " ON DUPLICATE KEY UPDATE " +
            " data_source =values(data_source),db_name=values(db_name),password = values(password)," +
            "middleware_type=values(middleware_type),connection_pool=values(connection_pool),ext_info=values(ext_info),attachment=values(attachment)," +
            "</script>")
    void saveOrUpdate(@Param("sourcesList") List<PressureResourceAppDataSourceEntity> sources);
}
