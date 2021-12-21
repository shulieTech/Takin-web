package io.shulie.takin.web.data.param.perfomanceanaly;

import java.util.List;

import io.shulie.takin.web.common.vo.perfomanceanaly.MemoryEntryVO;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.Data;

/**
 * @author qianshui
 * @date 2020/11/4 下午2:35
 */
@Data
public class PerformanceBaseDataParam extends TenantCommonExt {

    private String agentId;

    private String appName;

    private String appIp;

    private Long processId;

    /**
     * old memory used
     */
    private Long oldMemory;

    /**
     * total old memory
     */
    private Long totalOld;
    /**
     * perm memory used
     */
    private Long permMemory;

    /**
     * total perm memory
     */
    private Long totalPerm;

    /**
     * young memory used
     */
    private Long youngMemory;

    /**
     * total young memory
     */
    private Long totalYoung;

    /**
     * 非堆内存大小
     */
    private Long totalNonHeapMemory;

    /**
     * buffer pool 总内存大小
     */
    private Long totalBufferPoolMemory;

    private Long totalMemory;

    private Integer youngGcCount;

    /**
     * young gc 次数
     */
    private Integer fullGcCount;

    /**
     * young gc 耗时
     */
    private Long youngGcCost;

    /**
     * full gc 耗时
     */
    private Long fullGcCost;

    private String processName;

    private Long timestamp;

    private List<PerformanceThreadDataParam> threadDataList;

    /**
     * 堆内存
     */
    private MemoryEntryVO heapMemory;

    /**
     * 堆内存详情
     */
    private List<MemoryEntryVO> heapMemories;

    /**
     * 非堆内存
     */
    private MemoryEntryVO nonheapMemory;

    /**
     * 非堆内存
     */
    private List<MemoryEntryVO> nonheapMemories;

    /**
     * buffer pool 内存
     */
    private List<MemoryEntryVO> bufferPoolMemories;

}
