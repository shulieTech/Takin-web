package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@TableName(value = "t_middleware_type")
@ToString(callSuper = true)
@EqualsAndHashCode
public class MiddlewareTypeEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String engName;

    private String type;

    private Integer valueOrder;

    /**
     * 删除
     * 1 删除, 0 未删除
     */
    private Integer isDeleted;

    public Integer getValueOrder() {
        if (valueOrder == null) {
            return Integer.MAX_VALUE;
        }
        return valueOrder;
    }
}
