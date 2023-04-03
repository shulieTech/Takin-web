package io.shulie.takin.cloud.ext.content.enums;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liyuanba
 * @date 2021/10/13 3:11 下午
 */
public enum NodeTypeEnum {
    /**
     * HashTree
     */
    HASH_TREE("HashTree"),
    /**
     * TestPlan
     */
    TEST_PLAN("TestPlan"),
    /**
     * 线程组
     */
    THREAD_GROUP("ThreadGroup"),

    /**
     * HTTP请求默认值
     */
    CONFIG_TEST_ELEMENT("ConfigTestElement"),
    /**
     * 逻辑控制器
     */
    CONTROLLER("Controller"),
    /**
     * 取样器
     */
    SAMPLER("SamplerProxy", "Sampler", "Sample"),
    /**
     * 用户定义的变量
     */
    ARGUMENTS("Arguments"),
    /**
     * 信息头管理
     */
    HEADERMANAGER("HeaderManager"),
    /**
     * JSON提取器
     */
    POSTPROCESSOR("PostProcessor"),
    /**
     * 正则提取器
     */
    REGEXEXTRACTOR("RegexExtractor"),
    /**
     * 断言
     */
    ASSERTION("Assertion"),
    /**
     * CSV文件
     */
    DATASET("DataSet"),
    /**
     * 定时器
     */
    TIMER("Timer"),
    /**
     * 计数器
     */
    COUNTER("CounterConfig"),
    /**
     * BeanShell预处理程序
     */
    BEANSHELLPRE("BeanShellPreProcessor"),
    ;

    @Getter
    private String[] suffixes;

    NodeTypeEnum(String... suffixes) {
        this.suffixes = suffixes;
    }

    public static Map<String, NodeTypeEnum> pool = new HashMap<>();

    static {
        for (NodeTypeEnum e : NodeTypeEnum.values()) {
            for (String suffix : e.getSuffixes()) {
                if (pool.containsKey(suffix)) {
                    continue;
                }
                pool.put(suffix, e);
            }
        }
    }

    public static NodeTypeEnum value(String nodeName) {
        if (StringUtils.isBlank(nodeName)) {
            return null;
        }
        for (String suffix : pool.keySet()) {
            if (nodeName.endsWith(suffix)) {
                return pool.get(suffix);
            }
        }
        return null;
    }
}
