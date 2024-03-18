package io.shulie.takin.cloud.biz.input.scenemanage;

import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import lombok.Data;

import java.util.List;

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

    public static FileInfo genFileInfo(SceneManageWrapperOutput.SceneScriptRefOutput file){
        FileInfo info = new FileInfo();
        info.setFileName(file.getFileName());
        info.setSplit(file.getIsSplit() != null && file.getIsSplit() == 1);
        return info;
    }
}
