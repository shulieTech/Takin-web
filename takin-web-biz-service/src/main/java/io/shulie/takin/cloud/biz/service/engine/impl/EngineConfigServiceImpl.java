package io.shulie.takin.cloud.biz.service.engine.impl;

import java.util.Objects;

import io.shulie.surge.data.common.zk.ZkClient;
import io.shulie.takin.cloud.biz.service.engine.EngineConfigService;
import io.shulie.takin.cloud.common.constants.ZkNodePathConstants;
import io.shulie.takin.cloud.common.utils.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author moriarty
 */
@Service
@Slf4j
public class EngineConfigServiceImpl implements EngineConfigService {

    @Autowired
    private ZkClient zkClient;

    /**
     * 获取需要挂载本地磁盘的场景ID
     *
     * @return -
     */
    @Override
    public String[] getLocalMountSceneIds() {
        String result = "";
        try {
            byte[] data = zkClient.getData(ZkNodePathConstants.LOCAL_MOUNT_SCENE_IDS_PATH);
            if (Objects.nonNull(data) && data.length > 0) {
                result = new String(data);
            }
        } catch (Exception e) {
            return new String[] {};
        }
        if (StringUtils.isBlank(result)) {
            return new String[] {};
        }
        return result.trim().split(",");
    }

    @Override
    public Integer getLogSimpling() {
        try {
            byte[] data = zkClient.getData(ZkNodePathConstants.LOG_SAMPLING_PATH);
            if (Objects.nonNull(data) && data.length > 0) {
                return NumberUtil.parseInt(new String(data), 1);
            }
        } catch (Exception e) {
        }
        return 1;
    }

}
