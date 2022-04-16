package io.shulie.takin.cloud.common.enums;

import lombok.Getter;

/**
 * @author moriarty
 */
public enum FileSliceStatusEnum {

    /**
     *
     */
    UNSLICED(0, "未分片"),
    SLICED(1, "已分片"),
    SLICING(2, "拆分中"),
    FILE_CHANGED(3, "文件变更");

    @Getter
    private String status;
    @Getter
    private int code;

    FileSliceStatusEnum(Integer code, String status) {
        this.code = code;
        this.status = status;
    }

    public static FileSliceStatusEnum getFileSliceStatusEnumByCode(int code) {
        for (FileSliceStatusEnum statusEnum : FileSliceStatusEnum.values()) {
            if (code == statusEnum.code) {
                return statusEnum;
            }
        }
        return null;
    }
}
