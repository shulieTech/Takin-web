package io.shulie.takin.web.biz.service.application.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import com.alibaba.fastjson.JSON;
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
        String encryptPwd = SecureUtil.aes(encoded).encryptHex(ftpContext.getPassword());
        ftpContext.setPassword(encryptPwd);
        param.setSalt(Arrays.toString(encoded));
        param.setContext(JSON.toJSONString(ftpContext));
        return param;
    }

    @Override
    public ApplicationAgentPathTypeEnum getType() {
        return ApplicationAgentPathTypeEnum.OSS;
    }

    static class FTPContext{
        private String ip;
        private String port;
        private String username;
        private String password;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
