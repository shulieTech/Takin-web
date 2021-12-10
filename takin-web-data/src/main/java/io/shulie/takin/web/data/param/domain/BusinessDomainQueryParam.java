package io.shulie.takin.web.data.param.domain;

import java.util.List;

import com.pamirs.takin.entity.domain.PagingDevice;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import lombok.Data;

/**
 * @Author: fanxx
 * @Date: 2021/12/7 4:28 下午
 * @Description:
 */
@Data
public class BusinessDomainQueryParam extends PageBaseDTO {

    private List<Long> ids;
    private String name;
    private List<Integer> domainCodes;
}
