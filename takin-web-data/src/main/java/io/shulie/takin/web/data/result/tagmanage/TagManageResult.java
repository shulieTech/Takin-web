package io.shulie.takin.web.data.result.tagmanage;

import java.util.Date;

import lombok.Data;


/**
 * @author zhaoyong
 */
@Data
public class TagManageResult {

    private Long id;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签类型;0为脚本标签
     */
    private Integer tagType;

    /**
     * 标签状态;0为可用
     */
    private Integer tagStatus;

    private Date gmtCreate;

    private Date gmtUpdate;
}
