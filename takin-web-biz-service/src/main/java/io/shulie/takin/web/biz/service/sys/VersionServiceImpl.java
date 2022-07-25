package io.shulie.takin.web.biz.service.sys;

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
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.shulie.surge.data.common.zk.ZkClient;
import io.shulie.surge.data.common.zk.ZkClient.CreateMode;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.plugin.framework.core.configuration.IConfiguration;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.data.dao.sys.VersionDAO;
import io.shulie.takin.web.data.model.mysql.VersionEntity;
import io.shulie.takin.web.data.result.system.VersionVo;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.RuntimeMode;
import org.pf4j.util.FileUtils;
import org.pf4j.util.JarFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VersionServiceImpl implements VersionService {

    @Value("${takin.web.version:}")
    private String version;

    @Value("${takin.web.upgrade.ignore-snapshot:true}")
    private boolean ignoreSnapshot;

    @Resource
    private VersionDAO versionDAO;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private PluginManager pluginManager;

    @Autowired
    private ZkClient zkClient;

    /**
     * 发布新版本，先更新一下旧版本信息
     *
     * @param entity 版本信息
     * @return true-发布版本成功
     */
    @Override
    public boolean publish(VersionEntity entity) {
        boolean publishSuccess = versionDAO.publish(entity);
        afterPublish(publishSuccess, entity);
        return publishSuccess;
    }

    @Override
    public VersionVo selectVersion() {
        ValueOperations opsForValue = redisTemplate.opsForValue();
        String cacheKey = getCacheKey();
        String version = (String)opsForValue.get(cacheKey);
        Long userId = WebPluginUtils.traceUserId();
        if (userId == null || userId < 0) {
            userId = 0L;
        }
        boolean show = !(ignore() || opsForValue.getBit(getConfirmKey(), userId));
        if (StringUtils.isNotBlank(version)) {
            VersionVo vo = JsonHelper.json2Bean(version, VersionVo.class);
            vo.setShow(show);
            return vo;
        }
        VersionVo vo = new VersionVo();
        vo.setHasNew(false);
        VersionEntity least = versionDAO.selectLeast();
        if (least != null) {
            VersionEntity preVersion = versionDAO.selectLeastOnVersionCondition(false, least.getVersion());
            vo.setPreVersion(preVersion);
        }
        vo.setCurVersion(least);
        opsForValue.set(cacheKey, JsonHelper.bean2Json(vo));
        vo.setShow(show);
        return vo;
    }

    @Override
    public void confirm() {
        redisTemplate.opsForValue().setBit(getConfirmKey(), WebPluginUtils.traceUserId(), true);
    }

    @Override
    public boolean ignore() {
        return StringUtils.isBlank(version) || (ignoreSnapshot && StringUtils.endsWithIgnoreCase(version, "SNAPSHOT"));
    }

    @Override
    public Map<String, Properties> gitVersions() {
        Map<String, Properties> versionProperties = readPluginProperties();
        versionProperties.put(WEB, readMainProperties());
        return versionProperties;
    }

    @Override
    public void initGitVersion() {
        zkClient.deleteQuietly(WEB_REGISTER_PATH);
        zkClient.deleteQuietly(WEB_EE_REGISTER_PATH);
        zkClient.deleteQuietly(WEB_E2E_REGISTER_PATH);
        try {
            Map<String, Properties> versions = gitVersions();
            Properties web = versions.get(WEB);
            if (Objects.nonNull(web)) {
                zkClient.ensureParentExists(WEB_REGISTER_PATH);
                zkClient.createNode(WEB_REGISTER_PATH, JSON.toJSONBytes(web), CreateMode.PERSISTENT);
            }
            Properties ee = versions.get(EE);
            if (Objects.nonNull(ee)) {
                zkClient.ensureParentExists(WEB_EE_REGISTER_PATH);
                zkClient.createNode(WEB_EE_REGISTER_PATH, JSON.toJSONBytes(ee), CreateMode.PERSISTENT);
            }
            Properties e2e = versions.get(E2E);
            if (Objects.nonNull(e2e)) {
                zkClient.ensureParentExists(WEB_E2E_REGISTER_PATH);
                zkClient.createNode(WEB_E2E_REGISTER_PATH, JSON.toJSONBytes(e2e), CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            log.error("注册版本信息异常", e);
        }
    }

    @Override
    public Map<String, String> queryZkVersionData() {
        Map<String, String> versions = new HashMap<>(16);
        String webVersion = buildVersionStr(WEB_REGISTER_PATH);
        if (StringUtils.isNotBlank(webVersion)) {
            versions.put(WEB, webVersion);
        }
        String eeVersion = buildVersionStr(WEB_EE_REGISTER_PATH);
        if (StringUtils.isNotBlank(eeVersion)) {
            versions.put(EE, eeVersion);
        }
        String e2eVersion = buildVersionStr(WEB_E2E_REGISTER_PATH);
        if (StringUtils.isNotBlank(e2eVersion)) {
            versions.put(E2E, e2eVersion);
        }
        String amdbVersion = buildVersionStr(AMDB_REGISTER_PATH);
        if (StringUtils.isNotBlank(amdbVersion)) {
            versions.put(AMDB, amdbVersion);
        }
        String surgeVersion = buildVersionStr(SURGEREGISTER_PATH);
        if (StringUtils.isNotBlank(surgeVersion)) {
            versions.put(SURGE, surgeVersion);
        }
        return versions;
    }

    private String buildVersionStr(String path) {
        byte[] data = zkClient.getDataQuietly(path);
        if (data == null || data.length <= 0) {
            return null;
        }
        Properties properties = JSONObject.parseObject(data, Properties.class);
        return "branch：" + properties.getProperty(BRANCH) + " ( commit_id：" + properties.getProperty(COMMIT_ID) + " )";
    }

    // 更新版本信息缓存
    private void afterPublish(boolean success, VersionEntity entity) {
        ValueOperations opsForValue = redisTemplate.opsForValue();
        String version = entity.getVersion();
        VersionVo vo = new VersionVo();
        vo.setHasNew(success);
        // 不考虑数据库插入异常情况
        vo.setCurVersion(versionDAO.selectLeastOnVersionCondition(true, version));
        vo.setPreVersion(versionDAO.selectLeastOnVersionCondition(false, version));
        opsForValue.set(getCacheKey(), JsonHelper.bean2Json(vo));
    }

    private String getCacheKey() {
        return "tro:version";
    }

    private String getConfirmKey() {
        return getCacheKey() + ":confirm:" + version;
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
        List<Path> pluginPaths = getPluginPaths();
        if (CollectionUtils.isNotEmpty(pluginPaths)) {
            pluginPaths.stream().filter(path -> path.toString().contains(EE))
                .findFirst().ifPresent(path -> {
                    Properties eeProperties = new Properties();
                    versions.put(EE, eeProperties);
                    readProperties(path, eeProperties);
                });
            pluginPaths.stream().filter(path -> path.toString().contains(E2E))
                .findFirst().ifPresent(path -> {
                    Properties e2eProperties = new Properties();
                    versions.put(E2E, e2eProperties);
                    readProperties(path, e2eProperties);
                });
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
        if (org.apache.commons.lang.StringUtils.isBlank(pluginPath)) {
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