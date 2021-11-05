package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.cloud.ext.content.trace.PagingContextExt;

/**
 * 场景列表查询
 *
 * @author qianshui
 * @date 2020/4/17 下午2:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneManageQueryVO extends PagingContextExt {

    private Long sceneId;

    private String sceneName;

    private Integer status;

    /**
     * 场景id
     */
    private List<Long> sceneIds;

    private Long tagId;

    private String lastPtStartTime;

    private String lastPtEndTime;

}
