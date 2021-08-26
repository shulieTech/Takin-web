package io.shulie.takin.web.common.vo.perfomanceanaly;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/3/12 3:35 下午
 */
@Data
public class MemoryEntryVO {
    private String name;
    private Long init;
    private Long used;
    private Long total;
    private Long max;
}
