package io.shulie.takin.web.common.domain;

/**
 * @author shiyajian
 * create: 2020-09-24
 */

import java.util.List;

/**
 * @author shiyajian
 * create: 2020-08-21
 */
@Deprecated
public class TakinTraceParam {
    private String entry;

    private Long startTime;

    private Long endTime;

    private String type;

    private List<String> entranceList;

    private int pageNum;

    private int pageSize;

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public List<String> getEntranceList() {
        return entranceList;
    }

    public void setEntranceList(List<String> entranceList) {
        this.entranceList = entranceList;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
