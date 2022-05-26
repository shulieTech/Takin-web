package io.shulie.takin.cloud.common.enums.machine;

/**
 * @author fanxx
 * @date 2020/5/13 下午5:25
 */
public enum MachineTaskStatusEnum {
    /**
     * 1、开通中 2、开通失败 3、开通成功 4、销毁中 5、销毁失败 6、销毁成功
     */
    do_open(1, "开通中"),
    open_failed(2, "开通失败"),
    open_success(3, "开通成功"),
    do_destory(4, "销毁中"),
    destory_failed(5, "销毁失败"),
    destory_success(6, "销毁成功");
    private Integer code;
    private String name;

    MachineTaskStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getStatus() {
        return name;
    }

    public void setStatus(String status) {
        this.name = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
