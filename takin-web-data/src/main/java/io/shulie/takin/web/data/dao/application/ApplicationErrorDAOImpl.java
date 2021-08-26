package io.shulie.takin.web.data.dao.application;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationErrorQueryDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationErrorDTO;
import io.shulie.takin.web.data.param.application.ApplicationErrorQueryParam;
import io.shulie.takin.web.data.result.application.ApplicationErrorResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fanxx
 * @date 2020/10/16 11:11 上午
 */
@Service
public class ApplicationErrorDAOImpl implements ApplicationErrorDAO {

    @Autowired
    private ApplicationClient applicationClient;

    @Override
    public List<ApplicationErrorResult> selectErrorList(ApplicationErrorQueryParam param) {
        List<ApplicationErrorResult> applicationErrorResultList = Lists.newArrayList();
        ApplicationErrorQueryDTO queryDTO = new ApplicationErrorQueryDTO();
        queryDTO.setAppName(param.getApplicationName());
        List<ApplicationErrorDTO> applicationErrorDTOList = applicationClient.listErrors(queryDTO);
        if (CollectionUtils.isEmpty(applicationErrorDTOList)) {
            return Collections.emptyList();
        }
        applicationErrorResultList = applicationErrorDTOList.stream().map(applicationErrorDTO -> {
            ApplicationErrorResult applicationErrorResult = new ApplicationErrorResult();
            applicationErrorResult.setExceptionId(applicationErrorDTO.getId());
            applicationErrorResult.setAgentId(applicationErrorDTO.getAgentIds());
            applicationErrorResult.setDescription(applicationErrorDTO.getDescription());
            applicationErrorResult.setCreateTime(applicationErrorDTO.getTime());
            return applicationErrorResult;
        }).collect(Collectors.toList());
        return applicationErrorResultList;
    }
}
