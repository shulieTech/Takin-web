package io.shulie.takin.cloud.biz.output.scenetask;

import java.util.List;

import lombok.Data;

/**
 * @author moriarty
 */
@Data
public class SceneTaskStartCheckOutput {
    private Boolean hasUnread;
    private List<FileReadInfo> fileReadInfos;

    @Data
    public static class FileReadInfo {
        private String fileName;
        private String fileSize;
        private String readSize;
    }
}
