package io.shulie.takin.web.entrypoint.controller.shift;

import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageQueryVO;
import com.pamirs.takin.entity.domain.vo.shift.BaseResult;
import com.pamirs.takin.entity.domain.vo.shift.SceneManagerResult;
import com.pamirs.takin.entity.domain.vo.shift.ShiftCloudVO;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.input.scenemanage.SceneManageListOutput;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.swagger.annotations.Api;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/c/self-service-api/{tool-code}/task_api")
@Api(tags = "移动云接口", value = "移动云接口")
@RestController
public class ShiftCloudController {

    @Autowired
    private SceneManageService sceneManageService;

    //2.2
    @PostMapping("/task_list")
    public BaseResult getSceneManagers(@RequestBody ShiftCloudVO shiftCloudVO) {
        SceneManageQueryVO queryVO = new SceneManageQueryVO();
        queryVO.setPageNumber(shiftCloudVO.getPage_index());
        queryVO.setPageSize(shiftCloudVO.getPage_size());
        queryVO.setUserName(shiftCloudVO.getAccount());
        if (StringUtils.isNoneBlank(shiftCloudVO.getTask_name())) {
            queryVO.setSceneName(shiftCloudVO.getTask_name());
        }
        queryVO.setIsDeleted(0);
        ResponseResult<List<SceneManageListOutput>> responseResult = sceneManageService.getPageList(queryVO);
        Map<String, List> map = new HashMap<>(1);
        BaseResult result = new BaseResult<>();
        List<SceneManagerResult> list = new ArrayList<>();
        map.put("task_list", list);
        if (null != responseResult && CollectionUtils.isNotEmpty(responseResult.getData())) {
            responseResult.getData().forEach(r -> list.add(new SceneManagerResult(r.getId(), r.getSceneName(), r.getUserId(), r.getUserName())));
            result.setData(map);
        }
        return result;
    }
}
