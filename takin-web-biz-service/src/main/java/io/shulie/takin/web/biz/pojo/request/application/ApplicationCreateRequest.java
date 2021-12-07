package io.shulie.takin.web.biz.pojo.request.application;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.LongToStringFormatSerialize;
import lombok.Data;

/**
* @Package io.shulie.takin.web.biz.pojo.request.application
* @ClassName: ApplicationCreateRequest
* @author hezhongqi
* @description:
* @date 2021/12/1 16:46
*/
@Data
public class ApplicationCreateRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    //@Field applicationId : 应用编号
    @JsonSerialize(using = LongToStringFormatSerialize.class)
    private long applicationId;

    //@Field applicationName : 应用名称
    @NotBlank(message = "应用名称不能为空")
    private String applicationName;

    //@Field applicationName : 应用说明
    @NotBlank(message = "应用说明不能为空")
    private String applicationDesc;

    //@Field ddlScriptPath : 影子库表结构脚本路径`
    @NotBlank(message = "影子库表结构脚本路径不能为空")
    private String ddlScriptPath;

    //@Field cleanScriptPath : 数据清理脚本路径
    @NotBlank(message = "数据清理脚本路径不能为空")
    private String cleanScriptPath;

    //@Field readyScriptPath : 基础数据准备脚本路径
    @NotBlank(message = "基础数据准备脚本路径不能为空")
    private String readyScriptPath;

    //@Field basicScriptPath : 铺底数据脚本路径
    @NotBlank(message = "铺底数据脚本路径不能为空")
    private String basicScriptPath;

    //@Field cacheScriptPath : 缓存预热脚本地址
    @NotBlank(message = "缓存预热脚本地址不能为空")
    private String cacheScriptPath;

    // @Field useYn : 是否可用(0表示不可用;1表示可用)
    //@Range(min=0,max=1,message="有效值必须在0和1之间")
    private String useYn;

    //'接入状态； 0：正常 ； 1；待配置 ；2：待检测 ; 3：异常
    //@Range(min=0,max=3,message="应用接入状态")
    private Integer accessStatus;

    private String exceptionInfo;

    //节点数量
    private Integer nodeNum;

    private String switchStatus;
    //@Field cacheExpTime : 缓存失效时间
    @Min(value = 0, message = "缓存过期时间最小为0,0表示永不过期")
    private String cacheExpTime;
    //告警人，在链路探活中使用
    private String alarmPerson;
    private String pradarVersion;

    /**
     * 2018年5月17日
     *
     * @return 实体字符串
     * @author shulie
     * @version 1.0
     */
    @Override
    public String toString() {
        return "TApplicationMnt{" +
            "applicationId=" + applicationId +
            ", applicationName='" + applicationName + '\'' +
            ", applicationDesc='" + applicationDesc + '\'' +
            ", ddlScriptPath='" + ddlScriptPath + '\'' +
            ", cleanScriptPath='" + cleanScriptPath + '\'' +
            ", readyScriptPath='" + readyScriptPath + '\'' +
            ", basicScriptPath='" + basicScriptPath + '\'' +
            ", cacheScriptPath='" + cacheScriptPath + '\'' +
            ", alarmPerson='" + alarmPerson + '\'' +
            ", useYn='" + useYn + '\'' +
            ", cacheExpTime='" + cacheExpTime + '\'' +
            '}';
    }
    // add by 557092, in order to compare the object is equals to other or not.

    /**
     * 重写hashCode值，判断对象是否相等
     * 2018年5月17日
     *
     * @return hashCode
     * @author shulie
     * @version 1.0
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int)(applicationId ^ (applicationId >>> 32));
        result = prime * result + ((applicationName == null) ? 0 : applicationName.hashCode());
        return result;
    }

    // add by 557092, in order to compare the object is equals to other or not.

    /**
     * 重写equals()，判断对象是否相等
     * 2018年5月17日
     *
     * @return true相等 false不相等
     * @author shulie
     * @version 1.0
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ApplicationCreateRequest other = (ApplicationCreateRequest)obj;
        if (applicationId != other.applicationId) {
            return false;
        }
        if (applicationName == null) {
            if (other.applicationName != null) {
                return false;
            }
        } else if (!applicationName.equals(other.applicationName)) {
            return false;
        }
        return true;
    }
}
