package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import java.util.List;

import io.shulie.takin.web.common.vo.LabelValueVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/5/11 11:37 上午
 */
@Data
@ApiModel("出参类-脚本调试详情出参")
public class ScriptDebugDetailResponse {

    @ApiModelProperty("脚本调试记录id")
    private Long id;

    @ApiModelProperty(value = "脚本调试记录状态, 0 未启动, 1 启动中, 2 请求中, 3 请求结束, 4 调试成功, 5 调试失败", example = "0")
    private Integer status;

    @ApiModelProperty("失败类型, 10 启动通知超时失败, 20 漏数失败, 30 非200检查失败, 后面会扩展")
    private Integer failedType;

    @ApiModelProperty("错误标题")
    private String remark;

    @ApiModelProperty("检查漏数状态, 0:正常;1:漏数;2:未检测;3:检测失败")
    private Integer leakStatus;

    @ApiModelProperty("对应的 cloud 报告id")
    private Long cloudReportId;

    @ApiModelProperty("业务活动列表, label, value 数据结构形式")
    private List<LabelValueVO> businessActivities;

}
