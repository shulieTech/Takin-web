package io.shulie.takin.web.biz.task;

import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author caijianying
 */
@Component
public interface SimpleTask {
    /**
     * 默认分片总数量2
     */
     long defaultPartTotal = 2;
    /**
     * 分片总数量
     */
    long partTotal = defaultPartTotal;

    @Value("${takin.task.machine-id:1}")
     Integer machineId = null;

     default long getPartTotal(){
         String total = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_REPORT_PART_TOTAL);
         return total==null?defaultPartTotal:Long.valueOf(total);
    }

    default long getMachieId(){
        return machineId;
    }



    /**
     * 任务运行
     */
    void runTask();

}
