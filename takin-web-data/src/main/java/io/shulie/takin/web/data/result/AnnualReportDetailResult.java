package io.shulie.takin.web.data.result;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.AnnualReportEntity;

/**
 * 第三方登录服务表(AnnualReport)列表出参类
 *
 * @author liuchuan
 * @date 2021-12-30 10:54:01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AnnualReportDetailResult extends AnnualReportEntity {

}
