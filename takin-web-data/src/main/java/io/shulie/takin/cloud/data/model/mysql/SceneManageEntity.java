package io.shulie.takin.cloud.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.cloud.data.annocation.EnableSign;
import lombok.Data;

/**
 * 压测场景 - 实体类
 *
 * @author 数列科技
 * @author 张天赐
 */
@Data
@TableName(value = "t_scene_manage")
@EnableSign
public class SceneManageEntity {
    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 场景名称
     */
    @TableField(value = "scene_name")
    private String sceneName;
    /**
     * 参考数据字典 场景状态
     */
    @TableField(value = "status")
    private Integer status;
    /**
     * 压测场景类型：0普通场景，1流量调试
     */
    @TableField(value = "type")
    private Integer type;
    /**
     * 最新压测时间
     */
    @TableField(value = "last_pt_time")
    private Date lastPtTime;
    /**
     * 施压配置
     */
    @TableField(value = "pt_config")
    private String ptConfig;
    /**
     * 脚本类型：0-Jmeter 1-Gatling
     */
    @TableField(value = "script_type")
    private Integer scriptType;
    /**
     * 是否删除：0-否 1-是
     */
    @TableLogic
    @TableField(value = "is_deleted")
    private Integer isDeleted;
    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;
    /**
     * 扩展字段
     */
    @TableField(value = "features")
    private String features;
    /**
     * 脚本解析结果
     */
    @TableField(value = "script_analysis_result")
    private String scriptAnalysisResult;
    /**
     * 创建人
     */
    @TableField(value = "create_name")
    private String createName;
    /**
     * 最后修改时间
     */
    @TableField(value = "update_time")
    private Date updateTime;
    /**
     * 最后修改人
     */
    @TableField(value = "update_name")
    private String updateName;
    /**
     * 用户主键
     */
    @TableField(value = "user_id")
    private Long userId;
    /**
     * 租户主键
     */
    @TableField(value = "tenant_id")
    private Long tenantId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code")
    private String envCode;

    //数据签名字段补充
    @TableField(value = "sign" , fill = FieldFill.INSERT)
    private String sign;
    @TableField(value = "custom_id")
    private Long customId;
    @TableField(value = "dept_id")
    private Long deptId;
    @TableField(value = "create_uid")
    private Long createUid;
    @TableField(value = "customer_id")
    private Long customerId;
    @TableField(value = "is_archive")
    private Integer isArchive;


}


