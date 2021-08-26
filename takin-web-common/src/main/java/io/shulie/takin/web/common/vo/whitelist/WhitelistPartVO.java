package io.shulie.takin.web.common.vo.whitelist;

import java.util.List;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/12 5:30 下午
 */
@Data
public class WhitelistPartVO {
    /**
     * 全部应用
     */
    private List<String> allAppNames;
    /**
     * 生效应用
     */
    private List<String> effectiveAppNames;
}
