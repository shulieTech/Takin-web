package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import lombok.Data;

/**
* @Package io.shulie.takin.web.biz.pojo.request.scriptmanage
* @ClassName: ScriptCsvAliasNameUpdateRequest
* @author hezhongqi
* @description:
* @date 2023/10/8 10:03
*/
@Data
public class ScriptCsvAliasNameUpdateRequest  {

    private Long fileManageId;
    private String aliasName;
}
