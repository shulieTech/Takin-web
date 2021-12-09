package io.shulie.takin.web.biz.service.dashboard.impl;

import java.util.List;

import javax.annotation.Resource;

import com.pamirs.takin.entity.domain.dto.linkmanage.SystemProcessViewListDto;
import com.pamirs.takin.entity.domain.vo.linkmanage.queryparam.TechQueryVo;
import io.shulie.takin.web.biz.pojo.response.dashboard.ApplicationStatusResponse;
import io.shulie.takin.web.biz.pojo.response.dashboard.ApplicationSwitchStatusResponse;
import io.shulie.takin.web.biz.pojo.response.dashboard.UserWorkBenchResponse;
import io.shulie.takin.web.biz.service.dashboard.DashboardApplicationService;
import io.shulie.takin.web.biz.service.dashboard.WorkBenchService;
import io.shulie.takin.web.common.common.Response;
import org.springframework.stereotype.Service;

@Service
public class WorkBenchServiceImpl implements WorkBenchService {
    @Resource
    private DashboardApplicationService applicationService;
    @Resource
    private DashboardApplicationService dashboardApplicationService;

    /**
     * 获取用户的工作台统计信息
     *
     * @return 统计信息
     */
    @Override
    public UserWorkBenchResponse getWorkBench() {
        UserWorkBenchResponse result = new UserWorkBenchResponse();
        //压测开关
        ApplicationSwitchStatusResponse status = dashboardApplicationService.getUserAppSwitchInfo();
        result.setSwitchStatus(status.getSwitchStatus());

        //应用数量
        //appResponse
        ApplicationStatusResponse appStatusCount = applicationService.getAppStatusCount();
        if (appStatusCount != null) {
            result.setApplicationNum(appStatusCount.getApplicationNum());
            result.setAccessErrorNum(appStatusCount.getAccessErrorNum());
        }
        //系统流程
        TechQueryVo techQuery = new TechQueryVo();
        techQuery.setPageSize(Integer.MAX_VALUE);
        Response<List<SystemProcessViewListDto>> listResponse = null;
        //linkManageService.gettechLinksViwList(techQuery);
        if (listResponse != null && listResponse.getData() != null) {
            int changeNum = 0;
            for (SystemProcessViewListDto vo : listResponse.getData()) {
                if (vo.getIsChange() != null && "1".equals(vo.getIsChange())) {
                    changeNum++;
                }
            }
            result.setChangedProcessNum(changeNum);
            result.setSystemProcessNum(listResponse.getData().size());
        } else {
            result.setSystemProcessNum(0);
            result.setChangedProcessNum(0);
        }
        return result;
    }
}
