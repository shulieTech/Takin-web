package io.shulie.takin.web.biz.service.pressureresource.vo;

import io.shulie.surge.data.common.doc.annotation.Id;
import io.shulie.takin.web.biz.pojo.request.pressureresource.MockInfo;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.influxdb.annotation.Column;

import java.util.Date;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 10:24 AM
 */
@Data
@ToString(callSuper = true)
public class PressureResourceRelateRemoteCallCheckVO {
    private String type;
}
