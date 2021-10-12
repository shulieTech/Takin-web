package io.shulie.takin.web.common.util;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
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
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
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
     * @param path 节点路径
     * @param commonExt 租户属性
     * @return 租户, 环境隔离后的路径
     */
    public static String getZkTenantAndEnvPath(String path, TenantCommonExt commonExt) {
        return generateRedisKeyWithSeparator(Separator.Separator1, commonExt.getTenantAppKey(), commonExt.getEnvCode(), path);
    }


    /**
     * 获得 zk 租户, 环境隔离后的路径
     *
     * @param path 节点路径
     * @return 租户, 环境隔离后的路径
     */
    public static String getZkTenantAndEnvPath(String path) {
        return generateRedisKeyWithSeparator(Separator.Separator1, WebPluginUtils.fillTenantCommonExt(),
            WebPluginUtils.traceEnvCode(), path);
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
     * 一个象的list 转 另一个对象的list
     * 适合两者的字段名称及类型, 都一样
     * 使用 json 方式
     *
     * 要转换的类, 需要有无参构造器
     *
     * 100000 数据, 花费时间 1622712527764
     * 数据越多, 此方法愈快一些,
     * stream 方法加了 try/catch 是一部分原因, newInstance 可能也是一部分原因
     *
     * @param sourceList  源list
     * @param targetClazz 目标对象类对象
     * @param <T>         要转换的类
     * @return 另一个对象的list
     */
    public static <T> List<T> list2list(List<?> sourceList, Class<T> targetClazz) {
        return CollectionUtils.isEmpty(sourceList) ? Collections.emptyList()
            : JsonUtil.json2List(JsonUtil.bean2Json(sourceList), targetClazz);
    }

    /**
     * 一个象的list 转 另一个对象的list
     * 适合两者的字段名称及类型, 都一样
     *
     * 要转换的类, 需要有无参构造器
     *
     * 使用 stream 方式
     *
     * 100000 数据, 花费时间 1622712560395
     *
     * @param sourceList  源list
     * @param targetClazz 目标对象类对象
     * @param <T>         要转换的类
     * @return 另一个对象的list
     */
    public static <T> List<T> list2listByStream(List<?> sourceList, Class<T> targetClazz) {
        return sourceList.stream().map(source -> {
            try {
                T target = targetClazz.newInstance();
                BeanUtils.copyProperties(source, target);
                return target;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("目标对象实例化错误!");
            }
        }).collect(Collectors.toList());
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

        list2list(aList, B.class);
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
