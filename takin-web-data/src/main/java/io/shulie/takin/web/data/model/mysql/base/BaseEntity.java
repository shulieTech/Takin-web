package io.shulie.takin.web.data.model.mysql.base;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据库映射共有类
 *
 * @author liuchuan
 * @date 2021/4/7 5:27 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseEntity extends TenantBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO,value = "id")
    private Long id;

    /**
     * 删除
     * 1 删除, 0 未删除
     */
    @TableLogic
    private Integer isDeleted;

}
