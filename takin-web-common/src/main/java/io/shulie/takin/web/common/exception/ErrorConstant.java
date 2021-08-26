package io.shulie.takin.web.common.exception;

/**
 * @author caijianying
 */
public interface ErrorConstant {

    /**
     * 错误项目占位
     */
    String ERROR_PREFIX = "web-";

    //*************系统**************

    /**
     * 新增异常
     */
    String ADD_ERROR = "S0101";

    /**
     * 更新异常
     */
    String UPDATE_ERROR = "S0102";

    /**
     * 删除异常
     */
    String DELETE_ERROR = "S0103";

    /**
     * 数据处理异常
     */
    String DATA_PROCESS_ERROR = "S0104";

    /**
     * 文件比对异常
     */
    String FILE_COMPARE_ERROR = "S0105";

    /**
     * 文件通用校验异常
     */
    String FILE_VALIDATE_ERROR = "S0106";

    /**
     * 文件导入异常
     */
    String FILE_IMPORT_ERROR = "S0107";

    /**
     * 文件上传异常
     */
    String FILE_UPLOAD_ERROR = "S0108";

    /**
     * 文件上传异常
     */
    String FILE_CREATE_ERROR = "S0109";

    //*************用户**************

    /**
     * 校验异常
     */
    String VALIDATE_ERROR = "U0101";

    /**
     * 重复操作异常
     */
    String OPT_REPEAT_ERROR = "U0102";

    /**
     * 无数据权限
     */
    String NO_DATA_PERMISSION = "U0103";

    /**
     * 异常状态下操作
     */
    String STATUS_ERROR= "U0104";

    /**
     * 查询空数据列表产生的异常，目前主要场景在e2e(单纯地针对列表查询，不把查询结果拿来做校验的情况下使用)
     */
    String QUERY_NULL_ERROR= "U0105";

    /**
     * 查询过程中产生的未知异常
     */
    String QUERY_ERROR= "U0106";

    //*************第三方**************
    /**
     * 第三方调用异常
     */
    String CALL_THIRD_PARTY_ERROR = "T0101";

    /**
     * 第三方返回异常
     */
    String THIRD_PARTY_ERROR = "T0102";

}
