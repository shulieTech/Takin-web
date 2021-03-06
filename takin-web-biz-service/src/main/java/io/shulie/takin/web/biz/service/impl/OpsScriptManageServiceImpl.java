package io.shulie.takin.web.biz.service.impl;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.utils.file.FileManagerHelper;
import io.shulie.takin.utils.linux.LinuxHelper;
import io.shulie.takin.utils.string.StringUtil;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.OpsScriptFileService;
import io.shulie.takin.web.biz.service.OpsScriptManageService;
import io.shulie.takin.web.biz.utils.CopyUtils;
import io.shulie.takin.web.biz.utils.FileUtils;
import io.shulie.takin.web.biz.utils.PageUtils;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.constant.LockKeyConstants;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.enums.opsscript.OpsScriptEnum;
import io.shulie.takin.web.common.enums.opsscript.OpsScriptExecutionEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.opsscript.OpsScriptBatchNoDAO;
import io.shulie.takin.web.data.dao.opsscript.OpsScriptExecuteResultDAO;
import io.shulie.takin.web.data.dao.opsscript.OpsScriptFileDAO;
import io.shulie.takin.web.data.dao.opsscript.OpsScriptManageDAO;
import io.shulie.takin.web.data.model.mysql.OpsScriptBatchNoEntity;
import io.shulie.takin.web.data.model.mysql.OpsScriptExecuteResultEntity;
import io.shulie.takin.web.data.model.mysql.OpsScriptFileEntity;
import io.shulie.takin.web.data.model.mysql.OpsScriptManageEntity;
import io.shulie.takin.web.data.param.opsscript.OpsScriptFileParam;
import io.shulie.takin.web.data.param.opsscript.OpsScriptParam;
import io.shulie.takin.web.data.result.opsscript.OpsExecutionVO;
import io.shulie.takin.web.data.result.opsscript.OpsScriptDetailVO;
import io.shulie.takin.web.data.result.opsscript.OpsScriptFileVO;
import io.shulie.takin.web.data.result.opsscript.OpsScriptVO;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * ??????????????????(OpsScriptManage)??????????????????
 *
 * @author caijy
 * @since 2021-06-16 10:41:42
 */
@Service
@Slf4j
public class OpsScriptManageServiceImpl implements OpsScriptManageService {

    /**
     * ?????????????????????
     */
    @Value("${takin.data.path}")
    private String uploadPath;

    private ConcurrentHashMap<String, OpsExecutionVO> opsScriptLogCache = new ConcurrentHashMap();

    @Autowired
    OpsScriptManageDAO opsScriptManageDAO;

    @Autowired
    OpsScriptExecuteResultDAO opsScriptExecuteResultDAO;

    @Autowired
    OpsScriptFileDAO opsScriptFileDAO;

    @Autowired
    OpsScriptBatchNoDAO opsScriptBatchNoDAO;

    @Autowired
    @Qualifier("opsScriptThreadPool")
    ThreadPoolExecutor opsScriptThreadPool;

    @Autowired
    private DistributedLock distributedLock;

    private String deployUser;

    @Autowired
    private OpsScriptFileService opsScriptFileService;

    @PostConstruct
    public void init() {
        boolean deployUserEnable = ConfigServerHelper.getBooleanValueByKey(
            ConfigServerKeyEnum.TAKIN_FILE_OPS_SCRIPT_DEPLOY_USER_ENABLE);

        // ??????????????????????????????????????????
        deployUser = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_OPS_SCRIPT_DEPLOY_USER);
        if (deployUserEnable) {
            io.shulie.takin.web.biz.utils.LinuxHelper.executeLinuxCmdNotThrow("useradd " + deployUser);
        }
    }

    @Override
    public PagingList<OpsScriptVO> page(OpsScriptParam param) {
        PageUtils.clearPageHelper();
        List<Long> userIdList = WebPluginUtils.getQueryAllowUserIdList();
        LambdaQueryWrapper<OpsScriptManageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(!CollectionUtils.isEmpty(userIdList), OpsScriptManageEntity::getUserId, userIdList);
        wrapper.orderByDesc(OpsScriptManageEntity::getGmtCreate);
        IPage<OpsScriptManageEntity> listPage = opsScriptManageDAO.findListPage(param, wrapper);
        List<OpsScriptVO> opsScriptVOList = Lists.newArrayList();

        if (Objects.nonNull(listPage) && !CollectionUtils.isEmpty(listPage.getRecords())) {
            List<OpsScriptManageEntity> records = listPage.getRecords();
            List<Long> scriptIds = records.stream().map(OpsScriptManageEntity::getId).collect(Collectors.toList());
            LambdaQueryWrapper<OpsScriptExecuteResultEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(OpsScriptExecuteResultEntity::getOpsScriptId, scriptIds);
            queryWrapper.eq(OpsScriptExecuteResultEntity::getIsDeleted, 0);
            List<OpsScriptExecuteResultEntity> resultList = opsScriptExecuteResultDAO.list(queryWrapper);
            if (CollectionUtils.isEmpty(resultList)) {
                resultList = Lists.newArrayList();
            }
            List<Long> userIds = records.stream().map(OpsScriptManageEntity::getUserId).distinct().collect(
                Collectors.toList());
            Map<Long, UserExt> userMap = WebPluginUtils.getUserMapByIds(userIds);

            Map<String, String> timeMap = resultList.stream().collect(
                Collectors.toMap(t -> t.getOpsScriptId() + "",
                    t -> DateUtil.format(t.getExecuteTime(), DatePattern.NORM_DATETIME_PATTERN)));
            for (OpsScriptManageEntity record : records) {
                OpsScriptVO vo = new OpsScriptVO();
                vo.setName(record.getName());
                vo.setId(record.getId() + "");
                vo.setScriptType(record.getScriptType());
                vo.setSciptTypeName(OpsScriptEnum.getNameByType(record.getScriptType()));
                vo.setStatus(record.getStatus());
                vo.setStatusName(OpsScriptExecutionEnum.getNameByStatus(record.getStatus()));
                vo.setLastExecuteTime(timeMap.get(record.getId() + ""));
                vo.setLastModefyTime(DateUtil.format(record.getGmtUpdate(), DatePattern.NORM_DATETIME_PATTERN));
                vo.setUserName(WebPluginUtils.getUserName(record.getUserId(), userMap));
                vo.setUserId(record.getUserId());
                WebPluginUtils.fillQueryResponse(vo);
                opsScriptVOList.add(vo);
            }
        }

        return PagingList.of(opsScriptVOList, listPage.getTotal());
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Boolean add(OpsScriptParam param) {
        if (Objects.isNull(param)) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "????????????");
        }
        if (Objects.isNull(param.getName())) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "???????????????????????????");
        }
        //????????????
        OpsScriptManageEntity one = opsScriptManageDAO.lambdaQuery()
            .eq(OpsScriptManageEntity::getName, param.getName())
            .eq(OpsScriptManageEntity::getIsDeleted, 0)
            .one();
        if (!Objects.isNull(one)) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "????????????????????????");
        }
        if (Objects.isNull(param.getScriptType())) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "???????????????????????????");
        }

        if (CollectionUtils.isEmpty(param.getFileManageUpdateRequests())) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "?????????????????????");
        }

        List<OpsScriptFileParam> fileParamList = param.getFileManageUpdateRequests();
        if (CollectionUtils.isEmpty(fileParamList)) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "???????????????????????????");
        }

        if (param.getName().length() > 20) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "????????????????????????20???");
        }
        String tempPath = uploadPath + ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_OPS_SCRIPT_PATH);
        //??????????????????
        OpsScriptFileParam scriptFileParam = fileParamList.get(0);
        log.info("???????????????uploadId???{}", scriptFileParam.getUploadId());
        this.saveLocalFile(tempPath + scriptFileParam.getUploadId() + "/" + scriptFileParam.getFileName(),
            this.getContent(scriptFileParam));

        //????????????
        if (!CollectionUtils.isEmpty(param.getAttachmentManageUpdateRequests())) {
            param.getAttachmentManageUpdateRequests().forEach(t -> {
                t.setUploadId(scriptFileParam.getUploadId());
                String targetPath = tempPath + scriptFileParam.getUploadId() + "/" + t.getFileName();
                this.saveLocalFile(targetPath, this.getContent(t));
                t.setFilePath(targetPath);
                fileParamList.add(t);
            });
        }

        OpsScriptManageEntity manageEntity = new OpsScriptManageEntity();
        manageEntity.setScriptType(param.getScriptType());
        manageEntity.setName(param.getName());

        opsScriptManageDAO.save(manageEntity);
        //??????????????????
        OperationLogContextHolder.addVars(BizOpConstants.Vars.OPS_SCRIPT_ID, manageEntity.getId() + "");
        OperationLogContextHolder.addVars(BizOpConstants.Vars.OPS_SCRIPT_NAME, manageEntity.getName());

        List<OpsScriptFileEntity> entityList = CopyUtils.copyFieldsList(fileParamList, OpsScriptFileEntity.class);
        entityList.forEach(t -> t.setOpsScriptId(manageEntity.getId()));
        opsScriptFileDAO.saveBatch(entityList);

        return true;
    }

    private String getContent(OpsScriptFileParam param) {
        if (StringUtil.isNotBlank(param.getContent())) {
            return param.getContent();
        }
        return opsScriptFileService.viewFile(param.getFilePath());
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Boolean update(OpsScriptParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getId())) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "ID???????????????");
        }
        if (CollectionUtils.isEmpty(param.getFileManageUpdateRequests())) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "?????????????????????");
        }

        //????????????
        List<OpsScriptFileEntity> fileList = opsScriptFileDAO.lambdaQuery()
            .eq(OpsScriptFileEntity::getOpsScriptId, param.getId())
            .eq(OpsScriptFileEntity::getIsDeleted, 0)
            .list();
        if (CollectionUtils.isEmpty(fileList)) {
            fileList = Lists.newArrayList();
        }
        if (CollectionUtils.isEmpty(param.getAttachmentManageUpdateRequests())) {
            param.setAttachmentManageUpdateRequests(Lists.newArrayList());
        }

        //????????????
        opsScriptManageDAO.lambdaUpdate()
            .eq(OpsScriptManageEntity::getId, param.getId())
            .set(OpsScriptManageEntity::getName, param.getName())
            .set(OpsScriptManageEntity::getGmtUpdate, new Date())
            .set(OpsScriptManageEntity::getScriptType, param.getScriptType())
            .update();

        String tempPath = uploadPath + ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_OPS_SCRIPT_PATH);
        //???????????????
        OpsScriptFileParam scriptFileParam = param.getFileManageUpdateRequests().get(0);
        if (scriptFileParam.getContent() != null) {
            log.info("???????????????uploadId???{}", scriptFileParam.getUploadId());
            FileManagerHelper.deleteFiles(Lists.newArrayList(scriptFileParam.getFilePath()));
            //??????????????????
            this.saveLocalFile(tempPath + scriptFileParam.getUploadId() + "/" + scriptFileParam.getFileName(),
                this.getContent(scriptFileParam));
        }
        //????????????
        List<OpsScriptFileParam> attachmentManageUpdateRequests = param.getAttachmentManageUpdateRequests();
        List<String> pathsFromPage = attachmentManageUpdateRequests.stream().filter(
            t -> StringUtil.isNotBlank(t.getFilePath())).map(
            t -> t.getFilePath()).collect(Collectors.toList());
        //?????????????????????
        List<String> deletePath = fileList.stream().filter(
            t -> t.getFileType().equals(2) && !pathsFromPage.contains(t.getFilePath())).map(
            t -> t.getFilePath()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deletePath)) {
            FileManagerHelper.deleteFiles(deletePath);
        }
        //???????????????
        attachmentManageUpdateRequests.stream().filter(t -> StringUtil.isEmpty(t.getOpsScriptId())).forEach(t -> {
            t.setUploadId(scriptFileParam.getUploadId());
            String targetPath = tempPath + scriptFileParam.getUploadId() + "/" + t.getFileName();
            this.saveLocalFile(targetPath, this.getContent(t));
            t.setFilePath(targetPath);
        });

        //????????????file ????????????file
        opsScriptFileDAO.lambdaUpdate()
            .eq(OpsScriptFileEntity::getOpsScriptId, param.getId())
            .set(OpsScriptFileEntity::getIsDeleted, 1)
            .update();

        //????????????file
        List<OpsScriptFileEntity> entityList = Lists.newArrayList();
        param.getFileManageUpdateRequests().forEach(t -> {
            OpsScriptFileEntity fileEntity = new OpsScriptFileEntity();
            fileEntity.setOpsScriptId(Long.parseLong(param.getId()));
            fileEntity.setFileType(1);
            fileEntity.setFileName(t.getFileName());
            fileEntity.setFileSize(t.getFileSize());
            fileEntity.setFilePath(t.getFilePath());
            fileEntity.setUploadId(t.getUploadId());
            entityList.add(fileEntity);
        });
        //????????????file
        if (!CollectionUtils.isEmpty(attachmentManageUpdateRequests)) {
            attachmentManageUpdateRequests.forEach(t -> {
                OpsScriptFileEntity fileEntity = new OpsScriptFileEntity();
                fileEntity.setOpsScriptId(Long.parseLong(param.getId()));
                fileEntity.setFileType(2);
                fileEntity.setFileName(t.getFileName());
                fileEntity.setFileSize(t.getFileSize());
                fileEntity.setFilePath(t.getFilePath());
                fileEntity.setUploadId(t.getUploadId());
                entityList.add(fileEntity);
            });
        }
        opsScriptFileDAO.saveBatch(entityList);

        //??????????????????
        OperationLogContextHolder.addVars(BizOpConstants.Vars.OPS_SCRIPT_ID, param.getId() + "");
        OperationLogContextHolder.addVars(BizOpConstants.Vars.OPS_SCRIPT_NAME, param.getName());
        return true;
    }

    private void saveLocalFile(String filePath, String content) {
        FileManagerHelper.createFileByPathAndString(filePath, content);
        LinuxHelper.executeLinuxCmd("chmod 777" + filePath);
    }

    @Override
    public OpsScriptDetailVO detail(OpsScriptParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getId())) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "ID???????????????");
        }
        OpsScriptDetailVO detailVO = new OpsScriptDetailVO();
        OpsScriptManageEntity entity = opsScriptManageDAO.getById(param.getId());
        if (Objects.isNull(entity)) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "????????????????????????");
        }

        LambdaQueryWrapper<OpsScriptFileEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OpsScriptFileEntity::getOpsScriptId, param.getId());
        queryWrapper.eq(OpsScriptFileEntity::getIsDeleted, 0);
        List<OpsScriptFileEntity> list = opsScriptFileDAO.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            list = Lists.newArrayList();
        }
        List<OpsScriptFileVO> fileVOList = Lists.newArrayList();
        list.forEach(t -> {
            OpsScriptFileVO fileVO = new OpsScriptFileVO();
            fileVO.setId(t.getId() + "");
            fileVO.setFileSize(t.getFileSize());
            fileVO.setFileName(t.getFileName());
            fileVO.setFileType(t.getFileType());
            fileVO.setOpsScriptId(t.getOpsScriptId() + "");
            fileVO.setUploadId(t.getUploadId());
            fileVO.setDownloadUrl(t.getFilePath());
            fileVO.setUploadTime(t.getGmtCreate());
            fileVOList.add(fileVO);
        });
        detailVO.setFiles(fileVOList.stream().filter(t -> t.getFileType().equals(1)).collect(Collectors.toList()));
        detailVO.setAttachmentfiles(
            fileVOList.stream().filter(t -> t.getFileType().equals(2)).collect(Collectors.toList()));
        detailVO.setId(entity.getId() + "");
        detailVO.setName(entity.getName());
        detailVO.setScriptType(entity.getScriptType());
        detailVO.setSciptTypeName(OpsScriptEnum.getNameByType(entity.getScriptType()));
        return detailVO;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Boolean delete(OpsScriptParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getId())) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "ID???????????????");
        }
        LambdaUpdateWrapper<OpsScriptManageEntity> manageWrapper = new LambdaUpdateWrapper<>();
        manageWrapper.set(OpsScriptManageEntity::getIsDeleted, 1);
        manageWrapper.eq(OpsScriptManageEntity::getId, param.getId());
        opsScriptManageDAO.update(manageWrapper);

        LambdaUpdateWrapper<OpsScriptFileEntity> fileWrapper = new LambdaUpdateWrapper<>();
        fileWrapper.eq(OpsScriptFileEntity::getOpsScriptId, param.getId());
        fileWrapper.set(OpsScriptFileEntity::getIsDeleted, 1);
        opsScriptFileDAO.update(fileWrapper);

        LambdaUpdateWrapper<OpsScriptBatchNoEntity> batchNoWrapper = new LambdaUpdateWrapper<>();
        batchNoWrapper.eq(OpsScriptBatchNoEntity::getOpsScriptId, param.getId());
        batchNoWrapper.set(OpsScriptBatchNoEntity::getIsDeleted, 1);
        opsScriptBatchNoDAO.update(batchNoWrapper);

        LambdaUpdateWrapper<OpsScriptExecuteResultEntity> resultWrapper = new LambdaUpdateWrapper<>();
        resultWrapper.eq(OpsScriptExecuteResultEntity::getOpsScriptId, param.getId());
        resultWrapper.set(OpsScriptExecuteResultEntity::getIsDeleted, 1);
        opsScriptExecuteResultDAO.update(resultWrapper);

        //??????????????????
        OperationLogContextHolder.addVars(BizOpConstants.Vars.OPS_SCRIPT_ID, param.getId() + "");
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean execute(OpsScriptParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getId())) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "ID???????????????");
        }
        OpsScriptManageEntity manageEntity = opsScriptManageDAO.getById(param.getId());
        if (Objects.isNull(manageEntity)) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "???????????????????????????");
        }
        OpsScriptFileEntity one = opsScriptFileDAO.lambdaQuery()
            .eq(OpsScriptFileEntity::getOpsScriptId, param.getId())
            .eq(OpsScriptFileEntity::getFileType, 1)
            .eq(OpsScriptFileEntity::getIsDeleted, 0)
            .one();
        if (Objects.isNull(one) || StringUtil.isEmpty(one.getFilePath())) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "????????????????????????????????????????????????");
        }
        if (!new File(one.getFilePath()).exists()) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR,
                "???????????????" + one.getFilePath() + "??????????????????????????????");
        }

        String lockKey = "";
        String.format(LockKeyConstants.LOCK_CREATE_PROBE, WebPluginUtils.traceTenantId(), param.hashCode());
        this.isCreateError(!distributedLock.tryLockZeroWait(lockKey), AppConstants.TOO_FREQUENTLY);
        try {
            //????????????????????? ??????????????????
            opsScriptBatchNoDAO.lambdaUpdate()
                .set(OpsScriptBatchNoEntity::getIsDeleted, 1)
                .eq(OpsScriptBatchNoEntity::getOpsScriptId, manageEntity.getId())
                .update();
            OpsScriptBatchNoEntity entity = new OpsScriptBatchNoEntity();
            entity.setBatchNo(manageEntity.getId() + "#" + LocalDateTime.now());
            entity.setOpsScriptId(manageEntity.getId());
            opsScriptBatchNoDAO.save(entity);
            Long batchId = entity.getId();
            updateScriptStatus(param, OpsScriptExecutionEnum.RUNNING.getStatus());
            //????????????
            opsScriptLogCache.remove(param.getId() + "#" + WebPluginUtils.traceUserId());
            opsScriptThreadPool.execute(() -> this.runOpsShell(one, param, batchId));
        } catch (Exception e) {
            log.error("{}", "?????????????????????", e);
            throw this.getCreateError("??????????????????????????????" + e.getMessage());
        } finally {
            distributedLock.unLockSafely(lockKey);
        }
        return true;
    }

    /**
     * ???????????????
     *
     * @param condition ??????
     * @param message   ????????????
     */
    private void isCreateError(boolean condition, String message) {
        if (condition) {
            throw getCreateError(message);
        }
    }

    /**
     * ????????????
     *
     * @param message ????????????
     * @return ????????????
     */
    private TakinWebException getCreateError(String message) {
        return new TakinWebException(ExceptionCode.SCRIPT_MANAGE_EXECUTE_ERROR, message);
    }

    private void runOpsShell(OpsScriptFileEntity one, OpsScriptParam param, Long batchId) {
        String fullpath = one.getFilePath();
        String filename = one.getFilePath().substring(one.getFilePath().lastIndexOf("/") + 1);
        String fileDir = fullpath.substring(0, fullpath.lastIndexOf("/"));
        String command = "su - " + deployUser + " -c 'cd " + fileDir + " && sh " + filename + "' ";
        final String key = param.getId() + "#" + WebPluginUtils.traceUserId();
        LinuxHelper.runShell(command, 5000L, new LinuxHelper.Callback() {
            @Override
            public void before(Process process) {
                log.info("?????????????????????????????????file={}", one.getFilePath());
            }

            @Override
            public void after(Process process) {
                log.info("???????????????????????????file={}", one.getFilePath());
                try {
                    OpsExecutionVO executionVO = opsScriptLogCache.get(key);
                    if (executionVO == null) {
                        executionVO = new OpsExecutionVO("", true);
                    }
                    executionVO.setEnd(true);
                    opsScriptLogCache.put(key, executionVO);
                    //????????????????????????
                    log.info("????????????????????????...");
                    updateScriptStatus(param, OpsScriptExecutionEnum.FINISH.getStatus());
                    //??????log??????
                    String uploadId = UUID.randomUUID().toString();
                    String logPath = one.getFilePath().replace(one.getFileName(), "log/" + uploadId + ".log");
                    log.info("???????????????:{},??????log??????: {}", executionVO.getContent(), logPath);
                    FileManagerHelper.createFileByPathAndString(logPath, executionVO.getContent());
                    //??????????????????
                    log.info("?????????????????????...");
                    opsScriptExecuteResultDAO.lambdaUpdate()
                        .eq(OpsScriptExecuteResultEntity::getOpsScriptId, one.getOpsScriptId())
                        .set(OpsScriptExecuteResultEntity::getIsDeleted, 1)
                        .update();
                    OpsScriptExecuteResultEntity resultEntity = new OpsScriptExecuteResultEntity();

                    resultEntity.setUploadId(uploadId);
                    resultEntity.setBatchId(batchId);
                    resultEntity.setOpsScriptId(one.getOpsScriptId());
                    resultEntity.setLogFilePath(logPath);
                    resultEntity.setExcutorId(WebPluginUtils.traceUserId());
                    log.info("?????????????????????...");
                    opsScriptExecuteResultDAO.save(resultEntity);
                } catch (Exception e) {
                    log.error("?????????????????????", e);
                    throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "????????????????????????????????????" + e.getMessage());
                }
            }

            @Override
            public void exception(Process process, Exception e) {
                log.error("?????????????????????????????????file={}???????????????={}", one.getFilePath(), e.getMessage());
                opsScriptLogCache.put(key, new OpsExecutionVO(e.getMessage(), false));
            }
        }, logContent -> {
            log.info("????????????????????????file={}", one.getFilePath());
            OpsExecutionVO executionVO = opsScriptLogCache.get(key);
            if (Objects.isNull(executionVO)) {
                opsScriptLogCache.put(key, new OpsExecutionVO(logContent, false));
            } else {
                executionVO.setContent(executionVO.getContent() + " \n " + logContent);
                opsScriptLogCache.put(key, executionVO);
            }
        });
    }

    @Override
    public OpsExecutionVO getExcutionLog(String id) {
        if (Objects.isNull(id)) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "id???????????????");
        }
        OpsExecutionVO executionVO = opsScriptLogCache.get(id + "#" + WebPluginUtils.traceUserId());
        if (Objects.isNull(executionVO)) {
            executionVO = new OpsExecutionVO("", false);
        }
        return executionVO;
    }

    @Override
    public String getExcutionResult(String id) {
        if (Objects.isNull(id)) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "ID???????????????");
        }
        OpsScriptExecuteResultEntity one = opsScriptExecuteResultDAO.lambdaQuery().eq(
            OpsScriptExecuteResultEntity::getOpsScriptId, id).eq(
            OpsScriptExecuteResultEntity::getIsDeleted, 0).one();
        if (Objects.isNull(one)) {
            return "";
        }
        File file = new File(one.getLogFilePath());
        if (!file.exists()) {
            log.error("?????????{}???????????????", one.getLogFilePath());
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR,
                "?????????" + one.getLogFilePath() + "???????????????");
        }
        String logContent = FileUtils.readTextFileContent(file);
        return logContent;
    }

    private void updateScriptStatus(OpsScriptParam param, int status) {
        LambdaUpdateWrapper<OpsScriptManageEntity> manageWrapper = new LambdaUpdateWrapper<>();
        manageWrapper.set(OpsScriptManageEntity::getStatus, status);
        manageWrapper.eq(OpsScriptManageEntity::getId, param.getId());
        opsScriptManageDAO.update(manageWrapper);
    }

}
