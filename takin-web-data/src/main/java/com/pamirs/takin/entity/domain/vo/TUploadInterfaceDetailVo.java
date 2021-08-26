package com.pamirs.takin.entity.domain.vo;

/**
 * 说明: job dubbo 接口抓取 数据上传 详情
 *
 * @author 298403
 * @version v1.0
 * @date Create in 2019/1/14
 */
public class TUploadInterfaceDetailVo {

    /**
     * 类型 dubbo / job
     */
    private String type;

    /**
     * 接口名称
     */
    private String interfaceName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    @Override
    public String toString() {
        return "TUploadInterfaceDetailVo{" +
            "type='" + type + '\'' +
            ", interfaceName='" + interfaceName + '\'' +
            '}';
    }
}
