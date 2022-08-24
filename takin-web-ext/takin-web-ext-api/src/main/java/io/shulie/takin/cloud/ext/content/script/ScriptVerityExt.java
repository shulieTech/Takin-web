package io.shulie.takin.cloud.ext.content.script;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class ScriptVerityExt {

    /**
     * 业务请求列表
     */
    private List<String> request;

    /**
     * 脚本路径
     */
    private String scriptPath;
    /**
     * 脚本版本(管理版本)
     * <p>混合压测前是0</p>
     * <p>混合压测后是1</p>
     */
    private Integer version;

    private boolean fromScene;
    private boolean useNewVerify;
    private List<String> watchmanIdList; // 压力机集群Id
    // 脚本文件路径, 此处是单个
    private List<FileVerifyItem> scriptPaths = new ArrayList<>();
    // 插件路径，多个按逗号分隔
    private List<FileVerifyItem> pluginPaths = new ArrayList<>();
    // 数据文件路径，多个按逗号分隔
    private List<FileVerifyItem> csvPaths = new ArrayList<>();
    // 附件文件路径，多个按逗号分隔
    private List<FileVerifyItem> attachments = new ArrayList<>();

    @Data
    public static class FileVerifyItem {
        private String path;
        private String md5;
        private boolean calcFullPath;
        private String rootPath;
        private boolean bigFile;
        private boolean inner;

        public FileVerifyItem(String path, String md5) {
            this.path = path;
            this.md5 = md5;
        }

        public FileVerifyItem(String path, String md5, boolean bigFile) {
            this.path = path;
            this.md5 = md5;
            this.bigFile = bigFile;
        }

        public FileVerifyItem(boolean inner, String path, String md5) {
            this.path = path;
            this.md5 = md5;
            this.inner = inner;
        }

        public String getFullPath() {
            return calcFullPath ? rootPath + "/" + path : path;
        }
    }
}
