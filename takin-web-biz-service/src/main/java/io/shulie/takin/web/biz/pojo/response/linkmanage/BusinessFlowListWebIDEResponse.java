package io.shulie.takin.web.biz.pojo.response.linkmanage;

import lombok.Data;

/**
 * @Author: 南风
 * @Date: 2022/7/11 10:27 上午
 */
@Data
public class BusinessFlowListWebIDEResponse extends BusinessFlowListResponse{

    private Integer virtualNum;

    private Integer normalNum;
}
