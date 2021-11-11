package io.shulie.takin.web.data.param.application;

import java.util.Date;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;

/**
 * 应用管理
 *
 * @author ZhangXT
 * @date 2020/11/5 17:29
 */
@Data
public class ApplicationUpdateParam extends UserCommonExt {

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
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否有效 0:有效;1:无效
     */
    private Byte isDeleted;
    private String requestMethod;


    public ApplicationUpdateParam() {
    }


}
