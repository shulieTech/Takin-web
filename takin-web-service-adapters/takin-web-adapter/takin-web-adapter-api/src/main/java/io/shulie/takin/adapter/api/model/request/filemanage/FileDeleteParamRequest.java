package io.shulie.takin.adapter.api.model.request.filemanage;

import java.util.List;

import lombok.Data;

/**
 * @author zhaoyong
 * 文件删除参数
 */
@Data
public class FileDeleteParamRequest {

    List<String> paths;
}
