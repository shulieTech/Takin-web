package io.shulie.takin.cloud.data.dao.scene.manage.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pamirs.takin.cloud.entity.dao.scene.manage.TSceneScriptRefMapper;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.SceneScriptRef;
import io.shulie.takin.cloud.common.enums.FileSliceStatusEnum;
import io.shulie.takin.cloud.common.utils.Md5Util;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneBigFileSliceDAO;
import io.shulie.takin.cloud.data.mapper.mysql.SceneBigFileSliceMapper;
import io.shulie.takin.cloud.data.mapper.mysql.SceneScriptRefMapper;
import io.shulie.takin.cloud.data.model.mysql.SceneBigFileSliceEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneScriptRefEntity;
import io.shulie.takin.cloud.data.param.scenemanage.SceneBigFileSliceParam;
import io.shulie.takin.cloud.data.util.MPUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author moriarty
 */
@Slf4j
@Component
public class SceneBigFileSliceDAOImpl extends ServiceImpl<SceneBigFileSliceMapper, SceneBigFileSliceEntity>
    implements SceneBigFileSliceDAO, MPUtil<SceneBigFileSliceEntity> {

    @Resource(type = SceneScriptRefMapper.class)
    private SceneScriptRefMapper sceneScriptRefMapper;

    @Resource(type = TSceneScriptRefMapper.class)
    private TSceneScriptRefMapper tSceneScriptRefMapper;

    @Resource(type = SceneBigFileSliceMapper.class)
    private SceneBigFileSliceMapper sceneBigFileSliceMapper;

    private static final String SUFFIX = ".csv";

    @Override
    public int create(SceneBigFileSliceParam param) {
        LambdaQueryWrapper<SceneScriptRefEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneScriptRefEntity::getSceneId, param.getSceneId());
        wrapper.eq(SceneScriptRefEntity::getId, param.getFileRefId());
        List<SceneScriptRefEntity> sceneScriptRefEntities = sceneScriptRefMapper.selectList(wrapper);
        if (sceneScriptRefEntities.size() == 1) {
            SceneScriptRefEntity entity = sceneScriptRefEntities.get(0);
            if (entity.getFileName().endsWith(SUFFIX)) {
                SceneBigFileSliceEntity sliceEntity = new SceneBigFileSliceEntity() {{
                    setFileName(entity.getFileName());
                    setFilePath(entity.getUploadPath());
                    setSceneId(param.getSceneId());
                    setStatus(param.getStatus());
                    setScriptRefId(entity.getId());
                    setSliceCount(param.getSliceCount());
                    setSliceInfo(param.getSliceInfo());
                    setFileUpdateTime(param.getFileUploadTime());
                    setCreateTime(LocalDateTime.now());
                }};
                return sceneBigFileSliceMapper.insert(sliceEntity);
            }
        }
        return 0;
    }

    @Override
    public int isFileSliced(SceneBigFileSliceParam param) {
        LambdaQueryWrapper<SceneScriptRefEntity> refWrapper = new LambdaQueryWrapper<>();
        refWrapper.eq(SceneScriptRefEntity::getSceneId, param.getSceneId());
        refWrapper.eq(SceneScriptRefEntity::getFileName, param.getFileName());
        refWrapper.eq(SceneScriptRefEntity::getIsDeleted, 0);
        //文件类型有多种，只查类型为1的数据文件
        refWrapper.eq(SceneScriptRefEntity::getFileType, 1);
        List<SceneScriptRefEntity> sceneScriptRefEntities = sceneScriptRefMapper.selectList(refWrapper);
        //分片信息
        LambdaQueryWrapper<SceneBigFileSliceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneBigFileSliceEntity::getSceneId, param.getSceneId());
        wrapper.eq(SceneBigFileSliceEntity::getFileName, param.getFileName());
        SceneBigFileSliceEntity sliceEntity = sceneBigFileSliceMapper.selectOne(wrapper);

        if (CollectionUtils.isEmpty(sceneScriptRefEntities) || Objects.isNull(sliceEntity)) {
            return FileSliceStatusEnum.UNSLICED.getCode();
        } else if (FileSliceStatusEnum.SLICING.getCode() == sliceEntity.getStatus()) {
            return FileSliceStatusEnum.SLICING.getCode();
        }
        return sceneScriptRefEntities
            .stream()
            .findFirst()
            .map(entity -> {
                    if (Objects.isNull(sliceEntity.getSliceCount())
                        || Objects.isNull(sliceEntity.getSliceInfo())
                        || Objects.isNull(sliceEntity.getFileUpdateTime())
                        || !param.getSliceCount().equals(sliceEntity.getSliceCount())) {
                        log.warn(
                            "【文件拆分】--场景ID【{}】,文件名【{}】，文件变更或pod数量变更.分片信息中的文件上传时间【{}】,脚本关联的文件上传时间【{}】；已分片数量【{}】,"
                                + "启动pod数量【{}】",
                            param.getSceneId(), param.getFileName(), sliceEntity.getFileUpdateTime(),
                            entity.getUploadTime(), sliceEntity.getSliceCount(), param.getSliceCount());
                        return FileSliceStatusEnum.FILE_CHANGED.getCode();
                    } else if (Objects.nonNull(sliceEntity.getFileUpdateTime())
                        && sliceEntity.getFileUpdateTime().equals(entity.getUploadTime())) {
                        //如果参数验证结果为已分片，验证文件的md5值
                        if (StringUtils.isNotBlank(param.getFileMd5()) && StringUtils.isNotBlank(entity.getFileMd5())) {
                            String fileMd5 = Md5Util.md5File(param.getFilePath());
                            if (param.getFileMd5().equals(fileMd5)) {
                                return FileSliceStatusEnum.SLICED.getCode();
                            }
                            //更新文件的md5值到数据库
                            //entity.setFileMd5(fileMd5);
                            sceneScriptRefMapper.updateById(entity);
                            return FileSliceStatusEnum.FILE_CHANGED.getCode();
                        }
                        return FileSliceStatusEnum.SLICED.getCode();
                    }
                    return FileSliceStatusEnum.FILE_CHANGED.getCode();
                }
            ).orElse(FileSliceStatusEnum.UNSLICED.getCode());
    }

    @Override
    public int update(SceneBigFileSliceParam param) {
        LambdaQueryWrapper<SceneBigFileSliceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneBigFileSliceEntity::getSceneId, param.getSceneId());
        wrapper.eq(SceneBigFileSliceEntity::getFileName, param.getFileName());
        SceneBigFileSliceEntity entity = sceneBigFileSliceMapper.selectOne(wrapper);
        if (Objects.isNull(entity) || Objects.isNull(entity.getId())) {
            return 0;
        }
        SceneScriptRefEntity entity1 = this.selectRef(param);
        entity.setSliceCount(param.getSliceCount());
        entity.setScriptRefId(param.getFileRefId());
        entity.setFilePath(entity1.getUploadPath());
        entity.setFileName(param.getFileName());
        entity.setFileUpdateTime(param.getFileUploadTime());
        entity.setSliceInfo(param.getSliceInfo());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setStatus(param.getStatus());
        return sceneBigFileSliceMapper.updateById(entity);
    }

    @Override
    public SceneScriptRefEntity selectRef(SceneBigFileSliceParam param) {
        if (Objects.nonNull(param.getFileRefId()) && param.getFileRefId() > 0) {
            return sceneScriptRefMapper.selectById(param.getFileRefId());
        }
        LambdaQueryWrapper<SceneScriptRefEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneScriptRefEntity::getSceneId, param.getSceneId());
        wrapper.eq(SceneScriptRefEntity::getFileName, param.getFileName());
        //只查type=1的文件
        wrapper.eq(SceneScriptRefEntity::getFileType, 1);
        wrapper.eq(SceneScriptRefEntity::getIsDeleted, 0);
        return sceneScriptRefMapper.selectOne(wrapper);
    }

    @Override
    public SceneBigFileSliceEntity selectOne(SceneBigFileSliceParam param) {
        LambdaQueryWrapper<SceneBigFileSliceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneBigFileSliceEntity::getSceneId, param.getSceneId());
        if (StringUtils.isNotBlank(param.getFileName())) {
            wrapper.eq(SceneBigFileSliceEntity::getFileName, param.getFileName());
        }
        List<SceneBigFileSliceEntity> sceneBigFileSliceEntities = sceneBigFileSliceMapper.selectList(wrapper);
        if (sceneBigFileSliceEntities.size() >= 1) {
            return sceneBigFileSliceEntities.get(0);
        }
        return null;
    }

    @Override
    public Long createRef(SceneScriptRef ref) {
        return tSceneScriptRefMapper.insertSelective(ref);
    }

    @Override
    public int updateRef(SceneScriptRefEntity entity) {
        return sceneScriptRefMapper.updateById(entity);
    }
}
