package io.shulie.takin.web.biz.service.interfaceperformance.vo;

import io.shulie.takin.web.biz.pojo.request.interfaceperformance.ContentTypeVO;
import io.shulie.takin.web.biz.pojo.request.scene.NewSceneRequest;
import io.shulie.takin.web.biz.pojo.request.scene.SceneDetailResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 3:05 下午
 */
@Data
public class PerformanceConfigVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 接口调试名称
     */
    private String name;

    /**
     * 请求地址或者域名
     */
    private String requestUrl;

    /**
     * 请求头
     */
    private String headers;

    /**
     * cookies
     */
    private String cookies;

    /**
     * 请求体
     */
    private String body;

    /**
     * 请求地址或者域名
     */
    private String httpMethod;

    /**
     * 响应超时时间
     */
    private Integer timeout;

    /**
     * customerId
     */
    private Long customerId;

    /**
     * 是否重定向
     */
    private Boolean isRedirect;

    /**
     * contentType数据
     */
    private String contentType;

    private ContentTypeVO contentTypeVo;

    /**
     * 0：未调试，1，调试中
     */
    private Integer status;

    /**
     * 压测状态
     */
    @ApiModelProperty(value = "压测状态")
    private Integer pressureStatus;

    /**
     * 关联入口应用
     */
    private String entranceAppName;

    /**
     * 软删
     */
    private Boolean isDeleted;

    /**
     * 创建人
     */
    private Long creatorId;

    /**
     * 修改人
     */
    private Long modifierId;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 归属人
     */
    private String userName;

    private Long userId;

    /**
     * 更新时间
     */
    private Date gmtModified;

    /**
     * 备注
     */
    private String remark;

    /**
     * 压测相关配置
     */
    private NewSceneRequest pressureConfigRequest;
    /**
     * 绑定的场景id
     */
    private Long bindSceneId;


}
