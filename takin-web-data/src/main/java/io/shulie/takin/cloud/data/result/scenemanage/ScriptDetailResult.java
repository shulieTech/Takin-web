package io.shulie.takin.cloud.data.result.scenemanage;

import io.shulie.takin.cloud.common.enums.machine.EnumResult;
import lombok.Data;

/**
 * @author qianshui
 * @date 2020/5/18 下午11:42
 */
@Data
public class ScriptDetailResult {

    private String fileName;

    private String uploadTime;

    private EnumResult fileType;

    private Long uploadedData;

    private EnumResult isSplit;
}
