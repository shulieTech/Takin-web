package com.pamirs.takin.common.constant;

import lombok.Getter;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: com.pamirs.takin.common.constant
 * @date 2020-03-17 18:31
 */
@Getter
public enum JobEnum {
    /**
     * 0-quartz、1-elastic-job、2-xxl-job 后期维护到字典表中
     */
    QUARTZ("quartz"),
    ELASTIC_JOB("elastic-job"),
    XXL_JOB("xxl-job"),
    UNKNOW("unknow"),
    SPRING_QUARTZ("spring-quartz"),
    ;

    private String text;

    JobEnum(String text) {
        this.text = text;
    }

    public static JobEnum getJobByIndex(int index) {
        for (JobEnum jobEnum : JobEnum.values()) {
            if (jobEnum.ordinal() == index) {
                return jobEnum;
            }
        }
        return JobEnum.UNKNOW;
    }

    public static JobEnum getJobByText(String text) {
        for (JobEnum jobEnum : JobEnum.values()) {
            if (text.equals(jobEnum.getText())) {
                return jobEnum;
            }
        }
        return JobEnum.UNKNOW;
    }
}
