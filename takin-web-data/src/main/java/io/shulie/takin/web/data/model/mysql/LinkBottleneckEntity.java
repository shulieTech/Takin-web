package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 链路瓶颈
 */
@Data
@TableName(value = "t_link_bottleneck")
public class LinkBottleneckEntity {
    /**
     * id
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 应用名
     */
    @TableField(value = "APP_NAME")
    private String appName;

    /**
     * 关键字(TOPIC,JOB信息)
     */
    @TableField(value = "KEY_WORDS")
    private String keyWords;

    /**
     * 瓶颈类型(1、基础资源负载及异常，2、异步处理，3、TPS/RT稳定性，4，RT响应时间)
     */
    @TableField(value = "BOTTLENECK_TYPE")
    private Boolean bottleneckType;

    /**
     * 瓶颈等级(1、严重，2、普通，3、正常)
     */
    @TableField(value = "BOTTLENECK_LEVEL")
    private Boolean bottleneckLevel;

    /**
     * 瓶颈文本
     */
    @TableField(value = "TEXT")
    private String text;

    /**
     * JSON串
     */
    @TableField(value = "CONTENT")
    private String content;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(value = "MODIFY_TIME")
    private LocalDateTime modifyTime;

    /**
     * 是否有效(Y/N)
     */
    @TableField(value = "ACTIVE")
    private String active;

    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
