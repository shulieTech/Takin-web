package io.shulie.takin.cloud.biz.collector.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-05-11 14:30
 */
@Getter
@Setter
public class LoadInfo {

    private int cpuNum;
    private String load_1;
    private String load_2;
    private String load_3;
}
