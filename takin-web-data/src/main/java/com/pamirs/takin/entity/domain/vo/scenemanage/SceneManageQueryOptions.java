package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.io.Serializable;

import lombok.Data;

/**
* @author qianshui
 * @date 2020/4/18 上午11:13
 */
@Data
public class SceneManageQueryOptions implements Serializable {

    private static final long serialVersionUID = 5366646945677963740L;

    /**
     * 业务活动
     */
    private Boolean includeBusinessActivity = false;

    /**
     * 脚本文件
     */
    private Boolean includeScript = false;

    /**
     * SLA配置
     */
    private Boolean includeSLA = false;
}
