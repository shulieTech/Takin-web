package io.shulie.takin.web.data.result.exception;

import java.util.Date;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/1/4 7:41 下午
 */
@Data
public class ExceptionResult {
    private Long id;

    /**
     * 异常类型
     */
    private String type;

    /**
     * 异常编码
     */
    private String code;

    /**
     * 异常描述
     */
    private String description;

    /**
     * 处理建议
     */
    private String suggestion;

    /**
     * 发生次数
     */
    private String num;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtModified;

    /**
     * 软删
     */
    private Boolean isDeleted;
}
