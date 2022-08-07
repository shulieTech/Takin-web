package io.shulie.takin.web.biz.utils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import io.shulie.takin.web.ext.entity.tenant.TenantKeyExt;
import org.apache.commons.lang3.StringUtils;

public class FileEncoder {

    // copy from 安全中心；io.shulie.takin.transform.runtime.matcher.DefaultEncoderMatcher#TAKIN
    private static final String TAKIN = "@takin";

    public static boolean fileEncoded(String filePath) {
        String fileContent = new String(IoUtil.readBytes(FileUtil.getInputStream(filePath), TAKIN.length()), StandardCharsets.UTF_8);
        return encoded(fileContent);
    }

    public static boolean encoded(String fileContent) {
        return StringUtils.startsWith(fileContent, TAKIN);
    }

    // 加密并覆盖源文件, 检测是否已加密，避免重复加密
    public static void encode(TenantKeyExt ext, String filePath) {
        if (Objects.isNull(ext) || StringUtils.isBlank(filePath)) {
            return;
        }
        String fileContent = FileUtil.readString(filePath, StandardCharsets.UTF_8);
        if (encoded(fileContent)) {
            return;
        }
        String encode = encode(ext.getPublicKey(), fileContent);
        FileUtil.writeUtf8String(doTag(ext, encode), filePath);
    }

    private static String doTag(TenantKeyExt ext, String encode) {
        String version = String.format("%03d", ext.getVersion()); // 安全中心版本号为3位
        return TAKIN.concat(version).concat(encode).concat(TAKIN).concat(version);
    }

    private static String encode(String publicKey, String encryptor) {
        try {
            return SecureUtil.rsa(null, publicKey).encryptBase64(encryptor, KeyType.PublicKey);
        } catch (Throwable t) {
            return encryptor;
        }
    }
}
