package io.shulie.takin.web.entrypoint.controller.agent;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSONObject;
import com.pamirs.takin.entity.domain.dto.NodeUploadDataDTO;
import com.pamirs.takin.entity.domain.query.ShadowJobConfigQuery;
import com.pamirs.takin.entity.domain.vo.TUploadInterfaceVo;
import io.shulie.takin.sdk.kafka.MessageReceiveCallBack;
import io.shulie.takin.sdk.kafka.MessageReceiveService;
import io.shulie.takin.sdk.kafka.entity.MessageEntity;
import io.shulie.takin.sdk.kafka.impl.KafkaSendServiceFactory;
import io.shulie.takin.web.biz.pojo.perfomanceanaly.PerformanceBaseDataReq;
import io.shulie.takin.web.biz.pojo.request.agent.PushMiddlewareRequest;
import io.shulie.takin.web.biz.service.pressureresource.vo.agent.command.TakinAck;
import io.shulie.takin.web.data.param.application.ConfigReportInputParam;
import io.shulie.takin.web.entrypoint.controller.perfomanceanaly.PerformanceBaseDataController;
import io.shulie.takin.web.entrypoint.controller.pressureresource.PressureResourceAckController;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class KafkaReceiveService implements InitializingBean {
    @Resource
    private AgentPushController agentPushController;
    @Resource
    private PerformanceBaseDataController performanceBaseDataController;
    @Resource(name = "kafkaReceivePool")
    private ExecutorService kafkaReceivePool;
    @Resource
    private AgentApplicationController agentApplicationController;
    @Resource
    private PressureResourceAckController pressureResourceAckController;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("开始kafka消息监听");
        MessageReceiveService messageReceiveService = new KafkaSendServiceFactory().getKafkaMessageReceiveInstance();
        kafkaReceivePool.execute(() -> {
            messageReceiveService.receive(ListUtil.of("stress-test-agent-performance-basedata"), new MessageReceiveCallBack() {
                @Override
                public void success(MessageEntity messageEntity) {
                    String string = JSONObject.toJSONString(messageEntity.getBody());
                    if (!validateAgentAuth(messageEntity.getHeaders())) {
                        log.error("agent权限鉴定失败，消费失败");
                    } else {
                        PerformanceBaseDataReq req = JSONObject.parseObject(string, PerformanceBaseDataReq.class);
                        performanceBaseDataController.receivePerformanceBaseData(req);
                    }
                }

                @Override
                public void fail(String errorMessage) {
                    log.error("性能分析接口，接收kafka消息失败:{}", errorMessage);
                }
            });
        });

        kafkaReceivePool.execute(() -> {
            messageReceiveService.receive(ListUtil.of("stress-test-confcenter-interface-add-interfaceData"), new MessageReceiveCallBack() {
                @Override
                public void success(MessageEntity messageEntity) {
                    String string = JSONObject.toJSONString(messageEntity.getBody());
                    if (!validateAgentAuth(messageEntity.getHeaders())) {
                        log.error("agent权限鉴定失败，消费失败");
                    } else {
                        TUploadInterfaceVo tUploadInterfaceVo = JSONObject.parseObject(string, TUploadInterfaceVo.class);
                        agentPushController.judgeNeedUpload(tUploadInterfaceVo);
                    }
                }

                @Override
                public void fail(String errorMessage) {
                    log.error("应用上报接口，接收kafka消息失败:{}", errorMessage);
                }
            });
        });

        kafkaReceivePool.execute(() -> {
            messageReceiveService.receive(ListUtil.of("stress-test-agent-api-register"), new MessageReceiveCallBack() {
                @Override
                public void success(MessageEntity messageEntity) {
                    if (!validateAgentAuth(messageEntity.getHeaders())) {
                        log.error("agent权限鉴定失败，消费失败");
                    } else {
                        Map body = messageEntity.getBody();
                        agentPushController.registerApi(body);
                    }
                }

                @Override
                public void fail(String errorMessage) {
                    log.error("入口规则接口，接收kafka消息失败:{}", errorMessage);
                }
            });
        });

        kafkaReceivePool.execute(() -> {
            messageReceiveService.receive(ListUtil.of("stress-test-application-agent-access-status"), new MessageReceiveCallBack() {
                @Override
                public void success(MessageEntity messageEntity) {
                    String body = JSONObject.toJSONString(messageEntity.getBody());
                    if (!validateAgentAuth(messageEntity.getHeaders())) {
                        log.error("agent权限鉴定失败，消费失败");
                    } else {
                        NodeUploadDataDTO nodeUploadDataDTO = JSONObject.parseObject(body, NodeUploadDataDTO.class);
                        agentPushController.uploadAccessStatus(nodeUploadDataDTO);
                    }
                }

                @Override
                public void fail(String errorMessage) {
                    log.error("定时上报接口状态接口，接收kafka消息失败:{}", errorMessage);
                }
            });
        });

        kafkaReceivePool.execute(() -> {
            messageReceiveService.receive(ListUtil.of("stress-test-shadow-job-update"), new MessageReceiveCallBack() {
                @Override
                public void success(MessageEntity messageEntity) {
                    String body = JSONObject.toJSONString(messageEntity.getBody());
                    if (!validateAgentAuth(messageEntity.getHeaders())) {
                        log.error("agent权限鉴定失败，消费失败");
                    } else {
                        ShadowJobConfigQuery shadowJobConfigQuery = JSONObject.parseObject(body, ShadowJobConfigQuery.class);
                        agentPushController.update(shadowJobConfigQuery);
                    }
                }

                @Override
                public void fail(String errorMessage) {
                    log.error("上报影子job接口，接收kafka消息失败:{}", errorMessage);
                }
            });
        });

        kafkaReceivePool.execute(() -> {
            messageReceiveService.receive(ListUtil.of("stress-test-agent-push-application-config"), new MessageReceiveCallBack() {
                @Override
                public void success(MessageEntity messageEntity) {
                    String body = JSONObject.toJSONString(messageEntity.getBody());
                    if (!validateAgentAuth(messageEntity.getHeaders())) {
                        log.error("agent权限鉴定失败，消费失败");
                    } else {
                        ConfigReportInputParam configReportInputParam = JSONObject.parseObject(body, ConfigReportInputParam.class);
                        agentPushController.uploadConfigInfo(configReportInputParam);
                    }
                }

                @Override
                public void fail(String errorMessage) {
                    log.error("配置信息上报接口，接收kafka消息失败:{}", errorMessage);
                }
            });
        });

        kafkaReceivePool.execute(() -> {
            messageReceiveService.receive(ListUtil.of("stress-test-agent-push-application-middleware"), new MessageReceiveCallBack() {
                @Override
                public void success(MessageEntity messageEntity) {
                    String body = JSONObject.toJSONString(messageEntity.getBody());
                    if (!validateAgentAuth(messageEntity.getHeaders())) {
                        log.error("agent权限鉴定失败，消费失败");
                    } else {
                        PushMiddlewareRequest pushMiddlewareRequest = JSONObject.parseObject(body, PushMiddlewareRequest.class);
                        agentApplicationController.pushMiddlewareList(pushMiddlewareRequest);
                    }
                }

                @Override
                public void fail(String errorMessage) {
                    log.error("中间件信息上报接口，接收kafka消息失败:{}", errorMessage);
                }
            });
        });

        kafkaReceivePool.execute(() -> {
            messageReceiveService.receive(ListUtil.of("stress-test-confcenter-applicationmnt-update-applicationagent"), new MessageReceiveCallBack() {
                @Override
                public void success(MessageEntity messageEntity) {
                    if (!validateAgentAuth(messageEntity.getHeaders())) {
                        log.error("agent权限鉴定失败，消费失败");
                    } else {
                        Map body = messageEntity.getBody();
                        agentPushController.appAgentVersionUpdate(body.get("appName") == null ? null : body.get("appName").toString(),
                                body.get("agentVersion") == null ? null : body.get("agentVersion").toString(),
                                body.get("pradarVersion") == null ? null : body.get("pradarVersion").toString());
                    }
                }

                @Override
                public void fail(String errorMessage) {
                    log.error("agent版本信息上报接口，接收kafka消息失败:{}", errorMessage);
                }
            });
        });

        kafkaReceivePool.execute(() -> {
            messageReceiveService.receive(ListUtil.of("stress-test-pressureResource-agent"), new MessageReceiveCallBack() {
                @Override
                public void success(MessageEntity messageEntity) {
                    String jsonString = JSONObject.toJSONString(messageEntity.getBody());
                    TakinAck takinAck = JSONObject.parseObject(jsonString, TakinAck.class);
                    pressureResourceAckController.commandAck(takinAck);
                }

                @Override
                public void fail(String errorMessage) {
                    log.error("agent版本信息上报接口，接收kafka消息失败:{}", errorMessage);
                }
            });
        });
    }

    private boolean validateAgentAuth(Map<String, Object> headers) {
        Object userAppKey = headers.get("userAppKey");
        Object tenantAppKey = headers.get("tenantAppKey");
        Object userId = headers.get("userId");
        Object envCode = headers.get("envCode");
        Object agentExpand = headers.get("agentExpand");
        return WebPluginUtils.validateAgentAuth(userAppKey == null ? null : userAppKey.toString(),
                tenantAppKey == null ? null : tenantAppKey.toString(),
                userId == null ? null : userId.toString(),
                envCode == null ? null : envCode.toString(),
                agentExpand == null ? null : agentExpand.toString());
    }
}
