package io.shulie.takin.web.biz.service;

import java.util.Map;

public interface AopsService {

    /**
     * 查询 cpu memory io
     *
     * @param ip
     * @param startSecond
     * @param endSecond
     * @param time
     * @return
     */
    public Map getAopsData(String ip, String startSecond, String endSecond, String time);
}
