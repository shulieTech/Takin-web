package io.shulie.takin.web.data.result.scene;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/5/28 5:40 下午
 */
@Data
public class SceneLinkRelateResult {
    /**
     * 主键
     */
    private Long id;

    /**
     * 链路入口
     */
    private String entrance;

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
     * 业务链路ID
     */
    private String businessLinkId;

    /**
     * 技术链路ID
     */
    private String techLinkId;

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 当前业务链路ID的上级业务链路ID
     */
    private String parentBusinessLinkId;

    /**
     * 前端数结构对象key
     */
    private String frontUuidKey;

    /**
     * 脚本请求路径标识
     */
    private String scriptIdentification;

    /**
     * 脚本路径的MD5
     */
    private String scriptXpathMd5;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 环境
     */
    private String envCode;
}
