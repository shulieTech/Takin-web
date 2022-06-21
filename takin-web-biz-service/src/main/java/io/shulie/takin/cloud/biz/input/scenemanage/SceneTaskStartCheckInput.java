package io.shulie.takin.cloud.biz.input.scenemanage;

import java.util.List;

import lombok.Data;

/**
 * @author xr.l
 */
@Data
public class SceneTaskStartCheckInput {
    private Long sceneId;
    private Long scriptId;
    private Integer podNum;
    private List<FileInfo> fileInfoList;

    @Data
    public static class FileInfo {
        private String fileName;
        private boolean isSplit;
    }
}
