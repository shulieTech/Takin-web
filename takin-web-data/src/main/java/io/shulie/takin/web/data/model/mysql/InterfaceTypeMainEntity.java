package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

/**
 * 应用远程调用中间件主类型配置
 */
@Data
@TableName(value = "t_interface_type_main")
@ToString(callSuper = true)
public class InterfaceTypeMainEntity implements Serializable {

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
     * 对应大数据rpcType
     */
    private Integer rpcType;

    /**
     * 系统预设：1-预设，0-非预设(系统预设的不允许修改)
     */
    private Integer isSystem;
}
