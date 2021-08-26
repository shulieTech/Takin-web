package io.shulie.takin.web.data.dao.application;

import java.util.List;
import java.util.stream.Collectors;

import com.esotericsoftware.minlog.Log;
import com.google.common.collect.Lists;
import com.pamirs.takin.common.util.http.DateUtil;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationNodeQueryDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeDTO;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.param.application.ApplicationNodeQueryParam;
import io.shulie.takin.web.data.param.application.QueryApplicationNodeParam;
import io.shulie.takin.web.data.result.application.ApplicationNodeListResult;
import io.shulie.takin.web.data.result.application.ApplicationNodeResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mubai
 * @date 2020-09-23 19:15
 */

@Service
public class ApplicationNodeDAOImpl implements ApplicationNodeDAO {

    private int LOOP_NUM = 20;
    @Autowired
    private ApplicationClient applicationClient;

    @Override
    public PagingList<ApplicationNodeResult> pageNodes(ApplicationNodeQueryParam param) {
        // 分批从amdb获取应用节点数据
        List<String> appNames = param.getApplicationNames();
        List<ApplicationNodeResult> applicationNodeResultList;
        List<ApplicationNodeDTO> applicationNodeTotalList = Lists.newArrayList();

        int batchSize = 100;
        long totalCount = 0;
        for (int from = 0, to, size = appNames.size(); from < size; from = to) {
            to = Math.min(from + batchSize, size);
            List<String> pageAppNameList = appNames.subList(from, to);

            ApplicationNodeQueryDTO applicationQueryDTO = new ApplicationNodeQueryDTO();
            applicationQueryDTO.setAppNames(String.join(",", pageAppNameList));
            applicationQueryDTO.setCurrentPage(param.getCurrent());
            applicationQueryDTO.setPageSize(param.getPageSize());
            applicationQueryDTO.setAgentId(param.getAgentId());
            applicationQueryDTO.setProbeStatus(param.getProbeStatus());
            if (StringUtils.isNotBlank(param.getIp())) {
                applicationQueryDTO.setIpAddress(param.getIp());
            }

            PagingList<ApplicationNodeDTO> applicationNodePage = applicationClient.pageApplicationNodes(applicationQueryDTO);
            if (!applicationNodePage.isEmpty()) {
                totalCount = totalCount + applicationNodePage.getTotal();
                applicationNodeTotalList.addAll(applicationNodePage.getList());
            }
        }

        if (applicationNodeTotalList.isEmpty()) {
            return PagingList.empty();
        }

        applicationNodeResultList = applicationNodeTotalList.stream().map(applicationNodeDTO -> {
            ApplicationNodeResult applicationInstanceResult = new ApplicationNodeResult();
            applicationInstanceResult.setAppId(String.valueOf(applicationNodeDTO.getAppId()));
            applicationInstanceResult.setAppName(applicationNodeDTO.getAppName());
            applicationInstanceResult.setAgentId(String.valueOf(applicationNodeDTO.getAgentId()));
            applicationInstanceResult.setAgentLanguage(applicationNodeDTO.getAgentLanguage());
            applicationInstanceResult.setAgentVersion(applicationNodeDTO.getAgentVersion());
            applicationInstanceResult.setNodeIp(applicationNodeDTO.getIpAddress());
            applicationInstanceResult.setProcessNo(applicationNodeDTO.getProgressId());
            applicationInstanceResult.setMd5Value(applicationNodeDTO.getAgentMd5());

            applicationInstanceResult.setProbeStatus(applicationNodeDTO.getProbeStatus());
            applicationInstanceResult.setProbeVersion(applicationNodeDTO.getProbeVersion());

            applicationInstanceResult.setUpdateTime(DateUtil.getYYYYMMDDHHMMSS(applicationNodeDTO.getAgentUpdateTime()));
            return applicationInstanceResult;
        }).collect(Collectors.toList());
        return PagingList.of(applicationNodeResultList, totalCount);
    }

    @Override
    public PagingList<ApplicationNodeListResult> pageNode(QueryApplicationNodeParam param) {
        ApplicationNodeQueryDTO applicationQueryDTO = new ApplicationNodeQueryDTO();
        BeanUtils.copyProperties(param, applicationQueryDTO);
        PagingList<ApplicationNodeDTO> applicationNodePage = applicationClient.pageApplicationNode(applicationQueryDTO);
        return PagingList.of(CommonUtil.list2list(applicationNodePage.getList(), ApplicationNodeListResult.class), applicationNodePage.getTotal());
    }

    @Override
    public List<String> getOnlineAgentIds(ApplicationNodeQueryParam param) {
        if(CollectionUtils.isEmpty(param.getApplicationNames())) {
            return Lists.newArrayList();
        }

        List<ApplicationNodeDTO> nodeDTOS = Lists.newArrayList();
        // 50个轮询一次
        if (param.getApplicationNames().size() > LOOP_NUM) {
            int i = 1;
            boolean loop = true;
            do {
                List<String> subList = null;
                //批量处理
                if (param.getApplicationNames().size() > i * LOOP_NUM) {
                    subList = param.getApplicationNames().subList((i - 1) * 20, i * LOOP_NUM);
                } else {
                    subList =  param.getApplicationNames().subList((i - 1) * 100,  param.getApplicationNames().size());
                    loop = false;
                }
                i++;
                nodeDTOS.addAll(getNodeDTOS(subList));
            } while (loop);
        } else {
            nodeDTOS.addAll(getNodeDTOS(param.getApplicationNames()));
        }

        if(CollectionUtils.isEmpty(nodeDTOS)) {
            return Lists.newArrayList();
        }
        return nodeDTOS.stream().map(ApplicationNodeDTO::getAgentId).collect(Collectors.toList());
    }

    private List<ApplicationNodeDTO> getNodeDTOS(List<String> subList) {
        ApplicationNodeQueryDTO applicationQueryDTO = new ApplicationNodeQueryDTO();
        applicationQueryDTO.setCurrent(0);
        applicationQueryDTO.setPageSize(99999999);
        applicationQueryDTO.setAppNames(String.join(",",subList));
        try {
            PagingList<ApplicationNodeDTO> applicationNodePage = applicationClient.pageApplicationNodeV2(applicationQueryDTO);
            return applicationNodePage.getList();
        } catch (Exception e) {
            Log.error("从amdb查询在线agentId失败，查询应用:【{}】,失败原因:【{}】",
                applicationQueryDTO.getAppNames(),e);
            return Lists.newArrayList();
        }
    }

    @Override
    public ApplicationNodeResult getNodeByAgentId(String agentId) {
        ApplicationNodeResult applicationNode = new ApplicationNodeResult();
        applicationNode.setAgentId(agentId);
        applicationNode.setAgentLanguage("java");
        applicationNode.setAgentVersion("1.1");
        applicationNode.setCreateTime("2020-01-01 11:11:11");
        applicationNode.setNodeIp("127.0.0.1");
        applicationNode.setProcessNo("987");
        applicationNode.setMd5Value("sssssscdscve");
        applicationNode.setUpdateTime("2020-01-01 11:11:11");
        return applicationNode;
    }

}
