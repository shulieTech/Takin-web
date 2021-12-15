package io.shulie.takin.web.biz.service.domain;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.domain.BusinessDomainCreateRequest;
import io.shulie.takin.web.biz.pojo.request.domain.BusinessDomainDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.domain.BusinessDomainQueryRequest;
import io.shulie.takin.web.biz.pojo.request.domain.BusinessDomainUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.domain.BusinessDomainListResponse;

/**
 * 业务域表(BusinessDomain)service
 *
 * @author liuchuan
 * @date 2021-12-07 16:06:56
 */
public interface BusinessDomainService {
    void create(BusinessDomainCreateRequest createRequest);

    void update(BusinessDomainUpdateRequest updateRequest);

    void delete(BusinessDomainDeleteRequest deleteRequest);

    void deleteById(Long id);

    PagingList<BusinessDomainListResponse> list(BusinessDomainQueryRequest queryRequest);

    List<BusinessDomainListResponse> listNoPage();
}
