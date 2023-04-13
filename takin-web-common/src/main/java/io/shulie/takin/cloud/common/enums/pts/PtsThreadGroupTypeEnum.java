package io.shulie.takin.cloud.common.enums.pts;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum PtsThreadGroupTypeEnum {

    SETUP("setUp", "setUp线程组", 1),
    NORMAL("normal", "普通线程组",2),
    TEARDOWN("tearDown", "tearDown线程组",9),
    ;

    private String type;

    private String desc;

    private Integer sortNum;

    PtsThreadGroupTypeEnum(String type, String desc, Integer sortNum) {
        this.type = type;
        this.desc = desc;
        this.sortNum = sortNum;
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
