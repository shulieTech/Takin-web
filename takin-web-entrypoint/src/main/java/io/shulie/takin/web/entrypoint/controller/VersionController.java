package io.shulie.takin.web.entrypoint.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.plugin.framework.core.configuration.IConfiguration;
import io.shulie.takin.web.biz.service.sys.VersionService;
import io.shulie.takin.web.common.constant.ApiUrls;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.pf4j.RuntimeMode;
import org.pf4j.util.FileUtils;
import org.pf4j.util.JarFileFilter;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "sys")
public class VersionController {

    @Resource
    private VersionService versionService;

    @Resource
    private PluginManager pluginManager;

    private static final String GIT_PROPERTIES_FILE = "git.properties";
    private static final String WEB = "takin-web";
    private static final String EE = "takin-web-ee";
    private static final String E2E = "takin-e2e";

    private static Map<String, Properties> VERSIONS = null;

    @GetMapping("version")
    public ResponseResult<Object> version() {
        return ResponseResult.success(versionService.selectVersion());
    }

    @PutMapping("version/confirm")
    public ResponseResult<Boolean> confirm() {
        versionService.confirm();
        return ResponseResult.success(true);
    }

    @GetMapping("codeVersion")
    public ResponseResult<Map<String, Properties>> gitVersion() {
        if (VERSIONS != null) {
            return ResponseResult.success(VERSIONS);
        }
        Map<String, Properties> pluginProperties = readPluginProperties();
        pluginProperties.put(WEB, readMainProperties());
        VERSIONS = pluginProperties;
        return ResponseResult.success(VERSIONS);
    }

    private Properties readMainProperties() {
        Properties properties = new Properties();
        org.springframework.core.io.Resource resource = new DefaultResourceLoader()
            .getResource("classpath:" + GIT_PROPERTIES_FILE);
        if (resource.exists()) {
            try (InputStream stream = resource.getInputStream()) {
                properties.load(stream);
            } catch (Exception ignore) {
            }
        }
        return properties;
    }

    // copy from org.pf4j.PropertiesPluginDescriptorFinder.readProperties
    private Map<String, Properties> readPluginProperties() {
        Map<String, Properties> versions = new HashMap<>(4);
        Properties eeProperties = new Properties();
        Properties e2eProperties = new Properties();
        versions.put(EE, eeProperties);
        versions.put(E2E, e2eProperties);
        List<Path> pluginPaths = getPluginPaths();
        if (CollectionUtils.isNotEmpty(pluginPaths)) {
            pluginPaths.stream().filter(path -> path.toString().contains(EE))
                .findFirst().ifPresent(path -> readProperties(path, eeProperties));
            pluginPaths.stream().filter(path -> path.toString().contains(E2E))
                .findFirst().ifPresent(path -> readProperties(path, e2eProperties));
        }
        return versions;
    }

    // copy from org.pf4j.PropertiesPluginDescriptorFinder.readProperties
    private void readProperties(Path path, Properties properties) {
        try (InputStream input = Files.newInputStream(getPropertiesPath(path))) {
            properties.load(input);
        } catch (Exception ignore) {
        }
    }

    // org.pf4j.PropertiesPluginDescriptorFinder.getPropertiesPath
    private Path getPropertiesPath(Path pluginPath) {
        // it's a jar file
        try {
            return FileUtils.getPath(pluginPath, GIT_PROPERTIES_FILE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // copy from org.pf4j.BasePluginRepository.getPluginPaths
    private List<Path> getPluginPaths() {
        return streamFiles().map(File::toPath).collect(Collectors.toList());
    }

    // copy from io.shulie.takin.plugin.framework.core.pf4j.Pf4jPluginFactory.getPluginPath
    private Path getPluginPathRoot() {
        IConfiguration configuration = pluginManager.getConfiguration();
        String pluginPath = configuration.getPluginPath();
        if (StringUtils.isBlank(pluginPath)) {
            return configuration.environment() == RuntimeMode.DEVELOPMENT
                ? Paths.get("./plugins/") : Paths.get("plugins");
        } else {
            return Paths.get(pluginPath);
        }
    }

    // copy from org.pf4j.BasePluginRepository.streamFiles
    private Stream<File> streamFiles() {
        File[] files = getPluginPathRoot().toFile().listFiles(new JarFileFilter());
        return files != null ? Arrays.stream(files) : Stream.empty();
    }
}