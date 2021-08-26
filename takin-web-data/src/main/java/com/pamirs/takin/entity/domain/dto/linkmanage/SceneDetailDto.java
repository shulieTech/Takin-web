package com.pamirs.takin.entity.domain.dto.linkmanage;

import java.util.List;

import com.pamirs.takin.entity.domain.vo.linkmanage.MiddleWareEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统流程详情出参数
 *
 * @author vernon
 * @date 2019/12/12 19:10
 */
@Data
@ApiModel(value = "sceneDetailDto", description = "业务流程详情出参")

public class SceneDetailDto {
    @ApiModelProperty(name = "businessProcessName", value = "业务流程名字")
    private String businessProcessName;
    @ApiModelProperty(name = "isCode", value = "是否核心流程")
    private String isCode;
    @ApiModelProperty(name = "level", value = "业务流程等级")
    private String level;
    @ApiModelProperty(name = "middleWareEntities", value = "中间件汇总")
    private List<MiddleWareEntity> middleWareEntities;
    @ApiModelProperty(name = "businessLinkDtos", value = "业务活动集合,支持嵌套")
    private List<BusinessLinkDto> businessLinkDtos;
}
