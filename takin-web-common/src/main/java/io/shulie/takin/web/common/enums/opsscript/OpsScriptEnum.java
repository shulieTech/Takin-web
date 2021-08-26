package io.shulie.takin.web.common.enums.opsscript;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 1=影子库表创建脚本 2=基础数据准备脚本 3=铺底数据脚本 4=影子库表清理脚本 5=缓存预热脚本
 */
@Getter
@AllArgsConstructor
public enum OpsScriptEnum {

    SHADOW_CREATE_SCRIPT(1, "影子库表创建脚本"),
    BASE_DATA_SCRIPT(2, "基础数据准备脚本"),
    LAY_DATA_SCRIPT(3, "铺底数据脚本"),
    SHADOW_CLEAN_SCRIPT(4, "影子库表清理脚本"),
    CACHE_SCRIPT(5, "缓存预热脚本");
    private Integer type;
    private String name;

    public static String getNameByType(Integer type) {
        for (OpsScriptEnum statusEnum : OpsScriptEnum.values()) {
            if (statusEnum.getType().equals(type)) {
                return statusEnum.getName();
            }
        }
        return "";
    }

}
