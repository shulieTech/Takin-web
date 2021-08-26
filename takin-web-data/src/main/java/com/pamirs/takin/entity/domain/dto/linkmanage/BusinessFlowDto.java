package com.pamirs.takin.entity.domain.dto.linkmanage;

import java.util.List;

import com.pamirs.takin.entity.domain.vo.linkmanage.BusinessFlowTree;
import com.pamirs.takin.entity.domain.vo.linkmanage.MiddleWareEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2020/1/4 00:33
 */
@Data
@ApiModel(value = "BusinessFlowDto", description = "业务流程详情出参(最新")

public class BusinessFlowDto {
    @ApiModelProperty(name = "id", value = "主键")
    private String id;
    @ApiModelProperty(name = "businessProcessName", value = "业务流程名字")
    private String businessProcessName;
    @ApiModelProperty(name = "isCode", value = "是否核心流程")
    private String isCode;
    @ApiModelProperty(name = "level", value = "业务流程等级")
    private String level;
    @ApiModelProperty(name = "middleWareEntities", value = "中间件汇总")
    private List<MiddleWareEntity> middleWareEntities;
    @ApiModelProperty(name = "roots", value = "业务流程节点树")
    private List<BusinessFlowTree> roots;
    @ApiModelProperty(name = "existBusinessActive", value = "已经存在的业务活动对象集合")
    private List<ExistBusinessActiveDto> existBusinessActive;
}
