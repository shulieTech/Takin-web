package io.shulie.takin.cloud.common.enums.pts;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum PtsThreadGroupTypeEnum {

    SETUP("setUp", "setUp线程组"),
    NORMAL("normal", "普通线程组"),
    TEARDOWN("tearDown", "tearDown线程组"),
    ;

    private String type;

    private String desc;

    PtsThreadGroupTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static PtsThreadGroupTypeEnum getByType(String type) {
        if(StringUtils.isBlank(type)) {
            return NORMAL;
        }
        for(PtsThreadGroupTypeEnum var : PtsThreadGroupTypeEnum.values()) {
            if(var.getType().equals(type)) {
                return var;
            }
        }
        return NORMAL;
    }
}
