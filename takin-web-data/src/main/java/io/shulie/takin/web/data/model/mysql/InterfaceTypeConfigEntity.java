package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

@Data
@TableName(value = "t_interface_type_config")
@ToString(callSuper = true)
public class InterfaceTypeConfigEntity implements Serializable {

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
     * 接口类型id
     */
    private Long interfaceTypeId;

    /**
     * 支持配置
     */
    private Long configId;

    /**
     * 支持配置文本
     */
    private String configText;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    @JsonIgnore
    private String childTypeName;

    @TableField(exist = false)
    @JsonIgnore
    private String configName;
}
