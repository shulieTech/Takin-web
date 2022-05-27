package io.shulie.takin.web.biz.pojo.dto.scene;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 查询运行报告的tps, 所需参数
 *
 * @author liuchuan
 * @date 2021/6/11 1:33 下午
 */
@Data
public class QuerySceneReportTpsDTO {

    @ApiModelProperty("场景id")
    private Long sceneId;

    @ApiModelProperty("场景名称")
    private String sceneName;

}
