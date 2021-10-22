package io.shulie.takin.web.data.result.application;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.ApplicationDsCacheManageEntity;

/**
 * 缓存影子库表配置表(ApplicationDsCacheManage)详情出参类
 *
 * @author 南风
 * @date 2021-08-30 14:40:53
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApplicationDsCacheManageDetailResult extends ApplicationDsCacheManageEntity {

    public String getFilterStr(){
        return this.getColony()+"@@"+this.getUserName()+"@@"+this.getAgentSourceType();
    }

}
