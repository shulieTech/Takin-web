package io.shulie.takin.web.biz.pojo.openapi.converter;

import java.util.List;

import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import io.shulie.takin.cloud.sdk.model.response.report.ActivityResponse;
import io.shulie.takin.web.biz.pojo.openapi.request.report.ReportQueryOpenApiReq;
import io.shulie.takin.web.biz.pojo.openapi.response.report.BusinessActivityOpenApiResp;
import org.apache.commons.lang.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhaoyong
 */
@Mapper(imports = {StringUtils.class})
public interface ReportOpenApiConverter {

    ReportOpenApiConverter INSTANCE = Mappers.getMapper(ReportOpenApiConverter.class);

    ReportQueryParam ofReportQueryOpenApiReq(ReportQueryOpenApiReq reportQueryOpenApiReq);

    List<BusinessActivityOpenApiResp> ofLsitBusinessActivityOpenApiResp(List<ActivityResponse> data);
}
