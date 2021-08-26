package io.shulie.takin.web.biz.pojo.response.perfomanceanaly;

import java.io.Serializable;

import lombok.Data;

/**
 * 堆内存图表
 *
 * @author qianshui
 * @date 2020/11/4 上午11:39
 */
@Data
public class DumpMemoryChartResponse implements Serializable {
    private static final long serialVersionUID = 8516935252719694745L;

    private String time;

    private Double totalMemory;

    private Double permMemory;

    private Double youngMemory;

    private Double oldMemory;

    private Integer youngGcCount;

    private Integer fullGcCount;

    private Long youngGcCost;

    private Long fullGcCost;

}
