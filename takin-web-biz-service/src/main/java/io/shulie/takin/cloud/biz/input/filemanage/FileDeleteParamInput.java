package io.shulie.takin.cloud.biz.input.filemanage;

import java.util.List;

import lombok.Data;

/**
 * @author zhaoyong
 * 文件删除参数
 */
@Data
public class FileDeleteParamInput {

    List<String> paths;
}
