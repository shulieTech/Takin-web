package io.shulie.takin.web.biz.task;

import com.alibaba.fastjson.JSON;

import dai.samples.redis.delay.bean.DelayJob;
import dai.samples.redis.delay.bean.Job;
import dai.samples.redis.delay.constants.DelayConfig;
import io.shulie.takin.web.common.enums.job.JobStatus;
import io.shulie.takin.web.common.job.TakinDelayTask;
import io.shulie.takin.web.common.job.TakinTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
* @Package io.shulie.takin.web.biz.task
* @ClassName: TaskHandler
* @author hezhongqi
* @description:
* @date 2021/11/30 09:47
*/
@Slf4j
@Data
@AllArgsConstructor
public class TaskHandler implements Runnable{

    /**
     * 延迟队列
     */
    private DelayBucket delayBucket;
    /**
     * 任务池
     */
    private TaskPool taskPool;
    
    private ReadyQueue readyQueue;
    /**
     * 索引
     */
    private int index;

    /**
     */
    @Override 
    public void run() {
        log.info("定时任务开始执行");
        while (true) {
            try {
                TakinDelayTask delayTask = delayBucket.getFirstDelayTime(index);
                //没有任务
                if (delayTask == null) {
                    sleep();
                    continue;
                }
                // 发现延时任务
                // 延迟时间没到
                if (delayTask.getDelayDate() > System.currentTimeMillis()) {
                    sleep();
                    continue;
                }
                TakinTask task = taskPool.getJob(delayTask.getJodId());

                //延迟任务元数据不存在
                if (task == null) {
                    log.info("移除不存在任务:{}", JSON.toJSONString(delayTask));
                    delayBucket.removeDelayTime(index,delayTask);
                    continue;
                }

                JobStatus status = task.getStatus();
                if (JobStatus.RESERVED.equals(status)) {
                    log.info("处理超时任务:{}", JSON.toJSONString(task));
                    // 超时任务
                    processTtrJob(delayTask,task);
                } else {
                    log.info("处理延时任务:{}", JSON.toJSONString(job));
                    // 延时任务
                    processDelayJob(delayJob,job);
                } 
            } catch (Exception e) {
                log.error("扫描DelayBucket出错：",e.getStackTrace());
                sleep();
            }
        }
    }

    /**
     * 处理ttr的任务
     */
    private void processTtrJob(TakinDelayTask delayTask,TakinTask task) {
        task.setStatus(JobStatus.DELAY);
        // 修改任务池状态
        taskPool.addTask(task);
        // 移除delayBucket中的任务
        delayBucket.removeDelayTime(index,delayTask);
        Long delayDate = System.currentTimeMillis() + task.getDelayTime();
        delayTask.setDelayDate(delayDate);
        // 再次添加到任务中
        delayBucket.addDelayJob(delayTask);
    }

    /**
     * 处理延时任务
     */
    private void processDelayJob(TakinDelayTask delayTask,TakinTask task) {
        task.setStatus(JobStatus.READY);
        // 修改任务池状态
        taskPool.addTask(task);
        // 设置到待处理任务
        readyQueue.pushJob(delayTask);
        // 移除delayBucket中的任务
        delayBucket.removeDelayTime(index,delayTask);
    }
    
    private void sleep(){
        try {
            Thread.sleep(DelayConfig.SLEEP_TIME);
        } catch (InterruptedException e){
            log.error("",e);
        }
    }
}
