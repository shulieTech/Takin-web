package io.shulie.takin.cloud.ext.content.trace;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 集成了{@link PagingDevice}的{@link ContextExt}
 *
 * @author 张天赐
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PagingContextExt extends ContextExt {
    /**
     * 分页页码
     */
    private int pageNumber = 1;
    /**
     * 分页大小
     */
    private int pageSize = 20;
}
