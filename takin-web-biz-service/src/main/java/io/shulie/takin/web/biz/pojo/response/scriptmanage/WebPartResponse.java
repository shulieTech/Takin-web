/**
 * Copyright (C) 2013 CLXY Studio.
 * This content is released under the (Link Goes Here) MIT License.
 * http://en.wikipedia.org/wiki/MIT_License
 */
package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import java.util.List;

import io.shulie.takin.web.data.result.filemanage.FileManageResult;
import lombok.Data;

/**
 * @author hengyu
 */
@Data
public class WebPartResponse {

    private List<FileManageResult> fileManageResults;
}
