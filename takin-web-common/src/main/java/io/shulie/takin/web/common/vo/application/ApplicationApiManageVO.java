package io.shulie.takin.web.common.vo.application;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author vernon
 * @date 2020/7/7 10:40
 */
@Data
public class ApplicationApiManageVO implements Serializable {
    /**
     * 主键
     */
    private Long id;
    /**
     * api
     */
    private String api;
    /**
     * 应用主键
     */
    private Long applicationId;
    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    /**
     * 是否有效 0:有效;1:无效
     */
    private Byte isDeleted;

    private String method;

    private String requestMethod;

    private Boolean canEdit = true;

    private Boolean canRemove = true;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }
}
