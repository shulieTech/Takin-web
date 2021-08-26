package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_script_file_ref")
public class ScriptFileRefEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件id
     */
    @TableField(value = "file_id")
    private Long fileId;

    /**
     * 脚本id
     */
    @TableField(value = "script_deploy_id")
    private Long scriptDeployId;

    @TableField(value = "gmt_create")
    private Date gmtCreate;

    @TableField(value = "gmt_update")
    private Date gmtUpdate;
}
