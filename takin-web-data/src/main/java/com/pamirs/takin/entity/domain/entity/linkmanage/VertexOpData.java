package com.pamirs.takin.entity.domain.entity.linkmanage;

import java.util.Set;

import lombok.Data;

/**
 * @description: 节点IP信息、MQ TOPIC信息、DB  链接  表信息等
 * @author: CaoYanFei@ShuLie.io
 * @create: 2020-07-19 13:56
 **/
@Data
public class VertexOpData {
    private Set<String> ipList;
    private Set<String> dataList;
    private Set<String> unKnowIpList;
}
