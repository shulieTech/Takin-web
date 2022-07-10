package io.shulie.takin.web.biz.pojo.response.activity;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/5/11 1:54 下午
 */
@Data
@ApiModel("出参类-业务活动分类出参")
public class ActivityCategoryTreeResponse {

    @ApiModelProperty("分类id")
    private Long id;

    @ApiModelProperty("分类名称")
    private String title;

    @ApiModelProperty("分类父级id")
    private Long parentId;

    public ActivityCategoryTreeResponse(Long id, String title, Long parentId) {
        this.id = id;
        this.title = title;
        this.parentId = parentId;
    }

    @ApiModelProperty("子级分类")
    private List<ActivityCategoryTreeResponse> children = new ArrayList<>();

    public void addChild(ActivityCategoryTreeResponse child) {
        children.add(child);
    }
}
