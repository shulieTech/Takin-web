package io.shulie.takin.web.biz.service.linkmanage;

import java.util.List;

import io.shulie.takin.web.biz.pojo.request.application.InterfaceTypeChildCreateRequest;
import io.shulie.takin.web.biz.pojo.request.application.InterfaceTypeConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.request.application.InterfaceTypeMainCreateRequest;
import io.shulie.takin.web.biz.pojo.request.application.RemoteCallConfigCreateRequest;
import io.shulie.takin.web.data.model.mysql.InterfaceTypeChildEntity;
import io.shulie.takin.web.data.model.mysql.InterfaceTypeConfigEntity;
import io.shulie.takin.web.data.model.mysql.InterfaceTypeMainEntity;
import io.shulie.takin.web.data.model.mysql.RemoteCallConfigEntity;

public interface ApplicationRemoteInterfaceConfigService {

    /**
     * 添加中间件大类型
     */
    void addInterfaceTypeMain(InterfaceTypeMainCreateRequest request);

    /**
     * 添加中间件小类型
     */
    void addInterfaceTypeChild(InterfaceTypeChildCreateRequest request);

    /**
     * 添加中间件配置类型
     */
    void addRemoteCallConfig(RemoteCallConfigCreateRequest request);

    /**
     * 添加中间件小类型-配置类型关系
     */
    void addInterfaceTypeConfig(InterfaceTypeConfigCreateRequest request);

    List<InterfaceTypeMainEntity> queryInterfaceTypeMain();

    List<InterfaceTypeChildEntity> queryInterfaceTypeChild();

    List<InterfaceTypeConfigEntity> queryInterfaceTypeConfig();

    List<RemoteCallConfigEntity> queryRemoteCallConfig();

    void deleteInterfaceTypeConfig(Long id);
}
