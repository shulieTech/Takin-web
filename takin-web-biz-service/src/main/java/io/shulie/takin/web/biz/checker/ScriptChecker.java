package io.shulie.takin.web.biz.checker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import com.pamirs.takin.entity.domain.dto.scenemanage.SceneManageWrapperDTO;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneScriptRefOutput;
import io.shulie.takin.cloud.biz.service.schedule.impl.FileSplitService;
import io.shulie.takin.cloud.biz.utils.FileTypeBusinessUtil;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.utils.security.MD5Utils;
import io.shulie.takin.web.biz.service.scenemanage.SceneTaskService;
import io.shulie.takin.web.common.util.RedisClientUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.shulie.takin.cloud.data.util.PressureStartCache.FILE_DOWNLOAD_MESSAGE;
import static io.shulie.takin.cloud.data.util.PressureStartCache.FILE_DOWNLOAD_STATUS;
import static io.shulie.takin.cloud.data.util.PressureStartCache.FILE_VERIFY_MESSAGE;
import static io.shulie.takin.cloud.data.util.PressureStartCache.FILE_VERIFY_STATUS;
import static io.shulie.takin.cloud.data.util.PressureStartCache.getPressureFileKey;
import static io.shulie.takin.cloud.data.util.PressureStartCache.getResourceKey;

@Slf4j
@Component
public class ScriptChecker implements StartConditionChecker {

    @Resource
    private SceneTaskService sceneTaskService;

    private static final String SCRIPT_NAME_SUFFIX = "jmx";

    @Resource
    private RedisClientUtil redisClientUtil;

    @Value("${script.path}/")
    private String pathPrefix;

    @Override
    public CheckResult check(StartConditionCheckerContext context) {
        try {
            if (StringUtils.isBlank(context.getResourceId())) {
                doCheck(context);
                return CheckResult.pending(type());
            }
            return fetchFileStatus(context);
        } catch (Exception e) {
            return CheckResult.fail(type(), e.getMessage());
        }
    }

    // 检测文件状态：下载状态及校验状态
    public CheckResult fetchFileStatus(StartConditionCheckerContext context) {
        String attachId = context.getAttachId();
        if (StringUtils.isBlank(attachId)) {
            throw new RuntimeException("压测文件下载异常");
        }
        // 检测文件下载状态
        String pressureFileKey = getPressureFileKey(attachId);
        Integer downloadStatus = (Integer)redisClientUtil.hmget(pressureFileKey, FILE_DOWNLOAD_STATUS);
        String resourceKey = getResourceKey(context.getResourceId());
        redisClientUtil.hmset(resourceKey, FILE_DOWNLOAD_STATUS, downloadStatus);
        if (Objects.equals(downloadStatus, CheckStatus.PENDING.ordinal())) {
            return CheckResult.pending(type());
        }
        if (Objects.equals(downloadStatus, CheckStatus.FAIL.ordinal())) {
            return CheckResult.fail(type(), (String)redisClientUtil.hmget(pressureFileKey, FILE_DOWNLOAD_MESSAGE));
        }
        // 检测文件校验状态
        Integer verifyStatus = (Integer)redisClientUtil.hmget(pressureFileKey, FILE_VERIFY_STATUS);
        redisClientUtil.hmset(resourceKey, FILE_VERIFY_STATUS, verifyStatus);
        if (Objects.equals(verifyStatus, CheckStatus.PENDING.ordinal())) {
            return CheckResult.pending(type());
        }
        if (Objects.equals(verifyStatus, CheckStatus.FAIL.ordinal())) {
            return CheckResult.fail(type(), (String)redisClientUtil.hmget(pressureFileKey, FILE_VERIFY_MESSAGE));
        }
        return CheckResult.success(type());
    }

    private void doCheck(StartConditionCheckerContext context) {
        SceneManageWrapperOutput sceneData = context.getSceneData();
        // 压测文件完整性检测
        checkScriptComplete(sceneData);
        //检测脚本文件是否有变更
        checkModify(sceneData);
        // 校验是否与场景同步了
        checkSync(sceneData);
        // 压测脚本文件检查
        SceneManageWrapperDTO sceneDataDTO = context.getSceneDataDTO();
        sceneDataDTO.getWatchmanIdList().add(String.valueOf(context.getMachineId()));
        String scriptCorrelation = sceneTaskService.checkScriptCorrelation(sceneDataDTO);
        if (StringUtils.isNotBlank(scriptCorrelation)) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_JMX_FILE_CHECK_ERROR, scriptCorrelation);
        }
    }

    private void checkScriptComplete(SceneManageWrapperOutput sceneData) {
        List<FileInfo> fileInfos = sceneData.getUploadFile().stream()
            .filter(file -> FileTypeBusinessUtil.isScriptOrData(file.getFileType())
                || FileTypeBusinessUtil.isAttachment(file.getFileType()))
            .map(this::getFilePath).collect(Collectors.toList());
        checkExists(fileInfos);
    }

    private FileInfo getFilePath(SceneScriptRefOutput file) {
        FileSplitService.reWriteAttachmentSceneScriptRefOutput(file);
        return new FileInfo(file.getFileType(), pathPrefix + file.getUploadPath());
    }

    private void checkExists(List<FileInfo> fileInfos) {
        List<String> errorMessage = new ArrayList<>();
        fileInfos.forEach(file -> {
            if (!new File(file.getPath()).exists()) {
                String message = buildNoExistsMessage(file);
                if (message != null) {
                    errorMessage.add(message);
                }
            }
        });
        if (!CollectionUtils.isEmpty(errorMessage)) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_JMX_FILE_CHECK_ERROR,
                StringUtils.join(errorMessage, ", "));
        }
    }

    private String buildNoExistsMessage(FileInfo fileInfo) {
        if (FileTypeBusinessUtil.isScript(fileInfo.getType())) {
            return "jmx脚本文件[" + fileInfo.getPath() + "]不存在";
        } else if (FileTypeBusinessUtil.isData(fileInfo.getType())) {
            return "数据文件[" + fileInfo.getPath() + "]不存在";
        } else if (FileTypeBusinessUtil.isAttachment(fileInfo.getType())) {
            return "附件文件[" + fileInfo.getPath() + "]不存在";
        }
        return null;
    }

    private void checkSync(SceneManageWrapperOutput sceneData) {
        String disabledKey = "DISABLED";
        String featureString = sceneData.getFeatures();
        Map<String, Object> feature = JSONObject.parseObject(featureString,
            new TypeReference<Map<String, Object>>() {
            });
        if (feature.containsKey(disabledKey)) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_START_VERIFY_ERROR,
                "场景【" + sceneData.getId() + "】对应的业务流程发生变更，未能自动匹配，请手动编辑后启动压测");
        }
    }

    private void checkModify(SceneManageWrapperOutput sceneData) {
        SceneScriptRefOutput scriptRefOutput = sceneData.getUploadFile().stream().filter(Objects::nonNull)
            .filter(fileRef -> fileRef.getFileType() == 0 && fileRef.getFileName().endsWith(SCRIPT_NAME_SUFFIX))
            .findFirst()
            .orElse(null);
        boolean jmxCheckResult = checkOutJmx(scriptRefOutput);
        if (!jmxCheckResult) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_JMX_FILE_CHECK_ERROR,
                "启动压测场景--场景ID:" + sceneData.getId() + ",脚本文件校验失败！");
        }
    }

    private boolean checkOutJmx(SceneScriptRefOutput uploadFile) {
        if (Objects.nonNull(uploadFile) && StringUtils.isNotBlank(uploadFile.getUploadPath())) {
            String fileMd5 = MD5Utils.getInstance().getMD5(new File(pathPrefix, uploadFile.getUploadPath()));
            return StringUtils.isBlank(uploadFile.getFileMd5()) || uploadFile.getFileMd5().equals(fileMd5);
        }
        return false;
    }

    @Override
    public String type() {
        return "file";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Data
    @AllArgsConstructor
    public static class FileInfo {
        /**
         * 类型：
         * 1-脚本
         * 2-csv
         * 3-插件jar
         */
        private Integer type;
        private String path;
    }
}
