package io.shulie.takin.web.data.result.linkmange;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author fanxx
 * @date 2021/1/15 11:00 上午
 */
@Data
public class SceneResult {
    private Long id;
    private String sceneName;
    private Long customerId;
    private Long userId;

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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 场景类型，标识1为jmeter上传，默认0
     */
    private Integer type;

    /**
     * 存储树状结构
     */
    private String scriptJmxNode;

    /**
     * 脚本实例id
     */
    private Long scriptDeployId;

    /**
     * 关联节点数
     */
    private Integer linkRelateNum;

    /**
     * 脚本总节点数
     */
    private Integer totalNodeNum;
}
