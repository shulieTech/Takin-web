package io.shulie.takin.web.app.conf.mybatis;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* @Package io.shulie.takin.web.app.conf.mybatis
* @ClassName: MyBatisPlusConfig
* @author hezhongqi
* @description:
* @date 2021/11/2 16:55
*/

@Configuration
public class MyBatisPlusConfig {

//


    /**
     * 新多租户插件配置,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存万一出现问题
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new TakinTenantLineInnerInterceptor(new TakinTenantLineHandler() {
            @Override
            public Expression getEnvCode() {
                if (StringUtils.isNotBlank(WebPluginUtils.traceEnvCode())) {
                    return new StringValue(WebPluginUtils.traceEnvCode());
                }else {
                    return new StringValue(WebPluginUtils.DEFAULT_ENV_CODE);
                }
            }

            //@Override
            //public Expression getUserId() {
            //    if (WebPluginUtils.traceUserId() != null) {
            //        return new LongValue(WebPluginUtils.traceUserId());
            //    }else {
            //        return new LongValue(WebPluginUtils.DEFAULT_USER_ID);
            //    }
            //}

            @Override
            public Expression getTenantId() {
                if (WebPluginUtils.traceTenantId() != null) {
                    return new LongValue(WebPluginUtils.traceTenantId());
                }else {
                    return new LongValue(WebPluginUtils.DEFAULT_TENANT_ID);
                }
            }

            // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
            @Override
            public boolean ignoreTable(String tableName) {
                //return !"user".equalsIgnoreCase(tableName);
                return false;
            }
        }));
        // 如果用了分页插件注意先 add TenantLineInnerInterceptor 再 add PaginationInnerInterceptor
        // 用了分页插件必须设置 MybatisConfiguration#useDeprecatedExecutor = false
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());

        return interceptor;
    }

    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> configuration.addInterceptor(new com.github.pagehelper.PageInterceptor());
    }

    @Bean
    public MySqlInjector sqlInjector() {
        return new MySqlInjector();
    }


//    @Bean
//    @ConditionalOnMissingBean
//    public MetaSelectSignInterceptor selectSignInterceptor() {
//        return new MetaSelectSignInterceptor();
//    }

//    @Bean
//    @ConditionalOnMissingBean
//    public MetaUpdateSignInterceptor updateSignInterceptor() {
//        return new MetaUpdateSignInterceptor();
//    }



}

