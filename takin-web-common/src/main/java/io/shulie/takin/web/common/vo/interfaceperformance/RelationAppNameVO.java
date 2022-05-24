package io.shulie.takin.web.common.vo.interfaceperformance;

import lombok.Data;

import java.util.Date;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 3:05 下午
 */
@Data
public class RelationAppNameVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 关联入口应用
     */
    private String entranceAppName;
}
