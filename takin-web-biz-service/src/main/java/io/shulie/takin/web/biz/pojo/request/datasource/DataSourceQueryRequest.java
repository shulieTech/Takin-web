package io.shulie.takin.web.biz.pojo.request.datasource;

import java.util.List;

import javax.validation.constraints.Size;

import com.pamirs.takin.common.constant.DataSourceVerifyTypeEnum;
import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-28
 */
@Data
@ApiModel("数据源查询对象")
public class DataSourceQueryRequest extends PagingDevice {

    @ApiModelProperty("数据库类型")
    private DataSourceVerifyTypeEnum type;

    @ApiModelProperty("标签")
    private List<Long> tagsIdList;

    @ApiModelProperty("数据源名称")
    @Size(max = 50)
    private String datasourceName;

    @ApiModelProperty("数据源地址")
    @Size(max = 100)
    private String jdbcUrl;

    private Long deptId;
}
