package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_pradar_zk_config")
public class PradarZkConfigEntity {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * zk路径
     */
    @TableField(value = "zk_path")
    private String zkPath;

    /**
     * 类型
     */
    @TableField("`type`")
    private String type;

    /**
     * 数值
     */
    @TableField("`value`")
    private String value;

    /**
     * 配置说明
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "modify_time")
    private Date modifyTime;

}
