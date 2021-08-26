package io.shulie.takin.web.biz.pojo.response.application;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/27 9:44 上午
 */
@Data
public class ApplicationDsResponse extends AuthQueryResponseCommonExt {
    /**
     * 配置id
     */
    private Long id;

    /**
     * 应用id
     */
    private Long applicationId;

    /**
     * 存储类型，0：数据库 1：缓存
     */
    private DbTypeResponse dbType;

    /**
     * 方案类型，0：影子库 1：影子表 2:影子server
     */
    private DsTypeResponse dsType;

    /**
     * 服务器地址
     */
    private String url;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
