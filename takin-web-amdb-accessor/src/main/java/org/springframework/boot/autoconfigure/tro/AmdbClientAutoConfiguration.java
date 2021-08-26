package org.springframework.boot.autoconfigure.takin;

import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author shiyajian
 * create: 2020-09-25
 */
@Configuration
@EnableConfigurationProperties(AmdbClientProperties.class)
public class AmdbClientAutoConfiguration {
}
