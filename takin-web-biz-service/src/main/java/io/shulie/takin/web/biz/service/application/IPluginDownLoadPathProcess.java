package io.shulie.takin.web.biz.service.application;

import io.shulie.takin.web.common.enums.application.ApplicationAgentPathTypeEnum;
import io.shulie.takin.web.data.model.mysql.ApplicationPluginDownloadPathEntity;

/**
 * @Author: 南风
 * @Date: 2021/11/19 5:07 下午
 * 插件上传地址处理
 */
public interface IPluginDownLoadPathProcess {

    <T extends ApplicationPluginDownloadPathEntity> T encrypt(T param);

    ApplicationAgentPathTypeEnum getType();

}
