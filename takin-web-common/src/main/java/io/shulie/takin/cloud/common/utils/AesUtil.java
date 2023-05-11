package io.shulie.takin.cloud.common.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xjz@io.shulie
 * @date 2023/5/11
 * @desc ase加解密
 */
public class AesUtil {
    
    /**
     * 16位自定义密码 只能16、32、64
     */
    public static final String KEY = "1234567891011123";
    private AesUtil(){}

    /**
     * AES加密
     * @param content 明文密码
     * @return 加密密文
     */
    public static String encoder(String content){

        byte[] byteKey = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), KEY.getBytes()).getEncoded();
        SymmetricCrypto aes = SecureUtil.aes(byteKey);
        return aes.encryptBase64(content);
    }

    /**
     * 解决希音密码明文问题，将密码加密,格式：${密文密码} 
     * @param content
     * @return
     */
    public static  String sheinEncoder(String content){
        return "${" + encoder(content) + "}";
    }

    /**
     * AES解密  在解密的时候需要使用与加密时使用的同一个AES对象或者同一个KEY才能保证解密成功
     * @param content 加密密文
     * @return 解密之后的明文
     */
    public static String decoder(String content){
        byte[] byteKey = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), KEY.getBytes()).getEncoded();
        SymmetricCrypto aes = SecureUtil.aes(byteKey);
        return aes.decryptStr(content);
    }

    /**
     * 解密符合条件后的加密密码
     * @param content 文本
     * @return 解密后的文本
     */
    public static String decoderStr(String content){
        if(StringUtils.isBlank(content)){
            return content;
        }
        if(!content.contains("${") || !content.contains("}")){
            return content;
        }
        int indexOf = content.indexOf("${");
        int lastIndexOf = content.lastIndexOf("${");
        int indexOf1 = content.indexOf("}");
        int lastIndexOf1 = content.lastIndexOf("}");
        if(indexOf != lastIndexOf || indexOf1 != lastIndexOf1){
            throw new TakinWebException(TakinWebExceptionEnum.SHADOW_CONFIG_URL_CREATE_ERROR, "影子数据源或业务数据源加密规则填写错误,字符串中应当质只包含一对'${密文密码}'");

        }
        String pwd = content.substring(indexOf+2,indexOf1);
        String prefix = content.substring(0,indexOf);
        String suffix = content.substring(indexOf1+1,content.length());
        return prefix+ AesUtil.decoder(pwd)+suffix;
    }

    /**
     * 将mongodb业务数据源url中的密码进行加密
     * @param content 文本
     * @return 加密后的传
     */
    public static String encoderMongoUrl(String content){
        if(StringUtils.isBlank(content)){
            return content;
        }
        int indexOf = content.lastIndexOf(":");
        if(StringUtils.isNotBlank(content) && content.startsWith("mongodb") && content.contains("@") && indexOf != 7){
            // 截取密码
            int indexOf1 = content.indexOf("@");
            // 获取url中的密码
            String pwd = content.substring(indexOf+1,indexOf1);
            // 获取密码前面的字符串
            String prefix = content.substring(0, indexOf+1);
            // 获取密码后面的字符串
            String suffix = content.substring(indexOf1, content.length());
           return prefix + AesUtil.sheinEncoder(pwd) + suffix;
        }
        return content;
    }
}
