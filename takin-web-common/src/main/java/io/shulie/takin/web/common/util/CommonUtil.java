package io.shulie.takin.web.common.util;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.pamirs.takin.common.constant.Constants;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.common.common.Separator;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * 通用工具类
 *
 * @author liuchuan
 * @date 2021/6/3 4:43 下午
 */
public class CommonUtil implements AppConstants {

    /**
     * 获得 zk 租户, 环境隔离后的路径
     *
     * @param path 节点路径, 前缀可以带 /, 也可以不带
     * @return 租户, 环境隔离后的路径, /租户key/环境/xxx
     */
    public static String getZkTenantAndEnvPath(String path) {
        return getZkPathPassTenantAppKeyAndEnvCode(WebPluginUtils.traceTenantAppKey(), WebPluginUtils.traceEnvCode(), path);
    }

    /**
     * 传递 租户 key, 环境, zk 节点路径 来获得 zk 节点完整路径
     *
     * @param tenantAppKey 租户 key
     * @param envCode      环境
     * @param path         节点路径, 前缀可以带 /, 也可以不带
     * @return zk 节点完整路径
     */
    public static String getZkPathPassTenantAppKeyAndEnvCode(String tenantAppKey, String envCode, String path) {
        if (StrUtil.isBlank(path)) {
            throw new IllegalArgumentException("zookeeper 的节点路径必须填写!");
        }
        String tenantAndEnv = String.format("/%s/%s", tenantAppKey, envCode);
        // 传来的 path 开头是否带有 /
        if (path.startsWith(Separator.Separator1.getValue())) {
            return tenantAndEnv + path;
        }

        return String.format("%s/%s", tenantAndEnv, path);
    }

    /**
     * 应用配置标识
     */
    public static final String APP = "apps";

    /**
     * 获得 zk 租户, 环境隔离后的路径
     *
     * @param path 节点路径
     * @param commonExt 租户属性
     * @return 租户, 环境隔离后的路径
     */
    public static String getZkTenantAndEnvPath(String path, TenantCommonExt commonExt) {
        return generateRedisKeyWithSeparator(Separator.Separator1, commonExt.getTenantAppKey(), commonExt.getEnvCode(), path);
    }

    /**
     * 获取NameSpace
     * @param commonExt
     * @return
     */
    public static String getZkNameSpace(TenantCommonExt commonExt) {
        return commonExt != null ? CommonUtil.getZkTenantAndEnvPath(APP,commonExt): Constants.DEFAULT_NAMESPACE;
    }

    /**
     * springboot local 环境
     *
     * @return 是否
     */
    public static boolean isLocalEnv() {
        return !StringUtils.isEmpty(SpringUtil.getActiveProfile())
            && SpringUtil.getActiveProfile().startsWith(ACTIVE_PROFILE_LOCAL);
    }

    /**
     * springboot dev 环境
     *
     * @return 是否
     */
    public static boolean isDevEnv() {
        return !StringUtils.isEmpty(SpringUtil.getActiveProfile())
            && SpringUtil.getActiveProfile().startsWith(ACTIVE_PROFILE_DEV);
    }

    /**
     * springboot test 环境
     *
     * @return 是否
     */
    public static boolean isTestEnv() {
        return !StringUtils.isEmpty(SpringUtil.getActiveProfile())
            && SpringUtil.getActiveProfile().startsWith(ACTIVE_PROFILE_TEST);
    }

    /**
     * 换行符
     *
     * @return 换行符
     */
    public static String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    /**
     * 获得上传的绝对路径
     * xxx/project/public/probe/20210604/xxx
     * 根据租户id区分文件夹
     *
     * @param uploadPath 上传的文件夹
     * @param folder     分类目录
     * @return 文件所在文件夹的绝对路径
     */
    public static String getAbsoluteUploadPath(String uploadPath, String folder) {
        return String.format("%s%s%s%s%s%s", CommonUtil.getProjectAbsolutePath(), uploadPath,
            folder, File.separator, LocalDate.now().toString().replaceAll(ACROSS, BLANK_STRING),
            File.separator);
    }

    /**
     * 获得文件的保存路径
     * /data/path/probe/20210610/
     *
     * @param uploadPath 上传的文件路径
     * @param folder     分类目录
     * @return 上传文件的临时路径
     */
    public static String getUploadPath(String uploadPath, String folder) {
        return String.format("%s%s%s%s%s%s", uploadPath, File.separator,
            folder, File.separator, LocalDate.now().toString().replaceAll(ACROSS, BLANK_STRING),
            File.separator);
    }

    /**
     * 获得内网ip
     *
     * @return 内网ip
     */
    public static String getInetIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "";
        }
    }

    /**
     * 项目绝对路径
     *
     * @return 项目绝对路径
     */
    public static String getProjectAbsolutePath() {
        return System.getProperty("user.dir");
    }

    /**
     * redis key
     * @param keyPart
     * @return
     */
    public static String generateRedisKey(String... keyPart) {
        return generateRedisKeyWithSeparator(Separator.defautSeparator(), keyPart);
    }

    public static String generateRedisKeyWithSeparator(Separator separator, String... keyPart) {
        if (separator == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "separator cannot be null!");
        }
        return StringUtil.join(separator.getValue(), keyPart);
    }

    /* ---------------- 测试 -------------- */

    public static void main(String[] args) {
        TenantCommonExt ext = new TenantCommonExt();
        ext.setTenantAppKey("sasa");
        ext.setEnvCode("asaa");
        System.out.println(getZkTenantAndEnvPath("apps",ext));;

        // 生成一万个A, 转B
        long startAt = System.currentTimeMillis();

        List<A> aList = new ArrayList<>(10000);
        for (long i = 0; i < 1000000; i++) {
            aList.add(new A(i, RandomUtil.randomString(5)));
        }

        long middleAt = System.currentTimeMillis() - startAt;
        System.out.printf("生成数据花费时间: %d%n", middleAt);

        DataTransformUtil.list2list(aList, B.class);
        //list2listByStream(aList, B.class);
        System.out.printf("转换数据花费时间: %d%n", System.currentTimeMillis() - middleAt);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class A {
        private Long id;
        private String name;
    }

    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    private static class B {
        private Long id;
        private String name;
    }

}
