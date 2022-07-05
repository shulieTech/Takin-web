package io.shulie.takin.web.common.vo.interfaceperformance;

import lombok.Data;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 3:05 下午
 */
@Data
public class RelationAppNameDto {
    /**
     * 主键
     */
    private Long id;

    /**
     * 关联入口应用
     */
    private String entranceAppName;
}
