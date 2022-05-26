package io.shulie.takin.adapter.api.model.request.filemanager;

import java.util.List;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhaoyong
 * 文件删除参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileDeleteParamReq extends ContextExt {

    private List<String> paths;
}
