package io.shulie.takin.web.data.result.linkmange;

import java.util.Date;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/10/19 7:44 下午
 */
@Data
public class LinkManageResult extends UserCommonExt {

    private Long linkId;
    private String linkName;
    private String entrace;
    private String ptEntrace;
    private String changeBefore;
    private String changeAfter;
    private String changeRemark;
    private Integer isChange;
    private Integer isJob;
    private Integer isDeleted;
    private Date createTime;
    private Date updateTime;
    private String applicationName;
    private Integer changeType;
    private Integer canDelete;
    private String features;

}
