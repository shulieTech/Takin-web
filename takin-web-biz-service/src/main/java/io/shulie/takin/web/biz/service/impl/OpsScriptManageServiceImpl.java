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
import io.shulie.takin.web.common.common.Separator;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.constant.LockKeyConstants;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.enums.opsscript.OpsScriptEnum;
import io.shulie.takin.web.common.enums.opsscript.OpsScriptExecutionEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.util.ConfigServerHelper;
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
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 运维脚本主表(OpsScriptManage)表服务实现类
 *
 * @author caijy
 * @since 2021-06-16 10:41:42
 */
@Service
@Slf4j
public class OpsScriptManageServiceImpl implements OpsScriptManageService {
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

    private String tempPath;

    private String deployUser;

    @Autowired
    private OpsScriptFileService opsScriptFileService;

    @PostConstruct
    public void init() {
        boolean deployUserEnable = ConfigServerHelper.getBooleanValueByKey(
            ConfigServerKeyEnum.TAKIN_FILE_OPS_SCRIPT_DEPLOY_USER_ENABLE);

        // 服务器是否添加执行脚本的用户
        deployUser = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_OPS_SCRIPT_DEPLOY_USER);
        if (deployUserEnable) {
            io.shulie.takin.web.biz.utils.LinuxHelper.executeLinuxCmdNotThrow("useradd " + deployUser);
        }

        tempPath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_DATA_PATH) + WebPluginUtils
            .traceTenantCode() + Separator.Separator1.getValue() +
            ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_FILE_OPS_SCRIPT_PATH);
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
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "缺少参数");
        }
        if (Objects.isNull(param.getName())) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "脚本名称不能为空！");
        }
        //是否唯一
        OpsScriptManageEntity one = opsScriptManageDAO.lambdaQuery()
            .eq(OpsScriptManageEntity::getName, param.getName())
            .eq(OpsScriptManageEntity::getIsDeleted, 0)
            .one();
        if (!Objects.isNull(one)) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "脚本名称需唯一！");
        }
        if (Objects.isNull(param.getScriptType())) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "脚本类型不能为空！");
        }

        if (CollectionUtils.isEmpty(param.getFileManageUpdateRequests())) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "文件不能为空！");
        }

        List<OpsScriptFileParam> fileParamList = param.getFileManageUpdateRequests();
        if (CollectionUtils.isEmpty(fileParamList)) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "主要文件不能为空！");
        }

        if (param.getName().length() > 20) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "名称长度不能超过20！");
        }

        //上传主要文件
        OpsScriptFileParam scriptFileParam = fileParamList.get(0);
        log.info("生成的文件uploadId为{}", scriptFileParam.getUploadId());
        this.saveLocalFile(tempPath + scriptFileParam.getUploadId() + "/" + scriptFileParam.getFileName(),
            this.getContent(scriptFileParam));

        //上传附件
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
        //添加操作日志
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
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "ID不能为空！");
        }
        if (CollectionUtils.isEmpty(param.getFileManageUpdateRequests())) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "文件不能为空！");
        }

        //查询文件
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

        //更新主表
        opsScriptManageDAO.lambdaUpdate()
            .eq(OpsScriptManageEntity::getId, param.getId())
            .set(OpsScriptManageEntity::getName, param.getName())
            .set(OpsScriptManageEntity::getGmtUpdate, new Date())
            .set(OpsScriptManageEntity::getScriptType, param.getScriptType())
            .update();

        //删除原目录
        OpsScriptFileParam scriptFileParam = param.getFileManageUpdateRequests().get(0);
        if (scriptFileParam.getContent() != null) {
            log.info("生成的文件uploadId为{}", scriptFileParam.getUploadId());
            FileManagerHelper.deleteFiles(Lists.newArrayList(scriptFileParam.getFilePath()));
            //上传主要文件
            this.saveLocalFile(tempPath + scriptFileParam.getUploadId() + "/" + scriptFileParam.getFileName(),
                this.getContent(scriptFileParam));
        }
        //上传附件
        List<OpsScriptFileParam> attachmentManageUpdateRequests = param.getAttachmentManageUpdateRequests();
        List<String> pathsFromPage = attachmentManageUpdateRequests.stream().filter(
            t -> StringUtil.isNotBlank(t.getFilePath())).map(
            t -> t.getFilePath()).collect(Collectors.toList());
        //需要删除的附件
        List<String> deletePath = fileList.stream().filter(
            t -> t.getFileType().equals(2) && !pathsFromPage.contains(t.getFilePath())).map(
            t -> t.getFilePath()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deletePath)) {
            FileManagerHelper.deleteFiles(deletePath);
        }
        //上传新附件
        attachmentManageUpdateRequests.stream().filter(t -> StringUtil.isEmpty(t.getOpsScriptId())).forEach(t -> {
            t.setUploadId(scriptFileParam.getUploadId());
            String targetPath = tempPath + scriptFileParam.getUploadId() + "/" + t.getFileName();
            this.saveLocalFile(targetPath, this.getContent(t));
            t.setFilePath(targetPath);
        });

        //删除原有file 新增其他file
        opsScriptFileDAO.lambdaUpdate()
            .eq(OpsScriptFileEntity::getOpsScriptId, param.getId())
            .set(OpsScriptFileEntity::getIsDeleted, 1)
            .update();

        //新增主要file
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
        //新增附件file
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

        //添加操作日志
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
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "ID不能为空！");
        }
        OpsScriptDetailVO detailVO = new OpsScriptDetailVO();
        OpsScriptManageEntity entity = opsScriptManageDAO.getById(param.getId());
        if (Objects.isNull(entity)) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "查询不到此脚本！");
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
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "ID不能为空！");
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

        //添加操作日志
        OperationLogContextHolder.addVars(BizOpConstants.Vars.OPS_SCRIPT_ID, param.getId() + "");
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean execute(OpsScriptParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getId())) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "ID不能为空！");
        }
        OpsScriptManageEntity manageEntity = opsScriptManageDAO.getById(param.getId());
        if (Objects.isNull(manageEntity)) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "此脚本实例不存在！");
        }
        OpsScriptFileEntity one = opsScriptFileDAO.lambdaQuery()
            .eq(OpsScriptFileEntity::getOpsScriptId, param.getId())
            .eq(OpsScriptFileEntity::getFileType, 1)
            .eq(OpsScriptFileEntity::getIsDeleted, 0)
            .one();
        if (Objects.isNull(one) || StringUtil.isEmpty(one.getFilePath())) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "此脚本实例找不到文件，无法执行！");
        }
        if (!new File(one.getFilePath()).exists()) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR,
                "脚本文件【" + one.getFilePath() + "】不存在，无法执行！");
        }

        String lockKey = "";
        String.format(LockKeyConstants.LOCK_CREATE_PROBE, WebPluginUtils.traceTenantId(), param.hashCode());
        this.isCreateError(!distributedLock.tryLockZeroWait(lockKey), AppConstants.TOO_FREQUENTLY);
        try {
            //删除原有批次号 生成新批次号
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
            //执行文件
            opsScriptLogCache.remove(param.getId() + "#" + WebPluginUtils.traceUserId());
            opsScriptThreadPool.execute(() -> this.runOpsShell(one, param, batchId));
        } catch (Exception e) {
            log.error("{}", "脚本执行错误！", e);
            throw this.getCreateError("脚本执行错误！原因：" + e.getMessage());
        } finally {
            distributedLock.unLockSafely(lockKey);
        }
        return true;
    }

    /**
     * 是创建错误
     *
     * @param condition 条件
     * @param message   错误信息
     */
    private void isCreateError(boolean condition, String message) {
        if (condition) {
            throw getCreateError(message);
        }
    }

    /**
     * 创建错误
     *
     * @param message 错误信息
     * @return 错误异常
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
                log.info("运维脚本正在准备执行，file={}", one.getFilePath());
            }

            @Override
            public void after(Process process) {
                log.info("运维脚本执行结束，file={}", one.getFilePath());
                try {
                    OpsExecutionVO executionVO = opsScriptLogCache.get(key);
                    if (executionVO == null) {
                        executionVO = new OpsExecutionVO("", true);
                    }
                    executionVO.setEnd(true);
                    opsScriptLogCache.put(key, executionVO);
                    //修改脚本执行状态
                    log.info("修改脚本执行状态...");
                    updateScriptStatus(param, OpsScriptExecutionEnum.FINISH.getStatus());
                    //生成log文件
                    String uploadId = UUID.randomUUID().toString();
                    String logPath = one.getFilePath().replace(one.getFileName(), "log/" + uploadId + ".log");
                    log.info("获取到文本:{},生成log文件: {}", executionVO.getContent(), logPath);
                    FileManagerHelper.createFileByPathAndString(logPath, executionVO.getContent());
                    //保存执行日志
                    log.info("删除旧执行日志...");
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
                    log.info("保存新执行日志...");
                    opsScriptExecuteResultDAO.save(resultEntity);
                } catch (Exception e) {
                    log.error("脚本未正常结束", e);
                    throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "脚本未正常结束！原因为：" + e.getMessage());
                }
            }

            @Override
            public void exception(Process process, Exception e) {
                log.error("运维脚本执行发生异常，file={}，异常原因={}", one.getFilePath(), e.getMessage());
                opsScriptLogCache.put(key, new OpsExecutionVO(e.getMessage(), false));
            }
        }, logContent -> {
            log.info("运维脚本执行中，file={}", one.getFilePath());
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
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "id不能为空！");
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
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR, "ID不能为空！");
        }
        OpsScriptExecuteResultEntity one = opsScriptExecuteResultDAO.lambdaQuery().eq(
            OpsScriptExecuteResultEntity::getOpsScriptId, id).eq(
            OpsScriptExecuteResultEntity::getIsDeleted, 0).one();
        if (Objects.isNull(one)) {
            return "";
        }
        File file = new File(one.getLogFilePath());
        if (!file.exists()) {
            log.error("文件【{}】不存在！", one.getLogFilePath());
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_VALIDATE_ERROR,
                "文件【" + one.getLogFilePath() + "】不存在！");
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
