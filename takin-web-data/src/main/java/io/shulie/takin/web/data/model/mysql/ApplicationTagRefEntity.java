package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.NewBaseEntity;
import lombok.ToString;

/**
 * 应用标签表(ApplicationTagRef)实体类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:48:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_application_tag_ref")
@ToString(callSuper = true)
public class ApplicationTagRefEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = 177160334583640781L;

    /**
     * 应用id
     */
    private Long applicationId;

    /**
     * 应用名
     */
    private String applicationName;

    /**
     * 标签id
     */
    private Long tagId;

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 租户id
     */
    private Long customerId;

    /**
     * 用户id
     */
    private Long userId;

}
