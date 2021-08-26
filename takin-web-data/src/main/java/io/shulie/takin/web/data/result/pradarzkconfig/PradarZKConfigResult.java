package io.shulie.takin.web.data.result.pradarzkconfig;

import java.util.Date;

import lombok.Data;

@Data
public class PradarZKConfigResult {
    /**
     * id
     */
    private Long id;

    /**
     * 路径
     */
    private String zkPath;

    /**
     * 类型
     */
    private String type;

    /**
     * 数值
     */
    private String value;

    /**
     * 说明
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date modifyTime;
}
