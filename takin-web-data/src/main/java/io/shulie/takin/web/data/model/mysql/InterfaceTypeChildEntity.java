package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

/**
 * 应用远程调用中间件子类型配置
 */
@Data
@TableName(value = "t_interface_type_child")
@ToString(callSuper = true)
public class InterfaceTypeChildEntity implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 删除
     * 1 删除, 0 未删除
     */
    private Integer isDeleted;

    /**
     * 主类型Id
     */
    private Long mainId;

    /**
     * 中间件中文描述
     */
    private String name;

    /**
     * 中间件英文名称
     */
    private String engName;
}
