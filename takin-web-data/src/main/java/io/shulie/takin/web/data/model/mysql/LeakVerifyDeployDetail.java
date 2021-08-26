package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
    * 漏数实例详情表
    */
@Data
@TableName(value = "t_leak_verify_deploy_detail")
public class LeakVerifyDeployDetail {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 漏数验证实例id
     */
    @TableField(value = "leak_verify_deploy_id")
    private Long leakVerifyDeployId;

    /**
     * 漏数类型
     */
    @TableField(value = "leak_type")
    private String leakType;

    /**
     * 漏数数量
     */
    @TableField(value = "leak_count")
    private Long leakCount;

    /**
     * 漏数内容
     */
    @TableField(value = "leak_content")
    private String leakContent;

    @TableField(value = "gmt_create")
    private Date gmtCreate;

    @TableField(value = "gmt_update")
    private Date gmtUpdate;

    @TableField(value = "feature")
    private String feature;
}
