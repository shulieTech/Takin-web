package io.shulie.takin.web.biz.pojo.output.application;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;

/**
 * TODO
 *
 * @author hezhongqi
 * @date 2021/8/10 16:50
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class ShadowMqConsumerOutput extends UserCommonExt {
    /**
     * 主键id
     */
    private Long id;

    /**
     * topic
     */
    private String topicGroup;

    /**
     * MQ类型
     */
    private String type;

    /**
     * 应用id
     */
    private Long applicationId;

    /**
     * 应用名称，冗余
     */
    private String applicationName;

    /**
     * 是否可用(0表示未启用,1表示已启用)
     */
    private Integer status;

    /**
     * 拓展字段
     */
    @TableField(value = "feature")
    private String feature;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    private Integer deleted;
}
