package io.shulie.takin.web.biz.service.application.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.shulie.takin.web.biz.service.application.IPluginDownLoadPathProcess;
import io.shulie.takin.web.common.enums.application.ApplicationAgentPathTypeEnum;
import io.shulie.takin.web.data.model.mysql.ApplicationPluginDownloadPathEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @Author: 南风
 * @Date: 2021/11/19 5:18 下午
 */
@Service
public class FTPPathProcess implements IPluginDownLoadPathProcess {

    @Override
    public <T extends ApplicationPluginDownloadPathEntity> T encrypt(T param) {

        FTPContext ftpContext = Convert.convert(FTPContext.class, param.getContext());
        byte[] encoded  =  SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        String encryptPwd = SecureUtil.aes(encoded).encryptHex(ftpContext.getPasswd());
        ftpContext.setPasswd(encryptPwd);
        param.setSalt(Arrays.toString(encoded));
        param.setContext(JSON.toJSONString(ftpContext));
        return param;
    }

    @Override
    public ApplicationAgentPathTypeEnum getType() {
        return ApplicationAgentPathTypeEnum.OSS;
    }

    static class FTPContext{
        private String ftpHost;
        private Integer ftpPort;
        private String username;
        private String passwd;
        private String basePath;

        public String getFtpHost() {
            return ftpHost;
        }

        public void setFtpHost(String ftpHost) {
            this.ftpHost = ftpHost;
        }

        public Integer getFtpPort() {
            return ftpPort;
        }

        public void setFtpPort(Integer ftpPort) {
            this.ftpPort = ftpPort;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPasswd() {
            return passwd;
        }

        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }

        public String getBasePath() {
            return basePath;
        }

        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }
    }

    public static void main(String[] args) {
        String context = "{\"ftpHost\":\"192.168.1.56\",\"ftpPort\":21,\"passwd\":\"test@shulie2021\",\"username\":\"root\"}";
        FTPContext ftpContext = JSONObject.parseObject(context, FTPContext.class);
//        FTPContext ftpContext = Convert.convert(FTPContext.class,context);
        byte[] encoded  =  SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        String encryptPwd = SecureUtil.aes(encoded).encryptHex(ftpContext.getPasswd());
        ftpContext.setPasswd(encryptPwd);
        System.out.println(JSON.toJSONString(ftpContext));
        System.out.println(encoded.toString());
        //解密
        String s = SecureUtil.aes(encoded).decryptStr(encryptPwd);
        System.out.println(s);
    }
}
