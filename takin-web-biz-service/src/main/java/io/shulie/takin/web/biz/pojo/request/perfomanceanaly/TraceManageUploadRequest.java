package io.shulie.takin.web.biz.pojo.request.perfomanceanaly;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.common.vo.agent.TraceVO;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class TraceManageUploadRequest implements Serializable {
    private static final long serialVersionUID = 419235056269493506L;

    /**
     * 追踪实例对象
     */
    @JsonProperty("methodPattern")
    private String traceDeployObject;

    /**
     * 追踪凭证
     */
    private String sampleId;

    /**
     * 状态0:待采集;1:采集中;2:采集结束
     */
    private Integer status;

    /**
     * 调用栈数据
     */
    List<TraceVO> vos;

}
