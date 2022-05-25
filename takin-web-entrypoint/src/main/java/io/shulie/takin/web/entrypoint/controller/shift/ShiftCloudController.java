package io.shulie.takin.web.entrypoint.controller.shift;

import com.alibaba.fastjson.JSON;
import com.pamirs.takin.entity.domain.vo.report.SceneActionParam;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageQueryVO;
import com.pamirs.takin.entity.domain.vo.shift.BaseResult;
import com.pamirs.takin.entity.domain.vo.shift.SceneManagerResult;
import com.pamirs.takin.entity.domain.vo.shift.ShiftCloudVO;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneActionResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.input.scenemanage.SceneManageListOutput;
import io.shulie.takin.web.biz.service.TagService;
import io.shulie.takin.web.biz.service.UserService;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.entrypoint.controller.scenemanage.SceneTaskController;
import io.swagger.annotations.Api;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/api/c/self-service-api/tool-code/task_api")
@Api(tags = "移动云接口", value = "移动云接口")
@RestController
public class ShiftCloudController {

    @Autowired
    private SceneManageService sceneManageService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    @Autowired
    private SceneTaskController controller;

    private static final Map<Integer, String> TASK_CACHE = new ConcurrentHashMap();

    //2.1
    @Deprecated
    public void getProjects() {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://域名/ms/testcloudplatform/api/service/project/list");
        CloseableHttpResponse response = null;
        try {
            Map data = new HashMap();
            data.put("token","");
            StringEntity stringEntity = new StringEntity(JSON.toJSONString(data), "UTF-8");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                String result = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            //Ignore
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                //Ignore
            }
        }
    }

    //2.2
    @PostMapping("/task_list")
    public BaseResult getSceneManagers(@RequestBody ShiftCloudVO shiftCloudVO) {
        BaseResult result = new BaseResult<>();
        try {
            SceneManageQueryVO queryVO = new SceneManageQueryVO();
            queryVO.setPageNumber(shiftCloudVO.getPage_index());
            queryVO.setPageSize(shiftCloudVO.getPage_size());
            if (StringUtils.isNoneBlank(shiftCloudVO.getTask_name())) {
                queryVO.setSceneName(shiftCloudVO.getTask_name());
            }
            if (StringUtils.isNotBlank(shiftCloudVO.getProject_id())) {
                Long id = tagService.getIdByName(shiftCloudVO.getProject_id());
                if (null != id) {
                    queryVO.setTagId(id);
                }
            }
            queryVO.setIsDeleted(0);
            String userName = shiftCloudVO.getAccount();
            if (StringUtils.isNotBlank(userName)) {
                Long id = userService.getIdByName(userName);
                if (null != id) {
                    queryVO.setFilterSql("(" + id + ")");
                }
            }
            ResponseResult<List<SceneManageListOutput>> responseResult = sceneManageService.getPageList(queryVO);
            Map<String, List> map = new HashMap<>(1);
            List<SceneManagerResult> list = new ArrayList<>();
            map.put("task_list", list);
            if (null != responseResult && CollectionUtils.isNotEmpty(responseResult.getData())) {
                responseResult.getData().forEach(r -> list.add(new SceneManagerResult(r.getId(), r.getSceneName(), r.getUserId(), r.getUserName())));
                result.setData(map);
            }
            return result;
        } catch (Exception e) {
            result.fail(e.getMessage());
            return result;
        }
    }

    //2.3
    @PostMapping("/relate_task")
    public BaseResult relateTask(@RequestBody ShiftCloudVO shiftCloudVO) {
        BaseResult baseResult = new BaseResult();
        TASK_CACHE.put(shiftCloudVO.getTask_id(), shiftCloudVO.getTool_task_id());
        return baseResult;
    }

    //2.4
    @PostMapping("/execute_task")
    public BaseResult executeTask(@RequestBody ShiftCloudVO shiftCloudVO) {
        BaseResult baseResult = new BaseResult();
        try {
            String taskId = shiftCloudVO.getTool_task_id();
            SceneActionParam param = new SceneActionParam();
            param.setSceneId(Long.parseLong(taskId));
            param.setLeakSqlEnable(false);
            param.setContinueRead("0");
            WebResponse<SceneActionResp> response = controller.start(param);
            if (null != response && response.getSuccess()) {
                SceneActionResp sceneActionResp = response.getData();
                if (null != sceneActionResp) {
                    Long toolExecuteId = sceneActionResp.getData();
                    if (null != toolExecuteId) {
                        Map data = new HashMap(1);
                        data.put("tool_execute_id", toolExecuteId);
                        baseResult.setData(data);
                    }
                }
            }
            return baseResult;
        } catch (Exception e) {
            baseResult.fail(e.getMessage());
            return baseResult;
        }
    }
}
