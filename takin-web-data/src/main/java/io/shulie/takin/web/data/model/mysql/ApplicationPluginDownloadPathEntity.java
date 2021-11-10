package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 探针根目录(ApplicationPluginDownloadPath)实体类
 *
 * @author 南风
 * @date 2021-11-10 16:12:07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_application_plugin_download_path")
@ToString(callSuper = true)
public class ApplicationPluginDownloadPathEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = -27311155654400392L;
    
   /**
   * 应用id
   */  
    private Long applicationId;
    
   /**
   * 应用名
   */  
    private String applicationName;
    
   /**
   * 类型 0:oss;1:ftp;2:nginx
   */  
    private Integer pathType;
    
   /**
   * 地址
   */  
    private String pathAddress;
    
   /**
   * 路径
   */  
    private String path;
    
   /**
   * 用户名
   */  
    private String username;
    
   /**
   * 密码
   */  
    private String password;

    private Integer validStatus;

    /**
     * 租户id
     */
    @TableField(value = "CUSTOMER_ID", fill = FieldFill.INSERT)
    private Long customerId;

    /**
     * 用户id
     */
    @TableField(value = "USER_ID", fill = FieldFill.INSERT)
    private Long userId;
    
}
