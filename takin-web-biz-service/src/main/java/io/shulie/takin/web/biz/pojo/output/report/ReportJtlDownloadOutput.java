package io.shulie.takin.web.biz.pojo.output.report;

import java.io.Serializable;

import lombok.Data;

/**
 * @author 无涯
 * @date 2020/12/17 1:02 下午
 */
@Data
public class ReportJtlDownloadOutput implements Serializable {
    private static final long serialVersionUID = -262737931928766813L;

    private String content;
    private Boolean success;

    public ReportJtlDownloadOutput(String content, Boolean success) {
        this.content = content;
        this.success = success;
    }
}
