package io.shulie.takin.web.biz.listener;

import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeZkDTO;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.agentupgradeonline.AgentReportService;
import io.shulie.takin.web.biz.utils.AgentZkClientUtil;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentReportStatusEnum;
import io.shulie.takin.web.data.param.agentupgradeonline.CreateAgentReportParam;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 监听zk节点变化，目前用来兼容agent1.0的应用状态变化
 */
@Slf4j
@Component
public class ZkNodeListener implements InitializingBean {

    @Resource
    private AgentZkClientUtil client;
    @Resource
    private AgentReportService agentReportService;
    @Resource
    private ApplicationService applicationService;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            CuratorFramework client = this.client.getClient();
            String agentRegisteredPath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.AGENT_REGISTERED_PATH);
            log.info("zk监听的节点为:" + agentRegisteredPath);
            if (agentRegisteredPath == null) {
                log.warn("zk节点信息不存在，本次不会做节点监听，如果需要兼容agent1.0版本请在数据库添加zk节点");
                return;
            }
            if (agentRegisteredPath.endsWith("/")) {
                agentRegisteredPath = agentRegisteredPath.substring(0, agentRegisteredPath.length() - 1);
            }
            TreeCache treeCache = new TreeCache(client, agentRegisteredPath);
            String finalAgentRegisteredPath = agentRegisteredPath;
            TreeCacheListener treeCacheListener = new TreeCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                    log.info(treeCacheEvent.getType() + "==>" + treeCacheEvent.getData().getPath());
                    List<String> applicationNames = client.getChildren().forPath(finalAgentRegisteredPath);
                    log.info("获取到当前所有的子节点为:" + applicationNames);
                    if (CollectionUtils.isNotEmpty(applicationNames)) {
                        for (String application : applicationNames) {
                            List<String> agentIds = client.getChildren().forPath(finalAgentRegisteredPath + "/" + application);
                            if (CollectionUtils.isEmpty(agentIds)) {
                                continue;
                            }
                            Long appId = applicationService.queryApplicationIdByAppName(application);
                            if (appId == null) {
                                continue;
                            }
                            for (String agentId : agentIds) {
                                byte[] bytes = client.getData().forPath(finalAgentRegisteredPath + "/" + application + "/" + agentId);
                                String s = new String(bytes);
                                ApplicationNodeZkDTO applicationNodeDTO = JsonHelper.json2Bean(s, ApplicationNodeZkDTO.class);
                                //兼容agent1.0接口，所以这里更新应用状态
                                CreateAgentReportParam createAgentReportParam = new CreateAgentReportParam();
                                createAgentReportParam.setIpAddress(applicationNodeDTO.getAddress());
                                createAgentReportParam.setProgressId(applicationNodeDTO.getPid());
                                createAgentReportParam.setAgentVersion(applicationNodeDTO.getAgentVersion());
                                createAgentReportParam.setApplicationId(appId);
                                createAgentReportParam.setApplicationName(application);
                                createAgentReportParam.setAgentId(applicationNodeDTO.getAgentId());
                                createAgentReportParam.setStatus(applicationNodeDTO.isStatus() ? AgentReportStatusEnum.RUNNING.getVal() : AgentReportStatusEnum.ERROR.getVal());
                                createAgentReportParam.setAgentErrorInfo(applicationNodeDTO.getErrorMsg());
                                agentReportService.insertOrUpdate(createAgentReportParam);
                            }
                        }

                    }

                }
            };
            treeCache.getListenable().addListener(treeCacheListener);
            treeCache.start();
        } catch (Exception e) {
            log.error("监听zk节点出现异常", e);
        }
    }
}
