package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
* @Package io.shulie.takin.web.biz.pojo.response.scriptmanage
* @ClassName: ScriptCsvDataTemplateResponse
* @author hezhongqi
* @description:
* @date 2023/9/21 14:24
*/
@Data
public class ScriptCsvDataTemplateResponse {


    private List<TemplateDTO> templateDTO;

    private Long count;

    /**
     * 当前时间段总数
     */
    private Long total;

    //------------入参内容------------

    private String appName;

    private String serviceName;

    private String methodName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    @Data
    public static class TemplateDTO {
        private String title;
        private String key;
        private List<TemplateDTO> children;
    }

}



