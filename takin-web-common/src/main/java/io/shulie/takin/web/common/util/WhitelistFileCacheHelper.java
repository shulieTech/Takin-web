package io.shulie.takin.web.common.util;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * 白名单文件内容缓存类
 *
 * @author liuchuan
 * @date 2021/10/26 3:35 下午
 */
public class WhitelistFileCacheHelper {

    /**
     * 缓存
     */
    public static final Cache<String, String> CACHE= CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .maximumSize(5000)
        .build();

}
