package io.shulie.takin.web.amdb.bean.query.application;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/20 2:02 下午
 */
@Data
public class ApplicationInterfaceQueryDTO {
    private String appName;
    private String rpcType;
    private String serviceName;
    private String methodName;
    private String middlewareName;
    private String fieldNames;
    private Integer pageSize;
    private Integer currentPage;

}
