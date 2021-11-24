package io.shulie.takin.web.entrypoint.controller.agentupgradeonline;

import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.biz.service.agentupgradeonline.ApplicationTagRefService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 应用标签表(ApplicationTagRef)controller
 *
 * @author ocean_wll
 * @date 2021-11-09 20:09:43
 */
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL + "application/tag")
@Api(tags = "接口：应用标签管理")
public class ApplicationTagRefController {

    @Autowired
    private ApplicationTagRefService tagRefService;

    @ApiOperation("获取标签列表数据")
    @GetMapping("/list")
    public List<SelectVO> list() {
       return tagRefService.getListByTenant();
    }

}
