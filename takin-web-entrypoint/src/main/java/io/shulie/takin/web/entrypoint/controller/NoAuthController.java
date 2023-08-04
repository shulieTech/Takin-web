package io.shulie.takin.web.entrypoint.controller;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.web.biz.pojo.request.pts.IdRequest;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.common.common.Separator;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 无需权限的访问
 *
 * @author qianshui
 * @date 2020/11/23 下午6:18
 */
@RestController
@RequestMapping("/api/noauth")
@Slf4j
public class NoAuthController {

    @Autowired
    private RedisClientUtil redisClientUtil;

    @Resource
    private SceneManageDAO sceneManageDAO;

    @Resource
    private ReportDao reportDao;

    @PutMapping("/resume/scenetask")
    public ResponseResult resumeSceneTask(@RequestBody Map<String, Object> paramMap) {
        Long reportId = Long.parseLong(String.valueOf(paramMap.get("reportId")));
        if (reportId == null) {
            ResponseResult.fail("reportId cannot be null,", "");
        }
        redisClientUtil.del(WebRedisKeyConstant.REPORT_WARN_PREFIX + reportId);
        String redisKey = CommonUtil.generateRedisKeyWithSeparator(Separator.Separator3, WebPluginUtils.traceTenantAppKey(), WebPluginUtils.traceEnvCode(),
            String.format(WebRedisKeyConstant.PTING_APPLICATION_KEY, reportId));
        redisClientUtil.del(redisKey);
        return ResponseResult.success("resume success");
    }

    @PostMapping("/reset/scene/report/status")
    public ResponseResult resetSceneReportStatus(@RequestBody IdRequest idRequest) {
        SceneManageEntity sceneManageEntity = sceneManageDAO.getSceneById(idRequest.getId());
        if(sceneManageEntity == null) {
            return ResponseResult.fail("未找到压测场景id=" + idRequest.getId(), null);
        }
        LambdaUpdateWrapper<SceneManageEntity> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(SceneManageEntity::getStatus, 0);
        updateWrapper.eq(SceneManageEntity::getId, idRequest.getId());
        sceneManageDAO.update(updateWrapper);
        log.info("重置压测场景id={}状态成功....", idRequest.getId());
        reportDao.updateStatus(idRequest.getId(), 2);
        log.info("重置报告场景id={}状态成功....", idRequest.getId());
        String key = String.format("LOCK:pressure:scene:%s:locking", idRequest.getId());
        redisClientUtil.delete(key);
        log.info("重置压测场景id={}缓存数据成功....", idRequest.getId());
        return ResponseResult.success("重置成功");
    }
}
