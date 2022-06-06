package io.shulie.takin.web.data.mapper.provider;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: 南风
 * @Date: 2022/6/1 4:11 下午
 */
@Component
public class TenantDataSignProvider {

    public String clearSign(@Param("tenantIds") List<Long> tenantIds, @Param("envCode")String envCode, @Param("tableName") String tableName){
        String sql = new SQL().UPDATE(tableName).SET("sign = ''").toString();

        StringBuilder sb = new StringBuilder();
        sb.append(sql).append(" where env_code = '").append(envCode).append("'");
        if(!CollectionUtils.isEmpty(tenantIds)){
            sb.append("  and tenant_id in (");
            for(int i =0;i<tenantIds.size();i++){
                sb.append(tenantIds.get(i));
                if(i<tenantIds.size() -1){
                    sb.append(",");
                }
            }
            sb.append(")");
        }
        System.out.println(sb);
        return sb.toString();
    }
}
