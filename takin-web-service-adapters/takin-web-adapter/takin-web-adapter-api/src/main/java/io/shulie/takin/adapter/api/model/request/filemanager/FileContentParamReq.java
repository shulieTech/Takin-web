package io.shulie.takin.adapter.api.model.request.filemanager;

import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2020/12/9 10:50 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileContentParamReq extends ContextExt {
    private List<String> paths;
}
