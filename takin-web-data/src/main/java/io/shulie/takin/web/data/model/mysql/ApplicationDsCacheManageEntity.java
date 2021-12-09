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
import lombok.ToString;

/**
 * 缓存影子库表配置表(ApplicationDsCacheManage)实体类
 *
 * @author 南风
 * @date 2021-08-30 14:40:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_application_ds_cache_manage")
@ToString(callSuper = true)
public class ApplicationDsCacheManageEntity extends UserBaseEntity implements Serializable {
    private static final long serialVersionUID = 214783401261180458L;

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
     * 应用主键
     */
    private Long applicationId;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 中间件名称
     */
    private String cacheName;

    /**
     * 集群地址
     */
    private String colony;

    /**
     * 源用户名
     */
    private String userName;

    /**
     * 源密码
     */
    private String pwd;


    private String type;

    /**
     * 方案类型 6:影子key 7:影子集群
     */
    private Integer dsType;

    /**
     * 额外配置,存json
     */
    private String fileExtedn;

    /**
     * 影子额外配置
     */
    private String shaDowFileExtedn;

    /**
     * 配置全部参数json化
     */
    private String configJson;

    /**
     * 方案类型 0:amdb 1:手动录入
     */
    private Integer source;

    /**
     * 状态 0:启用；1:禁用
     */
    private Integer status;

    private String agentSourceType;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtUpdate;

}
