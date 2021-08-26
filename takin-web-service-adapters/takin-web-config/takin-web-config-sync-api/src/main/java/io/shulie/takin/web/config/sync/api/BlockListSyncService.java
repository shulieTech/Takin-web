package io.shulie.takin.web.config.sync.api;

import java.util.List;

import io.shulie.takin.web.config.enums.BlockListType;

/**
 * @author fanxx
 * @date 2020/9/28 5:13 下午
 */
public interface BlockListSyncService {

    void syncBlockList(String namespace, BlockListType type, List<String> blockLists);

}
