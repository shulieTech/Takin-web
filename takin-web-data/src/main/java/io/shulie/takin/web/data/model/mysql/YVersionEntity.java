package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

@Data
@TableName(value = "y_version")
public class YVersionEntity extends TenantBaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件id
     */
    @TableField(value = "sid")
    private Long sid;

    /**
     * 脚本id
     */
    @TableField(value = "dids")
    private String dids;

    @TableField(value = "pid")
    private String pid;

    @TableField(value = "vid")
    private String vid;
}
