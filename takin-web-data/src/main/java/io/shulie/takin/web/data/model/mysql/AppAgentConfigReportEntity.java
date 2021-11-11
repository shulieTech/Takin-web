package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * agent配置上报详情(AppAgentConfigReport)实体类
 *
 * @author 南风
 * @date 2021-09-28 17:27:21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_app_agent_config_report")
@ToString(callSuper = true)
public class AppAgentConfigReportEntity extends UserBaseEntity implements Serializable {
    private static final long serialVersionUID = 614299322159620031L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 删除
     * 1 删除, 0 未删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * AgentId
     */
    private String agentId;

    /**
     * 应用id
     */
    private Long applicationId;

    /**
     * 应用名
     */
    private String applicationName;

    /**
     * 配置类型 0:开关
     */
    private Integer configType;

    /**
     * 配置KEY
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 备注
     */
    private String commit;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtUpdate;

}
