package io.shulie.takin.web.biz.pojo.response.application;

import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/27 9:44 上午
 */
@Data
public class ApplicationDsV2Response extends AuthQueryResponseCommonExt {
    /**
     * 配置id
     */
    private Long id;

    /**
     * 应用id
     */
    private Long applicationId;

    /**
     * 中间件类型
     */
    private String middlewareType;

    /**
     * 隔离类型
     */
    private String dsType;

    /**
     * 连接池地址
     */
    private String url;

    /**
     * 连接池名称
     */
    private String connectionPool;

    /**
     * 附加信息
     */

    private String extMsg;

    /**
     * 是否手动添加
     */
    private String source;

    private String agentSourceType;
}
