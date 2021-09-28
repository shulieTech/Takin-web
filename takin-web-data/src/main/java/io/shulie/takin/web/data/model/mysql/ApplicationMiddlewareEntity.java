package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.NewBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 应用中间件(ApplicationMiddleware)实体类
 *
 * @author liuchuan
 * @date 2021-06-30 16:09:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_application_middleware")
@ToString(callSuper = true)
public class ApplicationMiddlewareEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = -75585827689839498L;

    /**
     * 应用id
     */
    private Long applicationId;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 项目名称
     */
    private String artifactId;

    /**
     * 项目组织名称
     */
    private String groupId;

    /**
     * 版本号
     */
    private String version;

    /**
     * 类型, 字符串形式
     */
    private String type;

    /**
     * 状态, 1已支持, 2 未支持, 3 无需支持, 4 未知, 0 无
     */
    private Integer status;

    /**
     * 删除
     * 1 删除, 0 未删除
     */
    private Integer isDeleted;

}
