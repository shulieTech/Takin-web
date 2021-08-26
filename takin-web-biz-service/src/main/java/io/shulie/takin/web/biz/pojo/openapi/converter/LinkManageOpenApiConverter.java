package io.shulie.takin.web.biz.pojo.openapi.converter;

import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessActiveViewListDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessFlowDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.SceneDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.SystemProcessViewListDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.TechLinkDto;
import io.shulie.takin.web.biz.pojo.openapi.response.linkmanage.BusinessActiveViewListOpenApiResp;
import io.shulie.takin.web.biz.pojo.openapi.response.linkmanage.BusinessFlowOpenApiResp;
import io.shulie.takin.web.biz.pojo.openapi.response.linkmanage.BusinessLinkOpenApiResp;
import io.shulie.takin.web.biz.pojo.openapi.response.linkmanage.SceneOpenApiResp;
import io.shulie.takin.web.biz.pojo.openapi.response.linkmanage.SystemProcessViewListOpenApiResp;
import io.shulie.takin.web.biz.pojo.openapi.response.linkmanage.TechLinkOpenApiResp;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessLinkResponse;
import org.apache.commons.lang.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhaoyong
 */
@Mapper(imports = {StringUtils.class})
public interface LinkManageOpenApiConverter {
    LinkManageOpenApiConverter INSTANCE = Mappers.getMapper(LinkManageOpenApiConverter.class);

    List<SystemProcessViewListOpenApiResp> ofListSystemProcessViewListDto(List<SystemProcessViewListDto> data);

    List<BusinessActiveViewListOpenApiResp> ofListBusinessActiveViewListOpenApiResp(List<BusinessActiveViewListDto> bussisnessLinks);

    BusinessLinkOpenApiResp ofBusinessLinkOpenApiResp(BusinessLinkResponse businessLinkResponse);

    List<SceneOpenApiResp> ofListSceneOpenApiResp(List<SceneDto> data);

    BusinessFlowOpenApiResp ofBusinessFlowOpenApiResp(BusinessFlowDto dto);

    TechLinkOpenApiResp ofTechLinkOpenApiResp(TechLinkDto dto);
}
