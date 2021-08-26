package io.shulie.takin.web.amdb.bean.result.application;

import java.io.Serializable;

import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-10-12
 */
@Data
public class ApplicationInterfaceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接口ID
     */
    private String id;

    /**
     * 接口类型
     */
    private String interfaceType;

    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * 应用名
     */
    private String appName;

    @Override
    public int hashCode() {
        return (appName + "#" + interfaceName + "#" + interfaceType).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ApplicationInterfaceDTO that = (ApplicationInterfaceDTO)o;
        return interfaceType.equals(that.interfaceType) && interfaceName.equals(that.interfaceName) && appName.equals(that.appName);
    }
}
