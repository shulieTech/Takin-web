package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.annocation.EnableSign;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "t_cluster_nacos_configuration")
@ToString(callSuper = true)
@EnableSign
public class ClusterNacosConfiguration implements Serializable {

    private static final long serialVersionUID = 339110573810927775L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO,value = "id")
    private Long id;

    /**
     * ecs中心名称
     */
    private String clusterName;

    /**
     * nacos服务地址
     */
    private String nacosServerAddr;

    /**
     * nacos命名空间
     */
    private String nacosNamespace;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtUpdate;

}
