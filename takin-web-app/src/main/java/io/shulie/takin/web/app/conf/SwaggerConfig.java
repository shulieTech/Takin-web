/*
 * Copyright (c) 2021. Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.shulie.takin.web.app.conf;

import java.time.LocalDate;
import java.util.function.Predicate;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.PathProvider;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.DefaultPathProvider;
import springfox.documentation.spring.web.paths.Paths;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * The type Swagger config.
 *
 * @author shulie
 */
@Configuration
@EnableSwagger2WebMvc
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
@Order(0)
@Profile({"local","dev","test","web"})
public class SwaggerConfig {

    @Value("${server.servlet.context-path:}")
    private String servletContextPath;

    @Value("${swagger.enable:true}")
    private Boolean swaggerEnable;
    // 定义分隔符
    private static final String SPLITOR = ";";
    private static String AGENT_API_BASE_PATH = "io.shulie.takin.web.entrypoint.controller.agent";
    private static String AGENT_API_BASE_PATH2 = "io.shulie.takin.web.entrypoint.controller.perfomanceanaly";



    @Bean
    public Docket agentApi() {
        Predicate<RequestHandler> selectors = RequestHandlerSelectors.basePackage(AGENT_API_BASE_PATH);
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("压测平台-agent接口")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .apis(RequestHandlerSelectors.basePackage(AGENT_API_BASE_PATH))
            .paths(PathSelectors.any())
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .enable(swaggerEnable)
            .apiInfo(this.apiInfo());
    }


    @Bean
    public Docket all() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("压测平台-所有接口")
            .select().apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(PathSelectors.any())
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .enable(swaggerEnable)
            .apiInfo(this.apiInfo());
    }


    /**
     * v4.* 版本的都放在这里
     *
     * @return 文档
     */
    @Bean
    public Docket apiV4() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("压测平台-V4版本")
            .select().apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex(
                "/api/(scriptDebug|scriptManage|v2/file|config|probe|v2/application/node|agent/application/node/probe"
                    + "|application/middleware|login).*|/agent.*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(this.apiInfo()) .enable(swaggerEnable);
    }

    @Bean
    public Docket apiV41() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("压测平台-V4.1")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex("/api/(scenemanage|scene|settle|report|link/dictionary).*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .enable(swaggerEnable)
            .apiInfo(apiInfo())
            ;
    }

    @Bean
    public Docket apiV411() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("压测平台-V4.1.1")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex("/api/confcenter/wbmnt/(add|query|delete|update)/(blist|blistbyid).*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    @Bean
    public Docket apiV42() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("压测平台-V4.2.0")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            //  .paths(getRegex("/api/user/work/(bench|bench/access).*"))
            .paths(getRegex("/api/(agent|api|link).*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    @Bean
    public Docket apiV423() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("压测平台-V4.2.3")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            //  .paths(getRegex("/api/user/work/(bench|bench/access).*"))
            .paths(getRegex("/api/(global/switch|report/link|report/realtime|application/whitelist).*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    @Bean
    public Docket apiV430() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("压测平台-V4.3.0")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            //  .paths(getRegex("/api/user/work/(bench|bench/access).*"))
            .paths(getRegex("/api/auth.*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    @Bean
    public Docket apiV440() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("压测平台-V4.4.0")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex("/api/operation.*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    @Bean
    public Docket apiV461() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("压测平台-V4.6.1")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex("/api/(scriptManage|scenemanage).*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    @Bean
    public Docket apiV470() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("压测平台-V4.7.0")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex("/api/(leak|datasource).*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    @Bean
    public Docket apiV471() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("压测平台-V4.7.1")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex("/api/(fastdebug|fast/debug).*"))
            //.paths(Predicates.not(getRegex("/api/fastdebug/debug/callStack/(exception|node/locate).*\"")))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    @Bean
    public Docket apiV480() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("压测平台-V4.8.0")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex("/api/(application/names|application/entrances|activities).*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    @Bean
    public Docket apiBlacklist() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("压测平台-新版黑名单")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(PathSelectors.ant("/api/confcenter/blacklist/**"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    @Bean
    public Docket apiWhitelist() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("日常迭代-白名单新增接口20210414")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex("/api/(whitelist/list|application/part|application/global)/*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    @Bean
    public Docket apiLink4() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("链路梳理第4期")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex("/api/(consumers|application/whitelist).*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    @Bean
    public Docket accountManage() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("日常需求-3.11迭代")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex("/api/(user|dept).*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    @Bean
    public Docket api_openApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("压测平台-开放接口v01")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex(ApiUrls.TAKIN_OPEN_API_URL + ".*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    @Bean
    public Docket api_FastDebug2() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("tro-web-快速使用2期")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex("/api/(application/center/app/config|datasource|fastdebug/debug/callStack/node/locate|"
                + "fastdebug/debug/callStack/exception|link/ds/manage|application/plugins/config|opsScriptManage|sys|"
                + "pradar/switch|application/center/app).*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    @Bean
    public Docket api_etePatrol() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("ete巡检1期")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex("/api/patrol/manager(/board/create|/board/get|/board/delete|/board/update|"
                + "/scene/create|/scene/get|/scene/detail|/scene/delete|/scene/update|/scene/exception|/scene/start|"
                + "/init|/exception|/exception_config|/exception_notice|/assert/createOrUpdate|/assert/get|/node/get"
                + "|/node/add|/node/delete|/error/get).*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    @Bean
    public Docket api_etePatrolScreen() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("ete巡检大屏")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex("/api/patrol/screen(/all|/statistic|/detail).*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    @Bean
    public Docket api_businessFlow() {
        return new Docket(DocumentationType.SWAGGER_2)
                .pathProvider(this.pathProvider())
                .groupName("业务流程jmeter上传改造")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(getRegex("/api(/businessFlow|/application/entrances/bySamplerType|/activities/queryNormalActivities|/v2/scene).*"))
                .build()
                .directModelSubstitute(LocalDate.class, String.class)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                ;
    }

    @Bean
    public Docket api_reportQuery(){
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("混合压测场景-压测报告")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
//            .paths(getRegex("/api(/report/queryReportTrend|/report/tempReportDetail|/report/queryTempReportTrend|/report/queryNodeTree|/report/getReportByReportId|/report/businessActivity/summary/list|report/listReport).*"))
            .paths(getRegex("/api/report/.*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo())
            ;
    }

    /**
     * 两周迭代放在这里
     *
     * @return -
     */
    @Bean
    public Docket api_twoWeekIteration() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("6.4版本")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex(
                "/api/(application/remote/call|/application/remote/call/list|activities/virtual|activities|activities"
                    + "/activity|link/scene/manage).*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo()).enable(swaggerEnable)
            ;
    }

    /**
     * 快速接入一期测试用 nf
     *
     * @return
     */
    @Bean
    public Docket api_webTest_nf() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("快速接入一期测试用")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(PathSelectors
                .regex("/api/(v2/application/remote/call|v2/consumers|v2/link/ds|/application/remote/call/list).*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo())
            ;
    }

    /**
     * 探针在线升级 nf
     *
     * @return
     */
    @Bean
    public Docket agent_upgrade_online() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("探针在线升级")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
//            .paths(getRegex(
//                "/api/(agentReport|applicationPluginUpgrade|applicationPluginUpgradeRef|applicationTagRef"
//                    + "|pluginDependent|pluginLibrary|pluginTenantRef|agent/heartbeat).*"))
                .paths(getRegex("/api/(fast/agent/access/list/new|fast/agent/access/list/tips|" +
                        "fast/agent/access/errorLog/list|agent/heartbeat|plugin/path|plugin/upgrade|" +
                        "application/tag|pluginDependent|pluginLibrary|v2/user|tenantInfo/list).*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo())
            ;
    }

    /**
     * 业务域管理
     * @return
     */
    @Bean
    public Docket api_business_domain() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("业务域管理")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex("/api/domain.*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo())
            ;
    }

    /**
     * api info
     *
     * @return ApiInfo
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Takin API Document")
            .build();
    }

    /**
     * 重写 PathProvider ,解决 context-path 重复问题
     *
     * @return -
     */
    private PathProvider pathProvider() {
        return new DefaultPathProvider() {
            @Override
            public String getOperationPath(String operationPath) {
                operationPath = operationPath.replaceFirst(servletContextPath, "/");
                UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath("/");
                return Paths.removeAdjacentForwardSlashes(uriComponentsBuilder.path(operationPath).build().toString());
            }

            @Override
            public String getResourceListingPath(String groupName, String apiDeclaration) {
                apiDeclaration = super.getResourceListingPath(groupName, apiDeclaration);
                return apiDeclaration;
            }
        };
    }

    private Predicate<String> getRegex(String regex) {
        return PathSelectors.regex(servletContextPath + regex);
    }

}
