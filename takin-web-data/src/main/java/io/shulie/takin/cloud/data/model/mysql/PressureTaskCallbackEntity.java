package io.shulie.takin.cloud.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName(value = "t_pressure_task_callback")
public class PressureTaskCallbackEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 资源Id
     */
    @TableField("resource_id")
    private String resourceId;

    /**
     * 回调类型
     */
    @TableField("call_back_type")
    private Integer callbackType;

    /**
     * 元数据
     */
    @TableField("meta_source")
    private String metaSource;

    @TableField(value = "gmt_create")
    private Date gmtCreate;

    public PressureTaskCallbackEntity(String resourceId, Integer callbackType, String metaSource, Date createDate) {
        this.resourceId = resourceId;
        this.callbackType = callbackType;
        this.metaSource = metaSource;
        this.setGmtCreate(createDate);
    }
}
