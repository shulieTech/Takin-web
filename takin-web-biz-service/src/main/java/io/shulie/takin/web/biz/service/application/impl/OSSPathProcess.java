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
public class OSSPathProcess implements IPluginDownLoadPathProcess {

    @Override
    public <T extends ApplicationPluginDownloadPathEntity> T encrypt(T param) {
        OSSContext ossContext = Convert.convert(OSSContext.class, param.getContext());
        byte[] encoded  =  SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        String encryptAccessKeyId = SecureUtil.aes(encoded).encryptHex(ossContext.getAccessKeyId());
        String encryptAccessKeySecret = SecureUtil.aes(encoded).encryptHex(ossContext.getAccessKeySecret());
        ossContext.setAccessKeyId(encryptAccessKeyId);
        ossContext.setAccessKeySecret(encryptAccessKeySecret);
        param.setSalt(Arrays.toString(encoded));
        param.setContext(JSON.toJSONString(ossContext));
        return param;
    }

    @Override
    public ApplicationAgentPathTypeEnum getType() {
        return ApplicationAgentPathTypeEnum.OSS;
    }

    static class OSSContext{
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }
    }
}
