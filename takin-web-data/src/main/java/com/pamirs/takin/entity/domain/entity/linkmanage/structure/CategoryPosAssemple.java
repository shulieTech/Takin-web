package com.pamirs.takin.entity.domain.entity.linkmanage.structure;

import lombok.Data;

/**
 * 节点和节点的位置
 *
 * @author mubai
 * @date 2020-07-03 15:08
 */

@Data
public class CategoryPosAssemple {

    private Category node;

    private Integer pos;

    public CategoryPosAssemple(Category node, Integer pos) {
        this.node = node;
        this.pos = pos;
    }
}
