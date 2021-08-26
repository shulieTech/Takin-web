package com.pamirs.takin.entity.domain.dto.linkmanage.mapping;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums.LinkDomainEnum;

/**
 * @author  vernon
 * @date 2019/12/23 22:40
 */
public class LinkDomainEnumMapping {
    public static LinkDomainEnum getByCode(String code) {
        for (LinkDomainEnum linkDomainEnum : LinkDomainEnum.values()) {
            if (linkDomainEnum.getCode().equalsIgnoreCase(code)) {
                return linkDomainEnum;
            }
        }
        return null;
    }
}
