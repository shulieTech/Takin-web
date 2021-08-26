package io.shulie.takin.web.biz.pojo.response.middleware;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/2/24 5:08 下午
 */
@Data
public class AppMiddlewareScanResponse {
    /**
     * 主键
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 应用名
     */
    @ApiModelProperty("应用名")
    private String appName;

    /**
     * 中间件类型，1. web容器，2. web服务器，3. 消息队列，4. 远程调用，5. 数据源，6. 连接池，7. ESB
     ，8. 缓存，9. 缓存中间件，10. NoSql，11. 文件存储，12. job
     */
    @ApiModelProperty("中间件类型")
    private String middlewareType;

    /**
     * jar包名称
     */
    @ApiModelProperty("jar包名称")
    private String jarName;

    /**
     * 1. 未录入，2. 无需支持，3. 未支持，4. 已支持
     */
    @ApiModelProperty("支持状态")
    private Integer status;

    /**
     * 中间件名称
     */
    @ApiModelProperty("中间件名称")
    private String middlewareName;



    /**
     * 中间件支持版本列表
     */
    @ApiModelProperty("中间件支持版本列表")
    private List<String> middlewareVersions;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String gmtCreate;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private String gmtModified;

    /**
     * 软删
     */
    private Boolean isDeleted;

    /**
     * 租户id
     */
    @ApiModelProperty("租户id")
    private Long customerId;

    /**
     * 创建人
     */
    private Long creatorId;

    /**
     * 修改人
     */
    private Long modifierId;
}
