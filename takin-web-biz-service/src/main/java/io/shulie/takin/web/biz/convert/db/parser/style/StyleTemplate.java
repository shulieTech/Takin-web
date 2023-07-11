package io.shulie.takin.web.biz.convert.db.parser.style;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/9/13 2:19 下午
 */
public interface StyleTemplate {


    String PREFIX = "pt_";

    String INPUT_FILE_NAME_USER_NAME = "shadowUserName";

    String INPUT_FILE_NAME_URL = "shadowUrl";

    String PWD_FILE_NAME = "shadowPwd";

    String INPUT_FILE_NAME_USER_NAME_CONTEXT= "影子数据源用户名";

    String INPUT_FILE_NAME_URL_CONTEXT = "影子数据源";

    String INPUT_FILE_NAME_URL_CONTEXT_REDIS = "影子集群";

    String REDIS_SHADOW_CONFIG = "shadowConfig";

    String REDIS_SHADOW_CONFIG_CONTEXT = "影子配置";

    String PWD_FILE_NAME_CONTEXT = "影子数据源密码";

    String DRIVER_CLASSNAME = "driverClassName";

    String DRIVER_CLASSNAME_SHOW_NAME = "驱动";

    String key1 = "跟随业务配置";

    String key2 = "自定义";

    String key3 = "tag";

    String key4 = "context";

    String key5 = "value";

    String key6 = "SHADOW_REDIS_";


    @AllArgsConstructor
    @Getter
    enum StyleEnums {
        INPUT(1, "输入框"),


        PWD_INPUT(2, "密码框"),


        SELECT_WITH_INPUT(3, "下拉选择后输入"),

        TABLE(4, "表单"),

        TEXT_INPUT(5,"文本框");



        private final Integer code;

        private final String desc;

        /**
         * 根据 desc 获得枚举
         *
         * @param desc desc
         * @return 枚举
         */
        public static StyleEnums getEnumByDesc(String desc) {
            return Arrays.stream(values())
                    .filter(styleEnum -> styleEnum.getDesc().equals(desc))
                    .findFirst().orElse(null);
        }

        /**
         * 根据 code 获得枚举
         *
         * @param code code
         * @return 枚举
         */
        public static StyleEnums getEnumByCode(Integer code) {
            return Arrays.stream(values())
                    .filter(styleEnum -> styleEnum.getCode().equals(code))
                    .findFirst().orElse(null);
        }

        /**
         * 根据 code, 获得描述
         *
         * @param code code
         * @return 描述
         */
        public static String getDescByCode(Integer code) {
            return Arrays.stream(values())
                    .filter(styleEnum -> styleEnum.getCode().equals(code))
                    .findFirst()
                    .map(StyleEnums::getDesc).orElse("");
        }
    }

    /**
     * 输入框组件
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class InputStyle extends AbstractStyleTemplate{

        private String key;

        private String label;

        private Integer nodeType;

        private Boolean required;

        public InputStyle(String key, String label, Integer nodeType) {
            this.key = key;
            this.label = label;
            this.nodeType = nodeType;
            this.required = true;
        }
    }

    /**
     * 下拉框选择后联动输入框 组件
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class InputWithSelectStyle extends AbstractStyleTemplate{


        private String key;

        private String label;

        private Integer nodeType;

        private InputWithSelectStyle.NodeInfo nodeInfo;

        private Boolean required;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class NodeInfo{

            private List<String> keys;

            private List<InputWithSelectStyle.NodeDetail> dataSource;
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class NodeDetail{


            private String label;

            private String value;
        }

        public InputWithSelectStyle(String key, String label, Integer nodeType, NodeInfo nodeInfo) {
            this.key = key;
            this.label = label;
            this.nodeType = nodeType;
            this.nodeInfo = nodeInfo;
            this.required = true;
        }
    }

    /**
     * 表单组件
     */
    @Data
    @AllArgsConstructor
    class ListStyle extends AbstractStyleTemplate{


        private String key;

        private String label;

        private Integer nodeType;

        private Boolean required;


        public ListStyle() {
            this.key = "shaDowTaleInfo";
            this.label ="";
            this.nodeType = StyleEnums.TABLE.getCode();
            this.required = true;
        }
    }

    /**
     * 输入框带tips组件
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class TipsInputStyle extends InputStyle {

        private String tips;

        public TipsInputStyle(String key, String label, Integer nodeType, String tips) {
            super(key, label, nodeType);
            this.tips = tips;
        }
    }
}
