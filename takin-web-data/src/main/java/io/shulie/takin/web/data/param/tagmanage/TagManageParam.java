package io.shulie.takin.web.data.param.tagmanage;

import lombok.Data;

/**
 * @author shulie
 */
@Data
public class TagManageParam {
    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签类型;0为脚本标签; 1:场景标签;2:应用
     */
    private Integer tagType;

    /**
     * 标签状态;0为可用
     */
    private Integer tagStatus;

}
