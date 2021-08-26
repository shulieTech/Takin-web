package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_isolation_app_main_config")
public class IsolationAppMainConfigEntity {
    /**
     * 应用ID
     */
    @TableId(value = "application_id", type = IdType.INPUT)
    private Long applicationId;

    /**
     * 是否校验网络
     */
    @TableField(value = "check_network")
    private Boolean checkNetwork;

    /**
     * 应用隔离MOCK节点
     */
    @TableField(value = "mock_app_nodes")
    private String mockAppNodes;

    /**
     * 当前状态  0.未隔离  1. 隔离中  2.已隔离
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 应用隔离上传隔离信息
     */
    @TableField(value = "iso_app_rets")
    private String isoAppRets;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    @TableField(value = "error_msg")
    private String errorMsg;
}
