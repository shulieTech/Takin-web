package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.NewBaseEntity;
import lombok.ToString;

/**
 * 插件版本库(PluginLibrary)实体类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:47:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_plugin_library")
@ToString(callSuper = true)
public class PluginLibraryEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = -59657215579375737L;

    /**
     * 插件名称
     */
    private String pluginName;

    /**
     * 插件类型 0:插件 1:主版本 2:agent版本
     */
    private Integer pluginType;

    /**
     * 插件版本
     */
    private String version;

    /**
     * 版本对应数值
     */
    private Long versionNum;

    /**
     * 更新说明
     */
    private String updateDescription;

    /**
     * 下载地址
     */
    private String downloadPath;

    /**
     * 备注
     */
    private String remark;

    /**
     * jar包配置文件原始数据,当前是主版本时填
     */
    private String ext;

    /**
     * 租户id
     */
    private Long customerId;

    /**
     * 用户id
     */
    private Long userId;

}
