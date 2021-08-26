package io.shulie.takin.web.config.sync.file.impl;

import java.util.List;

import io.shulie.takin.web.config.enums.BlockListType;
import io.shulie.takin.web.config.sync.api.BlockListSyncService;
import org.springframework.stereotype.Component;

/**
 * @author fanxx
 * @date 2020/9/28 5:16 下午
 */
@Component
public class BlockListSyncServiceImpl implements BlockListSyncService {

    @Override
    public void syncBlockList(String namespace, BlockListType type, List<String> blockLists) {
        //TODO 待讨论细节
    }
}
