package io.shulie.takin.cloud.common.pojo.vo.middleware;

import java.util.List;

import lombok.Data;

/**
 * 比对结果的结果和备注
 *
 * @author liuchuan
 * @date 2021/6/2 11:03 上午
 */
@Data
public class RemarksAndStatusDescVO {

    /**
     * 比对结果
     */
    private String statusDesc;

    /**
     * 比对备注
     */
    private List<String> remarks;

}
