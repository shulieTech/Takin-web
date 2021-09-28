package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.NewBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用节点探针操作表(ApplicationNodeProbe)实体类
 *
 * @author liuchuan
 * @date 2021-06-03 13:43:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_application_node_probe")
public class ApplicationNodeProbeEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = 621811300794225122L;

    /**
     * 租户id
     */
    @TableField(value = "customer_id", fill = FieldFill.INSERT)
    private Long customerId;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * agentId
     */
    private String agentId;

    /**
     * 操作类型, 1 安装, 3 升级, 2 卸载, 0 无
     */
    private Integer operate;

    /**
     * 操作结果, 0 失败, 1 成功, 99 无
     */
    private Integer operateResult;

    /**
     * 对应的探针包记录id, 卸载的时候不用填
     */
    private Long probeId;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 操作的id, 时间戳, 递增, agent 需要, 进行操作的时候会创建或更新
     */
    private Long operateId;

}
