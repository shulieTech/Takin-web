package com.pamirs.takin.common.constant;

import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

/**
 * 说明: 白名单规则枚举类
 *
 * @author shulie
 * @version v1.0
 * @date Create in 2019/3/1 15:42
 */
public enum WListRuleEnum {

    /**
     * NVAS-vas-cas-web 应用过滤规则
     */
    NVAS_VAS_CAS_WEB("NVAS-vas-cas-web", "/vas-cas-web/login;JSESSIONID="),

    /**
     * NVAS-vas-cas-web(域名为vip.pamirs.com:8080) 应用过滤规则
     */
    VIP_NVAS_VAS_CAS_WEB("NVAS-vas-cas-web", "http://vip.pamirs.com:8080/vas-cas-web/login;JSESSIONID="),

    /**
     * cubc 应用过滤规则
     */
    CUBC("cubc", "http://cubc.pamirs.com/cubc/codaccountmanage/queryModifyParamsById"),

    /**
     * wms-dpjjwms 应用过滤规则
     */
    WMS_DPJJWMS("wms-dpjjwms", "http://dpjjwms.pamirs.com/dpjjwms/"),
    ;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 该应用名称的过滤规则
     */
    private String rule;

    private WListRuleEnum(String appName, String rule) {
        this.appName = appName;
        this.rule = rule;
    }

    /**
     * 说明: 判断枚举值是否在定义范围内
     *
     * @param
     * @return
     * @author shulie
     * @date 2019/3/1 16:02
     */
    public static boolean checkDataSourceEnum(WListRuleEnum wListRuleEnum) {
        return Stream.of(WListRuleEnum.values())
            .anyMatch(wListRuleEnum1 -> wListRuleEnum1.equals(wListRuleEnum));
    }

    /**
     * 说明: 获取该过滤的app名称集合
     *
     * @return 过滤的app名称集合
     * @author shulie
     * @date 2019/3/1 16:12
     */
    public static Set<String> getAppNameList() {
        Set<String> ruleAppNameList = Sets.newHashSetWithExpectedSize(3);
        Stream.of(WListRuleEnum.values()).forEach(wListRuleEnum -> {
            ruleAppNameList.add(wListRuleEnum.getAppName());
        });

        return ruleAppNameList;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

}
