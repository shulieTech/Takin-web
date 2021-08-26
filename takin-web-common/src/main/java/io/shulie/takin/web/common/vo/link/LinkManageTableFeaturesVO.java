package io.shulie.takin.web.common.vo.link;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * 技术链路下的 features 映射类
 *
 * @author liuchuan
 * @date 2021/7/2 2:40 下午
 */
@Getter
@Setter
public class LinkManageTableFeaturesVO {

    /**
     * 中间件类型
     * @see io.shulie.takin.web.amdb.bean.common.EntranceTypeEnum
     */
    private String serverMiddlewareType;

    /**
     * rpcType
     * @see io.shulie.amdb.common.enums.RpcType
     */
    private String rpcType;

    /**
     * rpcType int 类型
     *
     * @return rpcType int 类型
     */
    public Integer getRpcTypeInteger() {
        if (StringUtils.isBlank(rpcType)) {
            return null;
        }

        return Integer.valueOf(rpcType);
    }

}
