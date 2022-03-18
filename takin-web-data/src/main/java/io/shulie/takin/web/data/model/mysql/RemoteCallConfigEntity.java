package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.common.enums.application.AppRemoteCallConfigEnum;
import lombok.Data;
import lombok.ToString;

@Data
@TableName(value = "t_remote_call_config")
@ToString(callSuper = true)
public class RemoteCallConfigEntity implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * order 不允许修改
     */
    private Integer valueOrder;

    /**
     * 删除
     * 1 删除, 0 未删除
     */
    private Integer isDeleted;

    /**
     * 中间件中文描述
     */
    private String name;

    /**
     * 中间件英文名称
     */
    private String engName;

    /**
     * 描述
     */
    private String description;

    /**
     * agentCheckType
     */
    private String checkType;

    /**
     * 系统预设：1-预设，0-非预设(系统预设的不允许修改，对应 {@link AppRemoteCallConfigEnum} 有常量使用)
     */
    private Integer isSystem;
}
