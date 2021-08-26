package io.shulie.takin.web.data.param.application;

import java.util.Date;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/4 8:45 下午
 */
@Data
public class LinkGuardCreateParam extends UserCommonExt {
    private Long id;
    private String applicationName;
    private Long applicationId;
    private String methodInfo;
    private String groovy;
    private Date createTime;
    private Date updateTime;
    private Boolean isDeleted;
    private Boolean isEnable;
    private String remark;
}
