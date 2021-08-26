package io.shulie.takin.web.biz.pojo.request.scenemanage;

import java.util.Date;

import javax.validation.constraints.NotNull;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-12-01 10:32
 */
@Data
public class SceneSchedulerTaskCreateRequest extends UserCommonExt {

    @NotNull(message = "场景id不能为空")
    private Long sceneId;

    private String content;

    @NotNull(message = "执行时间不能为空")
    private Date executeTime;

    private Integer isExecuted;

}
