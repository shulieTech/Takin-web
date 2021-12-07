package io.shulie.takin.web.biz.pojo.response.agentupgradeonline;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description agent插件上传返回结果
 * @Author ocean_wll
 * @Date 2021/11/10 4:24 下午
 */
@Data
@ApiModel("出参类-agent插件上传")
public class AgentPluginUploadResponse {

    @ApiModelProperty("文件路径")
    private String filePath;

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("上传时文件的名称, 非上传后的真名称")
    private String originalName;

    @ApiModelProperty("插件类型,0:中间件包，1:simulator包，2:agent包")
    private Integer pluginType;

    @ApiModelProperty("升级版本信息")
    private List<PluginInfo> pluginInfoList;

    @ApiModelProperty("是否允许新增配置")
    private Boolean allowAddConfig;

    @ApiModelProperty("是否允许提交")
    private Boolean allowSubmit;

    @ApiModelProperty("校验包的错误信息")
    private List<String> errorMessages;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("最后更新时间")
    private Date lastUpdateDate = new Date();

}
