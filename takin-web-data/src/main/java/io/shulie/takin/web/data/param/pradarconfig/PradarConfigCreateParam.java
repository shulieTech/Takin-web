package io.shulie.takin.web.data.param.pradarconfig;

import lombok.Data;

/**
 * @author junshao
 * @date 2021/07/08 2:35 下午
 */
@Data
public class PradarConfigCreateParam {
    /**
     * id
     */
    private Long id;

    /**
     * zkPath
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
     * 状态 0: 正常 1： 删除
     */
    private Boolean isDeleted;
}
