package io.shulie.takin.web.data.model.mysql;

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
   * 配置内容
   */  
    private String context;

    private String salt;

    private Integer validStatus;


    /**
     * 环境标识
     */
    private String envCode;

    /**
     * 租户Id
     */
    private Long tenantId;
    
}
