package io.shulie.takin.web.data.param.pressureresource;

import lombok.Data;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 2:56 下午
 */
@Data
public class PressureResourceDetailQueryParam {
    private Long resourceId;

    private List<Long> resourceIds;

    private String linkId;
}
