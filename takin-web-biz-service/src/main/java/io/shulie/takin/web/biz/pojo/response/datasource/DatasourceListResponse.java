package io.shulie.takin.web.biz.pojo.response.datasource;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.shulie.takin.web.biz.pojo.response.tagmanage.TagManageResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-28
 */
@Data
@ApiModel("数据源列表返回的简单对象")
public class DatasourceListResponse {

    @ApiModelProperty("数据源ID")
    private Long datasourceId;

    @ApiModelProperty("数据源名称")
    private String datasourceName;

    @ApiModelProperty("数据源类型")
    private DataSourceTypeResponse type;

    @ApiModelProperty("数据源连接")
    private String jdbcUrl;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("标签")
    private List<TagManageResponse> tags;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtUpdate;
}
