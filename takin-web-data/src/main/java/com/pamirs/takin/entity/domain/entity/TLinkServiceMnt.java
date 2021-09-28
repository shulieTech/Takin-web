package com.pamirs.takin.entity.domain.entity;

import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.LongToStringFormatSerialize;
import com.pamirs.takin.entity.domain.annotation.ExcelTag;

/**
 * 说明：基础链路服务实体类
 *
 * @author shulie
 * @version V1.0
 * @date 2018年3月1日 下午12:49:55
 */
@JsonIgnoreProperties(value = {"handler"})
public class TLinkServiceMnt extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // @Field id : 链路服务id
    @JsonSerialize(using = LongToStringFormatSerialize.class)
    @ExcelTag(name = "链路服务id", type = String.class)
    private long linkServiceId;

    // @Field linkId :链路id
    @JsonSerialize(using = LongToStringFormatSerialize.class)
    private long linkId;

    // @Field interfaceName : 用户名
    @NotBlank(message = "接口名称不能为空")
    @ExcelTag(name = "接口名称", type = String.class)
    private String interfaceName;

    // @Field interfaceDesc : 接口说明
    @NotBlank(message = "接口说明不能为空")
    @ExcelTag(name = "接口说明", type = String.class)
    private String interfaceDesc;

    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private long tenantId;
    /**
     * 用户id
     */
    private long userId;

    public long getTenantId() {
        return tenantId;
    }

    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEnvCode() {
        return envCode;
    }

    public void setEnvCode(String envCode) {
        this.envCode = envCode;
    }

    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;

    public TLinkServiceMnt() {
        super();
    }

    /**
     * 2018年5月17日
     *
     * @return the linkServiceId
     * @author shulie
     * @version 1.0
     */
    public long getLinkServiceId() {
        return linkServiceId;
    }

    /**
     * 2018年5月17日
     *
     * @param linkServiceId the linkServiceId to set
     * @author shulie
     * @version 1.0
     */
    public void setLinkServiceId(long linkServiceId) {
        this.linkServiceId = linkServiceId;
    }

    /**
     * 2018年5月17日
     *
     * @return the linkId
     * @author shulie
     * @version 1.0
     */
    public long getLinkId() {
        return linkId;
    }

    /**
     * 2018年5月17日
     *
     * @param linkId the linkId to set
     * @author shulie
     * @version 1.0
     */
    public void setLinkId(long linkId) {
        this.linkId = linkId;
    }

    /**
     * 2018年5月17日
     *
     * @return the interfaceName
     * @author shulie
     * @version 1.0
     */
    public String getInterfaceName() {
        return interfaceName;
    }

    /**
     * 2018年5月17日
     *
     * @param interfaceName the interfaceName to set
     * @author shulie
     * @version 1.0
     */
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    /**
     * 2018年5月17日
     *
     * @return the interfaceDesc
     * @author shulie
     * @version 1.0
     */
    public String getInterfaceDesc() {
        return interfaceDesc;
    }

    /**
     * 2018年5月17日
     *
     * @param interfaceDesc the interfaceDesc to set
     * @author shulie
     * @version 1.0
     */
    public void setInterfaceDesc(String interfaceDesc) {
        this.interfaceDesc = interfaceDesc;
    }

    /**
     * 2018年5月17日
     *
     * @return 实体字符串
     * @author shulie
     * @version 1.0
     */
    @Override
    public String toString() {
        return "TLinkServiceMnt [linkServiceId=" + linkServiceId + ", linkId=" + linkId + ", interfaceName="
            + interfaceName + ", interfaceDesc=" + interfaceDesc + "]";
    }

}
