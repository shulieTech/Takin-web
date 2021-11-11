package io.shulie.takin.web.data.param.linkmanage;

import java.util.Date;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;

/**
 * 业务活动修改
 *
 * @author ZhangXT
 * @date 2020/11/5 17:38
 */
@Data
public class BusinessLinkManageUpdateParam extends UserCommonExt {
    /**
     * 主键
     */
    private Long linkId;
    /**
     * 链路名称
     */
    private String linkName;
    /**
     * 链路入口
     */
    private String entrace;
    /**
     * 业务链路绑定的技术链路
     */
    private String relatedTechLink;
    /**
     * 业务链路级别: p0/p1/p2/p3
     */
    private String linkLevel;
    /**
     * 业务链路的上级业务链路名
     */
    private String parentBusinessId;
    /**
     * 是否有变更 0:正常；1:已变更
     */
    private Integer isChange;
    /**
     * 业务链路是否否核心链路 0:不是;1:是
     */
    private Integer isCore;
    /**
     * 是否有效 0:有效;1:无效
     */
    private Integer isDeleted;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 业务域
     */
    private String businessDomain;
    /**
     * 是否能删除
     */
    private Integer canDelete;



    public BusinessLinkManageUpdateParam() {
    }

}
