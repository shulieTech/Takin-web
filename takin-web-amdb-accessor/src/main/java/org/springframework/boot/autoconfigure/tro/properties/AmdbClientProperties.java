package org.springframework.boot.autoconfigure.takin.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * amdb 配置
 *
 * @author shiyajian
 * create: 2020-09-25
 */
@ConfigurationProperties(prefix = "amdb")
@Component
@Data
public class AmdbClientProperties {

    public static final String VERSION_PATH="/amdb/getVersion";

    private UrlBean url;

    @Data
    public static class UrlBean {
        /**
         * pradar url
         */
        private String pradar;

        /**
         * amdb url
         */
        private String amdb;
    }

}
