package com.pamirs.takin.common.enums;

public enum ResultCodeEnum {

    INVOKE_RESULT_SUCCESS("00", "返回成功", ResponseResultEnum.RESP_SUCCESS),
    INVOKE_RESULT_FAILED("01", "返回失败，一般是业务失败", ResponseResultEnum.RESP_FAILURE),
    INVOKE_RESULT_BIZ_ERR("02", "返回业务错误", ResponseResultEnum.RESP_FAILURE),
    INVOKE_RESULT_TIMEOUT("03", "返回超时错误", ResponseResultEnum.RESP_FAILURE),
    INVOKE_RESULT_UNKNOWN("04", "未知", ResponseResultEnum.RESP_FAILURE),
    INVOKE_ASSERT_RESULT_FAILED("05", "断言失败", ResponseResultEnum.ASSERT_FAILURE),
    SYSTEM_ERROR_CODE("500", "系统错误", ResponseResultEnum.RESP_FAILURE),
    ;

    public String code;

    public String desc;

    public ResponseResultEnum result;

    ResultCodeEnum(String code, String desc, ResponseResultEnum result) {
        this.code = code;
        this.desc = desc;
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public ResponseResultEnum getResult() {
        return result;
    }

    public static ResultCodeEnum getResultCodeEnumByCode(String code) {
        for(ResultCodeEnum responseCode : ResultCodeEnum.values()) {
            if(responseCode.getCode().equals(code)) {
                return responseCode;
            }
        }
        return null;
    }
}
