package com.pamirs.takin.entity.domain.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.DateToStringFormatSerialize;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 说明: 实体类基类
 *
 * @author shulie
 * @version v1.0
 * @date 2018年4月20日
 */
@JsonIgnoreProperties(value = {"handler"})
public class BaseEntity implements Serializable {

    //序列号
    private static final long serialVersionUID = 1L;

    // @Field createTime : 数据插入时间
    @ApiModelProperty(name = "createTime", value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = DateToStringFormatSerialize.class)
    private Date createTime;

    // @Field updateTime : 数据更新时间
    @ApiModelProperty(name = "updateTime", value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = DateToStringFormatSerialize.class)
    private Date updateTime;

    /**
     * 无参构造
     */
    public BaseEntity() {
        super();
    }

    /**
     * 2018年5月17日
     *
     * @return the createTime
     * @author shulie
     * @version 1.0
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 2018年5月17日
     *
     * @param createTime the createTime to set
     * @author shulie
     * @version 1.0
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 2018年5月17日
     *
     * @return the updateTime
     * @author shulie
     * @version 1.0
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 2018年5月17日
     *
     * @param updateTime the updateTime to set
     * @author shulie
     * @version 1.0
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
