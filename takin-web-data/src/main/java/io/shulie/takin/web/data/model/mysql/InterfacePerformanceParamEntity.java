package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 11:28 上午
 */
@Data
@TableName(value = "t_interface_performance_param")
@ToString(callSuper = true)
public class InterfacePerformanceParamEntity extends TenantBaseEntity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接口压测配置Id
     */
    @TableField(value = "config_id")
    private Long configId;

    /**
     * 参数名
     */
    @TableField(value = "param_name")
    private String paramName;

    /**
     * 参数值
     */
    @TableField(value = "param_value")
    private String paramValue;

    /**
     * 类型 1:自定义参数,2:数据源参数
     */
    @TableField(value = "`type`")
    private Integer type;

    /**
     * 列索引
     */
    @TableField(value = "file_column_index")
    private Integer fileColumnIndex;

    /**
     * 文件Id
     */
    @TableField(value = "file_id")
    private Long fileId;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create")
    private Date gmtCreate;

    /**
     * 更新时间
     */
    @TableField(value = "gmt_modified")
    private Date gmtModified;
}
