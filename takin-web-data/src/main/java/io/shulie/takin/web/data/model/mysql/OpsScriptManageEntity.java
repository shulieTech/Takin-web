package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 运维脚本主表(OpsScriptManage)实体类
 *
 * @author caijy
 * @date 2021-06-16 10:35:39
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_ops_script_manage")
public class OpsScriptManageEntity extends UserBaseEntity implements Serializable {
    private static final long serialVersionUID = 815261567033798687L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 删除
     * 1 删除, 0 未删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 脚本名称
     */
    private String name;

    /**
     * 来源于字典。脚本类型：1=影子库表创建脚本 2=基础数据准备脚本 3=铺底数据脚本 4=影子库表清理脚本 5=缓存预热脚本
     */
    private Integer scriptType;

    /**
     * 执行状态 0=待执行,1=执行中,2=已执行
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtUpdate;

    private Long deptId;

}
