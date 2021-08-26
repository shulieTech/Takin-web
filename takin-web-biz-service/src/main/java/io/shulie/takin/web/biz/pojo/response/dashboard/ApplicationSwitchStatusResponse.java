package io.shulie.takin.web.biz.pojo.response.dashboard;

import java.util.List;
import java.util.Map;

import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import lombok.Data;

@Data
public class ApplicationSwitchStatusResponse {
    /**
     * 应用名称
     */
    private String applicationName;
    /**
     * 开关状态
     * <p>
     * OPENED("已开启",0)
     * <br/>
     * OPENING("开启中",1)
     * <br/>
     * OPEN_FAILING("开启异常",2)
     * <br/>
     * CLOSED("已关闭",3)
     * <br/>
     * CLOSING("关闭中",4)
     * <br/>
     * CLOSE_FAILING("关闭异常",5)
     */
    private String switchStatus;
    /**
     * 异常信息
     */
    private Map<String, Object> exceptionMap;
    /**
     * 节点唯一键
     */
    private String nodeKey;
    /**
     * 节点ip
     */
    private String nodeIP;
    /**
     * 节点列表信息
     */
    private List<ApplicationVo> errorList;
    /**
     * 应用接入状态
     */
    private Integer accessStatus;
}
