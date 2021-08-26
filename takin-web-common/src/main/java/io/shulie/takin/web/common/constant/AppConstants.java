package io.shulie.takin.web.common.constant;

/**
 * app 常量池
 *
 * @author liuchuan
 * @date 2021/4/27 10:44 上午
 */
public interface AppConstants {

    /**
     * 不能为 null
     */
    String MUST_NOT_BE_NULL = " must not be null!";

    /**
     * 不能为 empty
     */
    String MUST_NOT_BE_EMPTY = " must not be empty!";

    /**
     * 文件权限 读写
     */
    String FILE_PERMISSION_RW = "rw";

    /**
     * 英文句号
     */
    String ENGLISH_PERIOD = "\\.";

    /**
     * 分号
     */
    String SEMICOLON = ";";

    /**
     * springboot local环境
     */
    String ACTIVE_PROFILE_LOCAL = "local";

    /**
     * springboot dev环境
     */
    String ACTIVE_PROFILE_DEV = "dev";

    /**
     * springboot test环境
     */
    String ACTIVE_PROFILE_TEST = "test";

    /**
     * 问号
     */
    String QUESTION_MARK = "?";

    /**
     * 脱敏后的密码, 8个*
     */
    String PASSWORD_COVER = "********";

    /**
     * xml 下的密码, %s 占位符密码
     */
    String PASSWORD_XML = "<property name=\"password\" value=\"%s\"/>";

    /**
     * json 下的密码, %s 占位符密码
     */
    String PASSWORD_JSON = "\"password\":\"%s\"";

    /**
     * 操作频繁提示
     */
    String TOO_FREQUENTLY = "操作太频繁!";

    /**
     * 必须填写提示语
     */
    String MUST_BE_NOT_NULL = " 必须填写!";

    /**
     * 是
     */
    int YES = 1;

    /**
     * 否
     */
    int NO = 0;

    /**
     * 横
     */
    String ACROSS = "-";

    /**
     * 等号
     */
    String EQUAL_MARK = "=";

    /**
     * 空字符串
     */
    String BLANK_STRING = "";

    /**
     * 逗号
     */
    String COMMA = ",";

    /**
     * 时间格式, 字符串形式
     */
    String DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";

}
