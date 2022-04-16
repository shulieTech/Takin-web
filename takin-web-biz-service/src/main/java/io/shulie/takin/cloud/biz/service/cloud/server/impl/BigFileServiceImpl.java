package io.shulie.takin.cloud.biz.service.cloud.server.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import cn.hutool.crypto.SecureUtil;
import com.pamirs.takin.cloud.entity.dao.scene.manage.TSceneScriptRefMapper;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.SceneScriptRef;
import com.pamirs.takin.cloud.entity.domain.query.SceneScriptRefQueryParam;
import com.pamirs.takin.cloud.entity.domain.vo.file.Part;
import io.shulie.takin.cloud.biz.service.cloud.server.BigFileService;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneTaskService;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.json.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author hengyu
 * @date 2020-05-12 14:50
 */
@Slf4j
@Service
public class BigFileServiceImpl implements BigFileService {

    private static final String FALSE_CODE = "0";
    private static final String BIG_FILE_KEY_PREFIX = "big:file:cache";
    public static final int SUCCESS_VALUE = 200;
    public static final int RETRY_CODE = 502;
    public static final int ERROR_CODE = 500;
    @Value("${pradar.upload.client.dir}")
    private String uploadClientPath;
    @Resource
    private TSceneScriptRefMapper tSceneScriptRefMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${script.temp.path}")
    private String tempPath;

    @Value("${script.path}")
    private String scriptPath;

    @Autowired
    private CloudSceneTaskService cloudSceneTaskService;

    @Override
    public ResponseResult<?> upload(Part dto) {
        if (dto == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_VERIFY_ERROR, "param can not to be null !");
        }

        //todo 进行鉴权
        if (dto.getFileName() == null || dto.getSceneId() == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_VERIFY_ERROR, "fileName or sceneId param validator fail!");
        }
        try {
            String parentPath = getParentPath(dto.getUuid());
            File file = getDestFile(parentPath, dto.getFileName());
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            OutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream);
            IOUtils.write(dto.getByteData(), outputStream);
            outputStream.flush();
            dto.setStatus(true);
            log.info("{}:文件上传成功.当前文件：{},文件绝对路径为：{}", dto.getOriginalName(), dto.getFileName(), file.getAbsolutePath());
            return ResponseResult.success();
        } catch (Exception e) {
            dto.setStatus(false);
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_ERROR, "数据上传失败，当前文件" + dto.getOriginalName(), e);
        } finally {
            dto.setByteData(null);
            redisTemplate.opsForHash().put(getKey(dto.getUuid()), dto.getFileName(), dto);
            redisTemplate.expire(getKey(dto.getUuid()), 1, TimeUnit.DAYS);
        }
    }

    private String getKey(String uuid) {
        return BIG_FILE_KEY_PREFIX.concat(":").concat(uuid);
    }

    private File getDestFile(String parentPath, String fileName) {
        String filePath = parentPath.concat("/").concat(fileName);
        return new File(filePath);
    }

    private String getParentPath(String originalName) {
        return tempPath.concat("/").concat(originalName);
    }

    @Override
    public ResponseResult<Map<String, Object>> compact(Part part) {

        log.info("{}:开始文件打包行为", part.getOriginalName());
        if (part.getOriginalName() == null || part.getSceneId() == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_VERIFY_ERROR, "fileName or sceneId param validator fail!");
        }

        List<Part> errors = new LinkedList<>();
        String parentPath = getParentPath(part.getUuid());

        long start = System.currentTimeMillis();

        //1. todo 暂时关掉校验，上传包校验功能没有开发完全
        //ResponseResult<Map<String, Object>> result = validatorFile(
        //    dto, errors, parentPath);
        //if (result != null) { return result; }

        long validatorTime = System.currentTimeMillis() - start;
        log.info("{}:校验文件完成花费时间为：{}", part.getOriginalName(), validatorTime);

        String nfsFilePath = scriptPath.concat("/").concat(String.valueOf(part.getSceneId())).concat("/").concat(
            part.getOriginalName());

        File destFile = new File(nfsFilePath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }

        //原本的同名文件删除
        if (destFile.exists()) {
            if (!destFile.delete()) {
                log.error("原同名文件【{}】删除失败！", destFile.getAbsolutePath());
            } else {
                log.info("原同名文件【{}】删除成功！", destFile.getAbsolutePath());
            }
        }

        long fileSize;
        try {
            FileOutputStream dest = new FileOutputStream(destFile, true);
            FileChannel dc = dest.getChannel();
            long total = part.getTotal();
            for (long i = 0; i < total; i++) {
                File partFile = getDestFile(parentPath, part.getOriginalName() + "." + i);
                if (!partFile.exists()) {
                    log.warn("{}:文件块不存在：{}", part.getOriginalName(), partFile.getAbsolutePath());
                    break;
                }
                FileInputStream fileInputStream = new FileInputStream(partFile);
                FileChannel pc = fileInputStream.getChannel();
                //合并子块
                pc.transferTo(0, pc.size(), dc);
                partFile.delete();
                fileInputStream.close();
            }
            dest.flush();
            if (dest != null) {
                dest.close();
            }
            log.info("{}:聚合文件成功路径为：{}", part.getOriginalName(), destFile.getAbsolutePath());
            fileSize = destFile.length();
        } catch (Exception ex) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_ERROR, "服务端文件合并异常,文件名:" + part.getOriginalName(), ex);

        }

        //3. 更新文件到场景目录
        //long removeFileStart = System.currentTimeMillis();
        updateFileData(part, destFile, fileSize);
        long end = System.currentTimeMillis();
        //log.info("开始移动文件结束，耗时：{}", end - removeFileStart);

        long length = destFile.length();
        Map<String, Object> map = new HashMap<>(4);
        map.put("fileLength", length);
        map.put("mergeCost", end - start);
        log.info("big file merge time cost: {}", end - start);
        //清除位点
        try {
            cloudSceneTaskService.cleanCachedPosition(part.getSceneId());
        } catch (Exception e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_ERROR, "重置位点失败:" + part, e);
        }
        return success(map);
    }

    /**
     * 更新文件到场景目录
     *
     * @param part     -
     * @param destFile -
     * @param fileSize -
     */
    private void updateFileData(Part part, File destFile, long fileSize) {
        SceneScriptRefQueryParam queryParam = new SceneScriptRefQueryParam();
        queryParam.setFileName(part.getOriginalName());
        queryParam.setSceneId(part.getSceneId());

        String filePath = destFile.getParentFile().getName().concat("/") + destFile.getName();
        SceneScriptRef dbData = tSceneScriptRefMapper.selectByExample(queryParam);
        if (dbData != null) {
            updateSceneScriptRef(part, dbData, filePath, fileSize);
        } else {
            addSceneScriptRef(part, filePath, fileSize);
        }

    }

    /**
     * 校验文件块是否完整，现在使用算法是 MD5值进行验证
     *
     * @param part       传输对象
     * @param errors     error对象
     * @param parentPath 上传父目录
     * @return -
     */
    private ResponseResult<Map<String, Object>> validatorFile(Part part, List<Part> errors,
        String parentPath) {
        try {
            //进行文件块校验
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(getKey(part.getUuid()));
            for (Object key : entries.keySet()) {
                Object cacheData = entries.get(key);
                Part cachePart = cacheData instanceof Part ? (Part)cacheData : null;
                if (cachePart == null) {throw new RuntimeException("类型转换失败");}
                String fileName = cachePart.getFileName();
                File targetFile = new File(parentPath.concat("/").concat(fileName));
                FileInputStream fileInputStream = new FileInputStream(targetFile);

                FileChannel pc = fileInputStream.getChannel();
                long length = targetFile.length();
                ByteBuffer allocate = ByteBuffer.allocate((int)length);
                pc.read(allocate);
                byte[] array = allocate.array();
                String md5 = SecureUtil.md5().digestHex(array);
                boolean result = cachePart.getMd5().equals(md5);
                if (!result) {errors.add(cachePart);}
            }
        } catch (Exception e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_VERIFY_ERROR, "validator file exception", e);
        }
        if (errors.size() != 0) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_VERIFY_ERROR, part.getOriginalName() + "文件块损坏个数为" + errors.size());
        }
        return null;
    }

    private ResponseResult<Map<String, Object>> success(Object data) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("code", SUCCESS_VALUE);
        map.put("msg", "上传成功！");
        map.put("data", data);
        return ResponseResult.success(map);
    }

    private ResponseResult<Map<String, Object>> error(Integer code, String msg, Object data) {
        Map<String, Object> map = new HashMap<>(3);
        //重新上传
        map.put("code", code);
        map.put("msg", msg);
        map.put("data", data);
        return ResponseResult.success(map);
    }

    private String getMergePath(String parentPath, String merge) {
        return parentPath.concat("/").concat(merge);
    }

    /**
     * 更新场景文件
     *
     * @param part     -
     * @param dbData   db数据
     * @param filePath 文件路径
     * @param fileSize 文件大小
     */
    public void updateSceneScriptRef(Part part, SceneScriptRef dbData,
        String filePath,
        long fileSize) {
        SceneScriptRef updateParam = new SceneScriptRef();
        updateParam.setId(dbData.getId());
        updateParam.setFileSize(String.valueOf(fileSize));
        updateParam.setUploadPath(filePath);
        setFileExt(part, updateParam);
        tSceneScriptRefMapper.updateByPrimaryKeySelective(updateParam);
        // lxr 2021.06.21
        // 新方案：顺丰使用多块磁盘，提供对外接口进行文件拆分
        //fileSliceService.bigFileSlice(new BigFileSliceRequest(){{
        //    setRefId(dbData.getId());
        //    setFilePath(filePath);
        //    setSceneId(dbData.getSceneId());
        //}});
    }

    private void setFileExt(Part part, SceneScriptRef updateParam) {
        Map<String, String> map = new HashMap<>(1);
        if (part.getIsOrderSplit() != null) {
            map.put("isOrderSplit", String.valueOf(part.getIsOrderSplit()));
        }
        if (part.getIsSplit() != null) {
            map.put("isSplit", String.valueOf(part.getIsSplit()));
        }
        if (part.getDataCount() != null) {
            map.put("dataCount", String.valueOf(part.getDataCount()));
        }
        //大文件标识
        map.put("isBigFile", "1");
        updateParam.setFileExtend(JsonHelper.bean2Json(map));
    }

    /**
     * 添加，同步添加
     *
     * @param part     文件对象
     * @param filePath 文件路径
     * @param fileSize 文件大小
     */
    public synchronized void addSceneScriptRef(Part part, String filePath, long fileSize) {
        SceneScriptRefQueryParam queryParam = new SceneScriptRefQueryParam();
        queryParam.setFileName(part.getOriginalName());
        queryParam.setSceneId(part.getSceneId());

        SceneScriptRef insertParam = new SceneScriptRef();
        insertParam.setFileName(part.getOriginalName());
        insertParam.setSceneId(part.getSceneId());
        insertParam.setScriptType(0);
        insertParam.setFileType(1);
        insertParam.setUploadPath(filePath);
        insertParam.setFileSize(String.valueOf(fileSize));
        setFileExt(part, insertParam);
        try {
            Long refId = tSceneScriptRefMapper.insertSelective(insertParam);
            // lxr 2021.06.21
            // 新方案：顺丰使用多块磁盘，提供对外接口进行文件拆分
            //fileSliceService.bigFileSlice(new BigFileSliceRequest(){{
            //    setRefId(refId);
            //    setFilePath(filePath);
            //    setSceneId(part.getSceneId());
            //    setFileName(part.getOriginalName());
            //}});
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public File getPradarUploadFile() {
        File docDir = new File(uploadClientPath);
        if (!docDir.exists()) {
            if (!docDir.mkdirs()) {
                throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_UPLOAD_FILE_ERROR, "pradarUpload client dir create failed.");
            }
        }
        File[] files = docDir.listFiles();
        if (files == null || files.length == 0) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_UPLOAD_FILE_ERROR, "pradarUpload client not found.");
        }
        for (File file : files) {
            if (file.isFile()) {
                return file;
            }
        }
        throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_UPLOAD_FILE_ERROR, "pradarUpload client not found.");
    }

}
