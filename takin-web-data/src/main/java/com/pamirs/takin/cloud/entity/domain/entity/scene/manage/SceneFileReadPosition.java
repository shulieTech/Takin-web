package com.pamirs.takin.cloud.entity.domain.entity.scene.manage;

import lombok.Data;

/**
 * @author -
 */
@Data
public class SceneFileReadPosition {
    /**
     * 分片开始位置
     */
    private Long startPosition;

    /**
     * 分片已经读取的位置
     */
    private Long readPosition;

    /**
     * 分片结束位置
     */
    private Long endPosition;
}
