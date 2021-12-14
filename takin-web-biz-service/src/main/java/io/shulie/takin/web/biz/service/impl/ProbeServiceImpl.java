package io.shulie.takin.web.biz.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.shulie.instrument.simulator.agent.api.AgentFileResolver;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.output.probe.CreateProbeOutput;
import io.shulie.takin.web.biz.pojo.output.probe.ProbeListOutput;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.ProbeService;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.constant.LockKeyConstants;
import io.shulie.takin.web.common.constant.ProbeConstants;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.shulie.takin.web.common.util.AppCommonUtil;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.data.dao.ProbeDAO;
import io.shulie.takin.web.data.param.probe.CreateProbeParam;
import io.shulie.takin.web.data.param.probe.UpdateProbeParam;
import io.shulie.takin.web.data.result.probe.ProbeDetailResult;
import io.shulie.takin.web.data.result.probe.ProbeListResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 探针包表(Probe)表服务实现类
 *
 * @author liuchuan
 * @since 2021-06-03 13:40:57
 */
@Slf4j
@Service
public class ProbeServiceImpl implements ProbeService, ProbeConstants, AppConstants {

    /**
     * 上传文件的路径
     */
    @Value("${takin.data.path}")
    private String uploadPath;

    @Autowired
    private ProbeDAO probeDAO;

    @Autowired
    private DistributedLock distributedLock;

    @Override
    public PagingList<ProbeListOutput> pageProbe(PageBaseDTO pageDTO) {
        PagingList<ProbeListResult> resultPage = probeDAO.pageProbe(pageDTO);
        return resultPage.getTotal() == 0 ? PagingList.empty()
            : PagingList.of(DataTransformUtil.list2list(resultPage.getList(), ProbeListOutput.class), resultPage.getTotal());
    }

    @Override
    public CreateProbeOutput create(String probePath) {
        String lockKey = String.format(LockKeyConstants.LOCK_CREATE_PROBE, WebPluginUtils.traceTenantId(), probePath.hashCode());
        this.isCreateError(!distributedLock.tryLockZeroWait(lockKey), TOO_FREQUENTLY);

        try {
            File uncompressFile = this.getUncompressFile(probePath);

            String probeVersionString = this.checkAndGetProbeVersionString(uncompressFile);

            // 更新或创建探针记录
            Long probeId = this.saveOrUpdateByVersion(probeVersionString, probePath);

            CreateProbeOutput createProbeOutput = new CreateProbeOutput();
            createProbeOutput.setProbeId(probeId);
            createProbeOutput.setProbeVersion(probeVersionString);
            return createProbeOutput;

        } catch (Exception e) {
            log.error("探针创建错误 --> 错误信息: {}", e.getMessage(), e);
            throw this.getCreateError(e.getMessage());

        } finally {
            distributedLock.unLockSafely(lockKey);
        }
    }

    /**
     * 检查并获取探针版本号字符串
     *
     * @param uncompressFile 解压的文件
     * @return 探针版本号字符串
     * @throws IOException 文件删除错误, 抛出 io 异常
     */
    private String checkAndGetProbeVersionString(File uncompressFile) throws IOException {
        // 获得探针版本号
        String probeVersionString = AgentFileResolver.getSimulatorVersion(uncompressFile);

        // 删除解压之前的文件
        FileUtil.del(uncompressFile);

        // 探针版本号判断, 必须大于 5.0.0
        this.isCreateError(StrUtil.isBlank(probeVersionString), "探针版本号错误!");
        this.isCreateError(!AppCommonUtil.isNewProbeVersion(probeVersionString), "探针版本号低于 5.0.0!");
        return probeVersionString;
    }

    /**
     * 获得解压的文件
     *
     * @param probePath 探针文件路径
     * @return 解压后的探针包
     */
    private File getUncompressFile(String probePath) {
        // 读取文件
        File compressFile = new File(probePath);
        // 判断文件是否存在
        this.isCreateError(!compressFile.exists(), "文件不存在!");

        // 判断文件类型
        String fileType = FileTypeUtil.getType(compressFile);
        this.isCreateError(!this.isZipFileType(fileType)
            && !this.isGzFileType(fileType), "文件类型错误!");

        // 保存最终探针包的路径
        String baseUploadPath = CommonUtil.getUploadPath(uploadPath, FOLDER_PROBE);
        // 解压文件, 解压到 probe/20210608/timestamp/
        // 解压文件为 probe/20210608/timestamp/simulator
        String uncompressPath = baseUploadPath + System.currentTimeMillis();
        if (this.isGzFileType(fileType)) {
            // 判断是不是 tar.gz
            this.isCreateError(!compressFile.getName().endsWith(FILE_TYPE_TAR_GZ), "文件类型错误!");
            this.uncompressGz(compressFile, uncompressPath);
        } else {
            ZipUtil.unzip(probePath, uncompressPath);
        }

        // 完整性校验
        File uncompressFile = new File(uncompressPath);
        List<String> errorMessages = AgentFileResolver.check(uncompressFile);
        this.isCreateError(CollectionUtil.isNotEmpty(errorMessages), String.join(COMMA, errorMessages));
        return uncompressFile;
    }

    /**
     * 根据版本号, 来更新或者创建
     *
     * @param version 版本号
     * @param path    文件路径
     * @return 探针记录id
     */
    private Long saveOrUpdateByVersion(String version, String path) {
        // 通过版本查看本地数据库是否存在
        ProbeDetailResult result = probeDAO.getByVersion(version);
        if (result != null) {
            UpdateProbeParam updateProbeParam = new UpdateProbeParam();
            updateProbeParam.setId(result.getId());
            updateProbeParam.setPath(path);
            this.isCreateError(!probeDAO.updateById(updateProbeParam), "相同版本探针更新失败!");
            return result.getId();
        }

        // 存在, 更新, 不存在, 创建
        CreateProbeParam createProbeParam = new CreateProbeParam();
        createProbeParam.setVersion(version);
        createProbeParam.setPath(path);
        createProbeParam.setGmtUpdate(new Date());
        this.isCreateError(!probeDAO.save(createProbeParam), "探针记录创建失败!");
        return createProbeParam.getId();
    }

    /**
     * zip 文件
     *
     * @param fileType 文件类型
     * @return 是否
     */
    private boolean isZipFileType(String fileType) {
        return FILE_TYPE_ZIP.equals(fileType);
    }

    /**
     * gz 文件
     *
     * @param fileType 文件类型
     * @return 是否
     */
    private boolean isGzFileType(String fileType) {
        return FILE_TYPE_GZ.equals(fileType);
    }

    /**
     * 解压 gz
     *
     * @param source     压缩文件
     * @param targetPath 输出路径
     */
    private void uncompressGz(File source, String targetPath) {
        // InputStream输入流，以下四个流将tar.gz读取到内存并操作
        // BufferedInputStream缓冲输入流
        // GzipCompressorInputStream解压输入流
        // TarArchiveInputStream解tar包输入流
        try (InputStream fi = new FileInputStream(source);
             BufferedInputStream bi = new BufferedInputStream(fi);
             GzipCompressorInputStream gzi = new GzipCompressorInputStream(bi);
             TarArchiveInputStream ti = new TarArchiveInputStream(gzi)) {

            ArchiveEntry entry;
            while ((entry = ti.getNextEntry()) != null) {

                //获取解压文件目录，并判断文件是否损坏
                Path newPath = zipSlipProtect(entry, targetPath);

                if (entry.isDirectory()) {
                    //创建解压文件目录
                    Files.createDirectories(newPath);
                } else {
                    //再次校验解压文件目录是否存在
                    Path parent = newPath.getParent();
                    if (parent != null) {
                        if (Files.notExists(parent)) {
                            Files.createDirectories(parent);
                        }
                    }
                    // 将解压文件输入到TarArchiveInputStream，输出到磁盘newPath目录
                    Files.copy(ti, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (Exception e) {
            log.error("解压文件错误: 文件类型 gz, 错误信息: {}", e.getMessage(), e);
            throw new RuntimeException("解压gz文件错误!");
        }
    }

    /**
     * 判断压缩文件是否被损坏，并返回该文件的解压目录
     *
     * @param entry     tar
     * @param targetDir 解压到的路径
     * @return 路径
     */
    private Path zipSlipProtect(ArchiveEntry entry, String targetDir) {
        Path targetDirResolved = Paths.get(targetDir).resolve(entry.getName());
        Path normalizePath = targetDirResolved.normalize();

        if (!normalizePath.startsWith(targetDir)) {
            throw new RuntimeException("压缩文件已被损坏: " + entry.getName());
        }

        return normalizePath;
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
        return new TakinWebException(ExceptionCode.PROBE_CREATE_ERROR, message);
    }

}
