package io.shulie.takin.web.common.constant;

/**
 * 探针相关常量池
 *
 * @author liuchuan
 * @date 2021/6/7 5:32 下午
 */
public interface ProbeConstants {

    /**
     * 未安装英文描述
     */
    String ENGLISH_DESC_NOT_INSTALLED = "notInstalled";

    /**
     * 探针版本号最低版本
     * 5.0.0 --> 500
     * 5.0.1 ✓
     * 5.0.0.1 ✓
     */
    int[] PROBE_VERSION_BASE = {5, 0, 0};

    /**
     * agent 版本号 新旧分水岭
     * 5.0.0 --> 500
     * 5.0.1 ✓
     * 5.0.0.1 ✓
     * 4.9.9.1 x
     */
    int[] AGENT_VERSION_BASE = {5, 0, 0};

    /**
     * 升级失败描述
     */
    String PROBE_OPERATE_RESULT_DESC_FAILED_UPGRADE = "您上次未升级成功";

     /**
     * 卸载失败描述
     */
    String PROBE_OPERATE_RESULT_DESC_FAILED_UNINSTALL = "您上次未卸载成功";

    /**
     * 安装失败描述
     */
    String PROBE_OPERATE_RESULT_DESC_FAILED_INSTALL = "您上次未安装成功";

    /**
     * 探针操作结果, 成功
     */
    int PROBE_OPERATE_RESULT_SUCCESS = 1;

    /**
     * 探针操作结果, 失败
     */
    int PROBE_OPERATE_RESULT_FAILED = 0;

    /**
     * 探针操作结果, 无
     */
    int PROBE_OPERATE_RESULT_NONE = 99;

    /**
     * 文件夹 probe
     */
    String FOLDER_PROBE = "probe";

    /**
     * 已安装 类型
     */
    Integer INSTALLED_TYPE = 1;

    /**
     * 未安装 类型
     */
    Integer UNINSTALLED_TYPE = 0;

    /**
     * zip 文件
     */
    String FILE_TYPE_ZIP = "zip";

    /**
     * gz 文件
     */
    String FILE_TYPE_GZ = "gz";

    /**
     * tar.gz 文件
     */
    String FILE_TYPE_TAR_GZ = "tar.gz";

}
