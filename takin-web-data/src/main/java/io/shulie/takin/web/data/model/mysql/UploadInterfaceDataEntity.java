package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * dubbo和job接口上传收集表
 */
@Data
@TableName(value = "t_upload_interface_data")
public class UploadInterfaceDataEntity {
    /**
     * 抽数表id
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * APP名
     */
    @TableField(value = "APP_NAME")
    private String appName;

    /**
     * 接口值
     */
    @TableField(value = "INTERFACE_VALUE")
    private String interfaceValue;

    /**
     * 上传数据类型 查看字典  暂时 1 dubbo 2 job
     */
    @TableField(value = "INTERFACE_TYPE")
    private Integer interfaceType;

    /**
     * 插入时间
     */
    @TableField(value = "CREATE_TIME")
    private LocalDateTime createTime;
}
