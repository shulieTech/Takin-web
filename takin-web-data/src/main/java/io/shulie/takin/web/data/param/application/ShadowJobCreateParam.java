package io.shulie.takin.web.data.param.application;

import java.util.Date;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/9 9:03 下午
 */
@Data
public class ShadowJobCreateParam extends UserCommonExt {
    private Long id;
    private Long applicationId;
    private String name;
    private Integer type;
    private Integer status;
    private Integer active;
    private Date createTime;
    private Date updateTime;
    private String configCode;
    private String remark;
}
