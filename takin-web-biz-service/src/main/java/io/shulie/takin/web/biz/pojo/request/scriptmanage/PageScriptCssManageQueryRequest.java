package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @Package io.shulie.takin.web.biz.pojo.request.scriptmanage
* @ClassName: PageScriptDataQueryRequest
* @author hezhongqi
* @description:
* @date 2023/9/20 18:10
*/
@Data
public class PageScriptCssManageQueryRequest extends PagingDevice {

    private static final long serialVersionUID = 840809684130453478L;

    private String businessFlowName;
    private Integer taskState;
    /**
     * 脚本csv组件名
     */
    @ApiModelProperty("scriptCsvDataSetName")
    private String scriptCsvDataSetName;

    /**
     * 脚本csv文件名
     */
    @ApiModelProperty("scriptCsvFileName")
    private String scriptCsvFileName;

    @ApiModelProperty("查询类型：0:查csv列表 1:查任务列表")
    private Integer type = 0;
}
