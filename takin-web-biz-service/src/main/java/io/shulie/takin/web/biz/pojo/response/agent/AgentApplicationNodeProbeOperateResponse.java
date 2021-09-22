package io.shulie.takin.web.biz.pojo.response.agent;

import java.util.Collections;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/6/7 9:29 上午
 */
@Data
@ApiModel("出参类-agent 应用节点探针操作")
public class AgentApplicationNodeProbeOperateResponse {

    @ApiModelProperty("命令id(这里用时间戳), 保持递增")
    private Long id;

    @ApiModelProperty("操作类型, 1 框架命令, 默认 1")
    private Integer commandType = 1;

    @ApiModelProperty("操作类型, 1 安装, 2 卸载, 3 升级")
    private Integer operateType;

    @ApiModelProperty("命令生成时间戳")
    private Long commandTime;

    @ApiModelProperty("命令有效时间, -1 无限, 默认十分钟, 10 * 60 * 100, 单位: 毫秒")
    private Integer liveTime = 600000;

    @ApiModelProperty("安装, 升级 操作时, 数据包的下载地址")
    private String dataPath;

    @ApiModelProperty("扩展字段, map 形式")
    private Map<String, Object> extra = Collections.emptyMap();

}
