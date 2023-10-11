package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
* @Package io.shulie.takin.web.biz.pojo.request.scriptmanage
* @ClassName: ScriptCsvDataTemplateRequest
* @author hezhongqi
* @description:
* @date 2023/10/8 17:55
*/
@Data
public class ScriptCsvDataTemplateRequest  {
    @ApiModelProperty(name = "linkId", value = "业务活动Id")
    private Long linkId;

    @ApiModelProperty(name = "startTime", value = "开始时间")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @ApiModelProperty(name = "endTime", value = "结束时间")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    @ApiModelProperty(name = "appName", value = "应用名")
    private String appName;

    @ApiModelProperty(name = "endTime", value = "接口名")
    private String serviceName;

    @ApiModelProperty(name = "endTime", value = "方法名")
    private String methodName;

    @ApiModelProperty(name = "count", value = "数据条数")
    private Long count;

}
