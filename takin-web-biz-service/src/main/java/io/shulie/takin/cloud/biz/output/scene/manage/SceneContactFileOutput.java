package io.shulie.takin.cloud.biz.output.scene.manage;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * @author xr.l
 */
@Data
public class SceneContactFileOutput implements Serializable {

    private Long sceneId;
    private List<ContactFileInfo> files;

    @Data
    public static class ContactFileInfo {
        private String fileName;
        private String filePath;
        private Long size;
        private Integer isSplit;
        private Integer isOrderSplit;
    }
}


