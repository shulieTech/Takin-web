package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums;

import lombok.Getter;

/**
 * @author  vernon
 * @date 2019/12/23 22:29
 */
@Getter
public enum LinkDomainEnum {
    ORDER_DOMAIN("0", "订单域"),

    WAYBILL_DOMAIN("1", "运单域"),

    CHARGE_DOMAIN("2", "结算域");

    private String code;
    private String desc;

    LinkDomainEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
