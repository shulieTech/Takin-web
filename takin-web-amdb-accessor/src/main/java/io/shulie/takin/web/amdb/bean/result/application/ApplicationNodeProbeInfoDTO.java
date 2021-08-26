package io.shulie.takin.web.amdb.bean.result.application;

import java.util.List;

import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/6/9 2:01 下午
 */
@Data
public class ApplicationNodeProbeInfoDTO {

    /**
     * 在线节点数
     */
    private Long onlineNodesCount;

    /**
     * 已安装探针节点数
     */
    private Long specificStatusNodesCount;

    /**
     * 节点探针版本列表
     */
    private List<String> versionList;

}
