package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 数据库映射共有类
 *
 * @author liuchuan
 * @date 2021/4/7 5:27 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class NewBaseEntity extends BaseEntity {

    private static final long serialVersionUID = -1L;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtUpdate;

}
