package com.pamirs.takin.cloud.entity.domain.entity;

import java.util.Date;

import lombok.Data;

/**
 * @author -
 */
@Data
public class TDictionaryData {

    private String id;

    private String dictType;

    private Integer valueOrder;
    private String valueName;

    private String valueCode;

    private String language;

    private String active;

    private Date createTime;

    private Date modifyTime;

    private String createUserCode;

    private String modifyUserCode;

    private String noteInfo;

    private Long versionNo;

}
