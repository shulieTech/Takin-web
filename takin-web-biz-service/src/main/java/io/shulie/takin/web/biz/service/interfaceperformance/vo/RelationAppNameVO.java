package io.shulie.takin.web.biz.service.interfaceperformance.vo;

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

    /**
     * 参数
     */
    private String param;

    /**
     * header头信息
     */
    private String header;
}
