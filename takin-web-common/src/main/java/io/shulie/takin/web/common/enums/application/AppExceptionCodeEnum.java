package io.shulie.takin.web.common.enums.application;

import lombok.Getter;

/**
 * @author 无涯
 * @date 2021/1/7 9:30 下午
 */
@Getter
public enum  AppExceptionCodeEnum {
    EPC0001("EPC0001","应用在线节点数与节点总数不一致"),
    EPC0002("EPC0002","应用各实例 Agent 版本不一致"),
    ;

    AppExceptionCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;
}
