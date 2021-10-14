package io.shulie.takin.web.entrypoint.controller.confcenter;

import com.pamirs.takin.entity.domain.vo.application.UpdateAppNodeNumVO;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 对外暴露的接口控制器
 * @Author ocean_wll
 * @Date 2021/10/14 10:39 上午
 */
@RestController
@RequestMapping(APIUrls.PUBLIC_TAKIN_API_URL)
@Api(tags = "接口: 应用管理中心（外部调用）", value = "应用管理中心（外部调用）")
public class PublicApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PutMapping("/console/application/center/app/nodeNum")
    @ApiOperation("修改应用节点数")
    public void modifyAppNodeNum(@Validated @RequestBody UpdateAppNodeNumVO vo) {
        applicationService.modifyAppNodeNum(vo.getData());
    }
}
