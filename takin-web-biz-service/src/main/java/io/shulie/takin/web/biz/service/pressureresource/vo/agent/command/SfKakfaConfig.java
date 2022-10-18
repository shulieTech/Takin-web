package io.shulie.takin.web.biz.service.pressureresource.vo.agent.command;

import com.alibaba.fastjson.JSON;
import io.shulie.takin.web.biz.pojo.request.pressureresource.MqConsumerFeature;
import io.shulie.takin.web.biz.service.pressureresource.common.MqTypeEnum;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateMqConsumerEntity;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * @author guann1n9
 * @date 2022/10/17 5:16 PM
 */
public class SfKakfaConfig implements Serializable {

    private String key;

    private String topic;

    private String topicTokens;

    private String group;

    private String systemIdToken;

    private String clusterName;

    private String monitorUrl;

    private Integer poolSize;

    private Integer messageConsumeThreadCount;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopicTokens() {
        return topicTokens;
    }

    public void setTopicTokens(String topicTokens) {
        this.topicTokens = topicTokens;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSystemIdToken() {
        return systemIdToken;
    }

    public void setSystemIdToken(String systemIdToken) {
        this.systemIdToken = systemIdToken;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getMonitorUrl() {
        return monitorUrl;
    }

    public void setMonitorUrl(String monitorUrl) {
        this.monitorUrl = monitorUrl;
    }

    public Integer getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
    }

    public Integer getMessageConsumeThreadCount() {
        return messageConsumeThreadCount;
    }

    public void setMessageConsumeThreadCount(Integer messageConsumeThreadCount) {
        this.messageConsumeThreadCount = messageConsumeThreadCount;
    }


    /**
     * 对象转换
     * @param mqConsumerEntity
     * @return
     */
    public static SfKakfaConfig mapping(PressureResourceRelateMqConsumerEntity mqConsumerEntity){
        if(!mqConsumerEntity.getMqType().equals(MqTypeEnum.SF_KAKFA.getCode())){
            //非顺丰kafka配置，跳过
            return null;
        }
        SfKakfaConfig sfKakfaConfig = new SfKakfaConfig();
        sfKakfaConfig.setKey(mqConsumerEntity.getTopic());
        sfKakfaConfig.setTopic(mqConsumerEntity.getTopic());
        sfKakfaConfig.setTopicTokens(mqConsumerEntity.getTopicTokens());
        sfKakfaConfig.setGroup(mqConsumerEntity.getGroup());
        sfKakfaConfig.setSystemIdToken(mqConsumerEntity.getSystemIdToken());
        if(!StringUtils.hasText(mqConsumerEntity.getFeature())){
            return sfKakfaConfig;
        }
        MqConsumerFeature mqConsumerFeature = JSON.parseObject(mqConsumerEntity.getFeature(), MqConsumerFeature.class);
        sfKakfaConfig.setClusterName(mqConsumerFeature.getClusterName());
        sfKakfaConfig.setMonitorUrl(mqConsumerFeature.getClusterAddr());
        sfKakfaConfig.setPoolSize(mqConsumerFeature.getProviderThreadCount());
        sfKakfaConfig.setMessageConsumeThreadCount(mqConsumerFeature.getConsumerThreadCount());
        return sfKakfaConfig;
    }
}
