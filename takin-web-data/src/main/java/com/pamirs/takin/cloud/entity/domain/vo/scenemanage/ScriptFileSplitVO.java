package com.pamirs.takin.cloud.entity.domain.vo.scenemanage;

import lombok.Data;

/**
 * @author moriarty
 */
@Data
public class ScriptFileSplitVO {

    private Long sceneId;

    private String fileName;

    private Boolean split;

    private Boolean orderSplit;

    private Integer podNum;

    private Integer orderColumnNum;

    private String columnSeparator;

    private Boolean forceSplit;

}
