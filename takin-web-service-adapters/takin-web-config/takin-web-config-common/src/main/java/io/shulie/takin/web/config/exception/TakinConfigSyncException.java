package io.shulie.takin.web.config.exception;

/**
 * @author zhaoyong
 * takin配置信息异常类
 */
public class TakinConfigSyncException extends Exception {

    //序列号
    private static final long serialVersionUID = 1L;

    //异常信息
    private String message;

    /**
     * 构造方法
     */
    public TakinConfigSyncException(String massage) {
        super(massage);
    }

    /**
     * 构造方法
     *
     * @param cause 异常链
     */
    public TakinConfigSyncException(String massage, Throwable cause) {
        super(massage, cause);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
