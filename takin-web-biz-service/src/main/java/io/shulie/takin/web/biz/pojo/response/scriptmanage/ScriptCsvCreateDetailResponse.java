package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import lombok.Data;

/**
* @Package io.shulie.takin.web.biz.pojo.response.scriptmanage
* @ClassName: ScriptCsvCreateDetailResponse
* @author hezhongqi
* @description:
* @date 2023/9/21 14:20
*/
@Data
public class ScriptCsvCreateDetailResponse {

    private ScriptCsvDataSetResponse scriptCsvDataSetResponse;

    private ScriptCsvCreateTaskResponse scriptCsvCreateTaskResponse;

    private ScriptCsvDataTemplateResponse scriptCsvDataTemplateResponse;
}



