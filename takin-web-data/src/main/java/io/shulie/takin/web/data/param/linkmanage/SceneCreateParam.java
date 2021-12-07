package io.shulie.takin.web.data.param.linkmanage;

import java.util.Date;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/4 2:56 下午
 */
@Data
public class SceneCreateParam extends UserCommonExt {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 场景名
     */
    private String sceneName;
    /**
     * 场景所绑定的业务链路名集合
     */
    private String businessLink;
    /**
     * 场景等级 :p0/p1/02/03
     */
    private String sceneLevel;
    /**
     * 是否核心场景 0:不是;1:是
     */
    private Integer isCore;
    /**
     * 是否有变更 0:没有变更，1有变更
     */
    private Integer isChanged;
    /**
     * 是否有效 0:有效;1:无效
     */
    private Integer isDeleted;
    /**
     * 插入时间
     */
    private Date createTime;
    /**
     * 变更时间
     */
    private Date updateTime;

}
