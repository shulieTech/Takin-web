package io.shulie.takin.cloud.biz.service.schedule;

import io.shulie.takin.cloud.common.constants.ScheduleEventConstant;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleRunRequest;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStartRequestExt;
import io.shulie.takin.cloud.ext.content.enginecall.ScheduleStopRequestExt;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.eventcenter.EventCenterTemplate;
import io.shulie.takin.eventcenter.annotation.IntrestFor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 莫问
 * @date 2020-05-12
 */
@Component
public class ScheduleEventService {

    @Autowired
    private EventCenterTemplate eventCenterTemplate;

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 调度初始化
     *
     * @param request -
     */
    public void initSchedule(ScheduleRunRequest request) {
        Event event = new Event();
        event.setEventName(ScheduleEventConstant.INIT_SCHEDULE_EVENT);
        event.setExt(request);
        eventCenterTemplate.doEvents(event);
    }

    /**
     * 运行调度
     *
     * @param event -
     */
    @IntrestFor(event = ScheduleEventConstant.RUN_SCHEDULE_EVENT)
    public void runSchedule(Event event) {
        scheduleService.runSchedule((ScheduleRunRequest)event.getExt());
    }

    /**
     * 启动调度
     *
     * @param event -
     */
    @IntrestFor(event = ScheduleEventConstant.START_SCHEDULE_EVENT)
    public void startSchedule(Event event) {
        ScheduleStartRequestExt request = (ScheduleStartRequestExt)event.getExt();
        scheduleService.startSchedule(request);

    }

    /**
     * 停止调度
     *
     * @param event -
     */
    @IntrestFor(event = ScheduleEventConstant.STOP_SCHEDULE_EVENT)
    public void stopSchedule(Event event) {
        ScheduleStopRequestExt request = (ScheduleStopRequestExt)event.getExt();
        scheduleService.stopSchedule(request);
    }

}
