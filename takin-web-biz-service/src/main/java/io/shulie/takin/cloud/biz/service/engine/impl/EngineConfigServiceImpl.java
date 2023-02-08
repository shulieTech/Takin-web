package io.shulie.takin.cloud.biz.service.engine.impl;

import io.shulie.takin.cloud.biz.service.engine.EngineConfigService;
import io.shulie.takin.cloud.common.constants.ZkNodePathConstants;
import io.shulie.takin.cloud.common.utils.NumberUtil;
import io.shulie.takin.web.data.dao.pradar.PradarZkConfigDAO;
import io.shulie.takin.web.data.result.pradarzkconfig.PradarZkConfigResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author moriarty
 */
@Service
@Slf4j
public class EngineConfigServiceImpl implements EngineConfigService {

    @Resource
    private PradarZkConfigDAO pradarZkConfigDAO;

    /**
     * 获取需要挂载本地磁盘的场景ID
     *
     * @return -
     */
    @Override
    public String[] getLocalMountSceneIds() {
        String result = "";
        try {
            PradarZkConfigResult byZkPath = pradarZkConfigDAO.getByZkPath(ZkNodePathConstants.LOCAL_MOUNT_SCENE_IDS_PATH);
            if (byZkPath != null) {
                result = byZkPath.getValue();
            }
        } catch (Exception e) {
            return new String[]{};
        }
        if (StringUtils.isBlank(result)) {
            return new String[]{};
        }
        return result.trim().split(",");
    }

    @Override
    public Integer getLogSimpling() {
        try {
            PradarZkConfigResult byZkPath = pradarZkConfigDAO.getByZkPath(ZkNodePathConstants.LOG_SAMPLING_PATH);
            if (byZkPath != null) {
                return NumberUtil.parseInt(byZkPath.getValue(), 1);
            }
        } catch (Exception e) {
        }
        return 1;
    }

}
