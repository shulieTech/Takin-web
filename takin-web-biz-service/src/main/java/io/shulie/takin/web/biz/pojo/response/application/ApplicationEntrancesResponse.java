package io.shulie.takin.web.biz.pojo.response.application;

import java.util.Objects;

import io.shulie.takin.web.common.vo.WebOptionEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-29
 */
@Data
@ApiModel("应用入口返回值信息")
public class ApplicationEntrancesResponse extends WebOptionEntity {

    private String method;

    private String rpcType;

    private String extend;

    private String serviceName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (!super.equals(o)) {return false;}
        ApplicationEntrancesResponse that = (ApplicationEntrancesResponse)o;
        return Objects.equals(method, that.method) && Objects.equals(rpcType, that.rpcType) && Objects.equals(extend,
            that.extend) && Objects.equals(serviceName, that.serviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), method, rpcType, extend, serviceName);
    }
}
