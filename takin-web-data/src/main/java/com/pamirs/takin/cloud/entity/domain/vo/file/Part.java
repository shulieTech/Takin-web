package com.pamirs.takin.cloud.entity.domain.vo.file;

import lombok.Data;

/**
 * @author hengyu
 * @date 2020-05-12 14:43
 */
@Data
public class Part {

    private String uuid;
    /**
     * 存储的目标文件名称
     */
    private String fileName;

    private String originalName;

    /**
     * 场景id
     */
    private Long sceneId;

    private String userAppKey;

    /**
     * 字节数组
     */
    private byte[] byteData;

    /**
     * 文件写入的行数
     */
    private String fileLineNumKey;

    /**
     * 起始位置
     */
    private Long start;
    /**
     * 文件块截止位置
     */
    private Long end;
    /**
     * md5 编码
     */
    private String md5;

    /**
     * md5 编码
     */
    private long total;

    /**
     * md5 编码
     */
    private boolean status;

    /**
     * 是否是重新上传的块数
     */
    private boolean isRetry;

    /**
     * 重新上传的次数
     */
    private int retryTimes;

    /**
     * 是否按照顺序拆分
     */
    private Long isOrderSplit = null;
    /**
     * 是否需要拆分
     */
    private Long isSplit = null;
    /**
     * 拆分文件块数
     */
    private Long dataCount = null;

}
