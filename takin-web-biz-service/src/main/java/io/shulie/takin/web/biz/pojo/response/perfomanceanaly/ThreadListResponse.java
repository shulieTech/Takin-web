package io.shulie.takin.web.biz.pojo.response.perfomanceanaly;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
* @author qianshui
 * @date 2020/11/4 上午11:32
 */
@Data
public class ThreadListResponse implements Serializable {
    private static final long serialVersionUID = -4546154536371347630L;

    private List<ThreadStatusResponse> status;

    private List<ThreadDetailResponse> details;
}
