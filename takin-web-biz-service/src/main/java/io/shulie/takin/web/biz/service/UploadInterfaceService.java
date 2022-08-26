package io.shulie.takin.web.biz.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;
import com.pamirs.takin.common.constant.Constants;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.common.redis.RedisKey;
import com.pamirs.takin.common.redis.UploadInterfaceKeyMaker;
import com.pamirs.takin.entity.dao.confcenter.TUploadInterfaceDataDao;
import com.pamirs.takin.entity.domain.entity.TUploadInterfaceData;
import com.pamirs.takin.entity.domain.vo.TUploadInterfaceDetailVo;
import com.pamirs.takin.entity.domain.vo.TUploadInterfaceVo;
import com.pamirs.takin.entity.domain.vo.TUploadNeedVo;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeZkDTO;
import io.shulie.takin.web.biz.common.CommonService;
import io.shulie.takin.web.biz.service.agentupgradeonline.AgentReportService;
import io.shulie.takin.web.biz.utils.AgentZkClientUtil;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentReportStatusEnum;
import io.shulie.takin.web.data.param.agentupgradeonline.CreateAgentReportParam;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author 298403
 * @description 上传接口信息接口
 * @create 2019/01/15
 */
@Slf4j
@Service
public class UploadInterfaceService extends CommonService implements InitializingBean {

    @Autowired
    private TUploadInterfaceDataDao uploadInterfaceDataDao;
    @Resource
    private AgentZkClientUtil agentZkClientUtil;
    @Resource
    private AgentReportService agentReportService;
    @Resource
    private ApplicationService applicationService;

    private String agentRegisteredPath = null;

    private ConcurrentHashMap<String, Long> agentDealTimeMap = new ConcurrentHashMap<>();

    /**
     * 判断是否需要上传接口信息
     *
     * @param uploadNeedVo
     * @return
     */
    public boolean executeNeedUploadInterface(TUploadNeedVo uploadNeedVo) throws TakinModuleException {
        if (uploadNeedVo == null || StringUtils.isEmpty(uploadNeedVo.getAppName())) {
            return false;
        }
        this.agentInfoUpdate(uploadNeedVo.getAppName());
        String key = UploadInterfaceKeyMaker.getUploadKey(uploadNeedVo.getAppName());
        Optional<Object> value = redisManager.valueGet(key);
        if (value.isPresent()) {
            if (Integer.parseInt(value.get().toString()) == uploadNeedVo.getSize().intValue()) {
                return false;
            } else {
                redisManager.removeKey(key);
                return true;
            }
        }
        return true;
    }

    private void agentInfoUpdate(String appName) {
        long timeMillis = System.currentTimeMillis();
        if (agentDealTimeMap.containsKey(appName)) {
            //因为这个接口目前5s调用一次，心跳数据一分钟更新一次就好，这里做一次限制，避免服务压力太大
            if (timeMillis - agentDealTimeMap.get(appName) < 1000 * 40){
                return;
            }
        } else {
            agentDealTimeMap.put(appName, timeMillis);
        }

        log.info("zk监听的节点为:" + agentRegisteredPath);
        if (agentRegisteredPath == null) {
            log.warn("zk节点信息不存在，本次不会做节点监听，如果需要兼容agent1.0版本请在数据库添加zk节点");
            return;
        }
        Long appId = applicationService.queryApplicationIdByAppName(appName);
        if (appId == null) {
            log.warn("没有根据当前应用名查询到对应的id，请确认是否数据出现异常，应用名:" + appName);
            return;
        }
        try {
            String registeredPath = agentRegisteredPath;
            CuratorFramework client = this.agentZkClientUtil.getClient();
            if (!registeredPath.endsWith("/")) {
                registeredPath = registeredPath + "/";
            }
            registeredPath = registeredPath + appName;
            List<String> agentIds = client.getChildren().forPath(registeredPath);
            if (CollectionUtils.isEmpty(agentIds)) {
                return;
            }
            for (String agentId : agentIds) {
                byte[] bytes = client.getData().forPath(registeredPath + "/" + agentId);
                String s = new String(bytes);
                JSONObject jsonObject = JSONObject.parseObject(s);
                if (jsonObject.get("simulatorVersion") != null) {
                    log.info("agent2.0的数据，不予处理，agent版本为:" + jsonObject.get("simulatorVersion"));
                    return;
                }

                ApplicationNodeZkDTO applicationNodeDTO = JsonHelper.json2Bean(s, ApplicationNodeZkDTO.class);
                //兼容agent1.0接口，所以这里更新应用状态
                CreateAgentReportParam createAgentReportParam = new CreateAgentReportParam();
                createAgentReportParam.setIpAddress(applicationNodeDTO.getAddress());
                createAgentReportParam.setProgressId(applicationNodeDTO.getPid());
                createAgentReportParam.setAgentVersion(applicationNodeDTO.getAgentVersion());
                createAgentReportParam.setApplicationId(appId);
                createAgentReportParam.setApplicationName(appName);
                createAgentReportParam.setAgentId(applicationNodeDTO.getAgentId());
                createAgentReportParam.setStatus(applicationNodeDTO.isStatus() ? AgentReportStatusEnum.RUNNING.getVal() : AgentReportStatusEnum.ERROR.getVal());
                createAgentReportParam.setAgentErrorInfo(applicationNodeDTO.getErrorMsg());
                agentReportService.insertOrUpdate(createAgentReportParam);
            }
        } catch (Exception e) {
            log.error("处理agent1.0 zk数据出现异常", e);
        }

    }

    /**
     * 保存上传的接口信息
     *
     * @param uploadInterfaceVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int saveUploadInterfaceData(TUploadInterfaceVo uploadInterfaceVo) throws TakinModuleException {
        if (uploadInterfaceVo == null || StringUtils.isEmpty(uploadInterfaceVo.getAppName()) || CollectionUtils.isEmpty(
                uploadInterfaceVo.getAppDetails())) {
            return 0;
        }
        String key = UploadInterfaceKeyMaker.getUploadKey(uploadInterfaceVo.getAppName());
        //超时时间
        RedisKey redisKey = new RedisKey(key, 60 * 30);
        if (!redisManager.acquireLock(redisKey)) {
            return 0;
        }
        //保存前先删除数据
        uploadInterfaceDataDao.deleteByAppName(uploadInterfaceVo.getAppName());
        List<TUploadInterfaceData> saveDataList = new ArrayList<>();
        for (TUploadInterfaceDetailVo appDetail : uploadInterfaceVo.getAppDetails()) {
            TUploadInterfaceData tempDate = new TUploadInterfaceData();
            tempDate.setAppName(uploadInterfaceVo.getAppName());
            tempDate.setId(snowflake.next());
            tempDate.setInterfaceValue(appDetail.getInterfaceName());
            if (Constants.UPLOAD_DATA_TYPE_DUBBO.equalsIgnoreCase(appDetail.getType())) {
                tempDate.setInterfaceType(Constants.UPLOAD_DATA_DBTYPE_DUBBO);
            } else if (Constants.UPLOAD_DATA_TYPE_JOB.equalsIgnoreCase(appDetail.getType())) {
                tempDate.setInterfaceType(Constants.UPLOAD_DATA_DBTYPE_JOB);
            } else {
                continue;
            }
            saveDataList.add(tempDate);
            if (saveDataList.size() > 500) {
                uploadInterfaceDataDao.insertList(saveDataList);
                saveDataList = new ArrayList<>();
            }
        }
        if (CollectionUtils.isNotEmpty(saveDataList)) {
            uploadInterfaceDataDao.insertList(saveDataList);
        }
        //最后放入缓存
        redisManager.valuePut(redisKey, System.currentTimeMillis() + 60 * 3000);
        return uploadInterfaceVo.getAppDetails().size();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        agentRegisteredPath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.AGENT_REGISTERED_PATH);
    }
}
