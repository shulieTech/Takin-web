package io.shulie.takin.web.data.dao.cloud.resouces.impl;

import io.shulie.takin.adapter.api.model.response.cloud.resources.Resource;
import io.shulie.takin.web.data.dao.cloud.resouces.CloudResourcesDao;
import io.shulie.takin.web.data.mapper.mysql.CloudResourcesMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class CloudResourcesDaoImpl implements CloudResourcesDao {

    @javax.annotation.Resource
    private CloudResourcesMapper cloudResourcesMapper;

    @Override
    public void getResourceStatus(Resource resource) {
        Map statusAndErrorMessage = cloudResourcesMapper.getResourceStatus(resource.getResourceId());
        //TODO convert status and error message
        if (null != statusAndErrorMessage) {
            Object status = statusAndErrorMessage.get("status");
            if (null != status) {
                resource.setStatus(Integer.valueOf(status.toString()));
            }
            Object errorMessage = statusAndErrorMessage.get("exception_msg");
            if (null != errorMessage) {
                resource.setErrorMessage(errorMessage.toString());
            }
        }
    }
}
