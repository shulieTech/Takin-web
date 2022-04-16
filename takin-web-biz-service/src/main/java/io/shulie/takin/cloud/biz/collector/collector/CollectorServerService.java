package io.shulie.takin.cloud.biz.collector.collector;

import java.util.List;

import io.shulie.takin.cloud.biz.collector.bean.DiskUsage;
import io.shulie.takin.cloud.biz.collector.bean.LoadInfo;
import io.shulie.takin.cloud.biz.collector.bean.ServerStatusInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 计算压测引擎上报的服务器状态。用于弹性伸缩
 *
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-05-11 14:26
 */
@Slf4j
@Service
public class CollectorServerService {

    public void collector(ServerStatusInfo serverStatusInfo) {
        int cpu = collectorCpu(serverStatusInfo.getCpu());
        int memory = collectorMemory(serverStatusInfo.getMemery());
        int io = collectorIo(serverStatusInfo.getIo());
        int disk = collectorDisk(serverStatusInfo.getDiskUsages());
        int loader = collectorLoader(serverStatusInfo.getLoadInfo());
    }

    /**
     * 计算CPU使用率
     * -1 缩容
     * 0  正常
     * 1  扩容
     *
     * @param cpu -
     * @return -
     */
    public int collectorCpu(float cpu) {
        return 0;
    }

    /**
     * 计算内存使用率
     * -1 缩容
     * 0  正常
     * 1  扩容
     *
     * @param memory -
     * @return -
     */
    public int collectorMemory(long memory) {
        return 0;
    }

    /**
     * 计算io使用率
     * -1 缩容
     * 0  正常
     * 1  扩容
     *
     * @param io -
     * @return -
     */
    public int collectorIo(String io) {
        return 0;
    }

    /**
     * 计算loader使用率
     * -1 缩容
     * 0  正常
     * 1  扩容
     *
     * @param loadInfo -
     * @return -
     */
    public int collectorLoader(LoadInfo loadInfo) {
        return 0;
    }

    /**
     * 计算磁盘使用率
     * -1 缩容
     * 0  正常
     * 1  扩容
     *
     * @param diskUsages -
     * @return -
     */
    public int collectorDisk(List<DiskUsage> diskUsages) {
        return 0;
    }
}
