package io.shulie.takin.web.data.result.system;

import java.io.Serializable;

import io.shulie.takin.web.data.model.mysql.VersionEntity;
import lombok.Data;

@Data
public class VersionVo implements Serializable {
    /**
     * 是否有新版本
     */
    private boolean hasNew;

    /**
     * 是否展示提示信息
     */
    private Boolean show;

    /**
     * 上个版本
     */
    private VersionEntity preVersion;

    /**
     * 当前版本
     */
    private VersionEntity curVersion;
}
