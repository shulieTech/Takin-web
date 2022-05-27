package io.shulie.takin.cloud.data.dao.report;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import io.shulie.takin.cloud.common.constants.ReportConstants;
import io.shulie.takin.cloud.common.utils.JsonPathUtil;
import io.shulie.takin.cloud.data.mapper.mysql.ReportBusinessActivityDetailMapper;
import io.shulie.takin.cloud.data.mapper.mysql.ReportMapper;
import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.param.report.ReportInsertParam;
import io.shulie.takin.cloud.data.param.report.ReportQueryParam;
import io.shulie.takin.cloud.data.param.report.ReportQueryParam.PressureTypeRelation;
import io.shulie.takin.cloud.data.param.report.ReportUpdateConclusionParam;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.web.common.util.RedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author 无涯
 * @date 2020/12/17 3:31 下午
 */
@Slf4j
@Service
public class ReportDaoImpl implements ReportDao {

    @Resource
    private ReportMapper reportMapper;

    @Resource
    private ReportBusinessActivityDetailMapper detailMapper;

    @Resource
    private RedisClientUtil redisClientUtil;

    @Override
    public int insert(ReportInsertParam param) {
        if (Objects.nonNull(param)) {
            ReportEntity entity = BeanUtil.copyProperties(param, ReportEntity.class);
            Date insertDate = new Date();
            entity.setGmtCreate(insertDate);
            entity.setGmtUpdate(insertDate);
            entity.setStartTime(insertDate);
            return reportMapper.insert(entity);
        }
        return 0;
    }

    @Override
    public List<ReportResult> queryReportList(ReportQueryParam param) {
        LambdaQueryWrapper<ReportEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(param.getEndTime())) {
            wrapper.le(ReportEntity::getGmtCreate, param.getEndTime());
        }
        if (null != param.getStatus()) {
            wrapper.eq(ReportEntity::getStatus, param.getStatus());
        }
        if (null != param.getIsDel()) {
            wrapper.eq(ReportEntity::getIsDeleted, param.getIsDel());
        }
        wrapper.isNotNull(param.isJobIdNotNull(), ReportEntity::getJobId);

        if (Objects.nonNull(param.getPressureTypeRelation())) {
            PressureTypeRelation relation = param.getPressureTypeRelation();
            if (relation.getHave()) {
                wrapper.eq(ReportEntity::getPressureType, relation.getPressureType());
            } else {
                wrapper.ne(ReportEntity::getPressureType, relation.getPressureType());
            }
        }

        List<ReportEntity> entities = reportMapper.selectList(wrapper);
        if (entities != null && entities.size() > 0) {
            return entities.stream()
                    .map(t -> BeanUtil.copyProperties(t, ReportResult.class))
                    .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    @Override
    public ReportResult selectById(Long id) {
        List<ReportEntity> entityList = reportMapper.selectList(
                Wrappers.lambdaQuery(ReportEntity.class)
                        .eq(ReportEntity::getId, id)
        );
        if (entityList.size() != 1) {
            return null;
        }
        return BeanUtil.copyProperties(entityList.get(0), ReportResult.class);
    }

    /**
     * 获取最新一条报告id
     *
     * @param sceneId 场景主键
     * @return -
     */
    @Override
    public ReportResult getRecentlyReport(Long sceneId) {
        LambdaQueryWrapper<ReportEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportEntity::getSceneId, sceneId);

        Object reportId = redisClientUtil.hmget(PressureStartCache.getSceneResourceKey(sceneId),
                PressureStartCache.REPORT_ID);
        if (Objects.nonNull(reportId)) {
            wrapper.eq(ReportEntity::getId, Long.valueOf(String.valueOf(reportId)));
        }
        wrapper.orderByDesc(ReportEntity::getId);
        wrapper.last("limit 1");
        List<ReportEntity> entities = reportMapper.selectList(wrapper);
        if (entities != null && entities.size() > 0) {
            return BeanUtil.copyProperties(entities.get(0), ReportResult.class);
        }
        return null;
    }

    @Override
    public void updateReportConclusion(ReportUpdateConclusionParam param) {
        ReportEntity entity = BeanUtil.copyProperties(param, ReportEntity.class);
        reportMapper.updateById(entity);
    }

    @Override
    public void updateReport(ReportUpdateParam param) {
        ReportEntity entity = BeanUtil.copyProperties(param, ReportEntity.class);
        if (null == param.getGmtUpdate()) {
            entity.setGmtUpdate(Calendar.getInstance().getTime());
        }
        reportMapper.updateById(entity);
    }

    @Override
    public void finishReport(Long reportId) {
        ReportEntity entity = new ReportEntity();
        entity.setId(reportId);
        entity.setStatus(ReportConstants.FINISH_STATUS);
        entity.setGmtUpdate(new Date());
        reportMapper.updateById(entity);
    }

    @Override
    public void updateReportLock(Long resultId, Integer lock) {
        ReportEntity entity = new ReportEntity();
        entity.setId(resultId);
        entity.setLock(lock);
        entity.setGmtUpdate(new Date());
        reportMapper.updateById(entity);
    }

    @Override
    public ReportResult getTempReportBySceneId(Long sceneId) {
        LambdaQueryWrapper<ReportEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportEntity::getSceneId, sceneId);

        Object reportId = redisClientUtil.hmget(PressureStartCache.getSceneResourceKey(sceneId),
                PressureStartCache.REPORT_ID);
        if (Objects.nonNull(reportId)) {
            wrapper.eq(ReportEntity::getId, Long.valueOf(String.valueOf(reportId)));
        }

        wrapper.eq(ReportEntity::getIsDeleted, 0);
        wrapper.orderByDesc(ReportEntity::getId);
        wrapper.last("limit 1");
        List<ReportEntity> entities = reportMapper.selectList(wrapper);
        if (entities != null && entities.size() > 0) {
            return BeanUtil.copyProperties(entities.get(0), ReportResult.class);
        }
        return null;
    }

    @Override
    public ReportResult getReportBySceneId(Long sceneId) {
        LambdaQueryWrapper<ReportEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportEntity::getSceneId, sceneId);
        // 根据状态
        wrapper.eq(ReportEntity::getStatus, 0);
        wrapper.eq(ReportEntity::getIsDeleted, 0);
        wrapper.orderByDesc(ReportEntity::getId);
        wrapper.last("limit 1");
        List<ReportEntity> entities = reportMapper.selectList(wrapper);
        if (entities != null && entities.size() > 0) {
            return BeanUtil.copyProperties(entities.get(0), ReportResult.class);
        }
        return null;
    }

    @Override
    public void updateReportStartTime(Long reportId, Date startTime) {
        ReportEntity entity = new ReportEntity();
        entity.setId(reportId);
        entity.setStartTime(startTime);
        entity.setGmtUpdate(new Date());
        reportMapper.updateById(entity);
    }

    /**
     * 根据场景主键设置压测报告状态
     *
     * @param sceneId 场景主键
     * @param status  状态值
     * @return 操作影响行数
     */
    @Override
    public int updateStatus(Long sceneId, Integer status) {
        return reportMapper.update(
                new ReportEntity() {{
                    setStatus(status);
                }},
                Wrappers.lambdaQuery(ReportEntity.class).eq(ReportEntity::getSceneId, sceneId));
    }

    @Override
    public void updateReportEndTime(Long resultId, Date endTime) {
        ReportEntity entity = new ReportEntity();
        entity.setId(resultId);
        entity.setEndTime(endTime);
        entity.setGmtUpdate(new Date());
        reportMapper.updateById(entity);
    }

    @Override
    public ReportResult getById(Long resultId) {
        if (resultId == null) {
            return null;
        }
        ReportEntity reportEntity = reportMapper.selectById(resultId);
        return BeanUtil.copyProperties(reportEntity, ReportResult.class);
    }

    @Override
    public List<ReportBusinessActivityDetailEntity> getReportBusinessActivityDetailsByReportId(Long reportId,
                                                                                               NodeTypeEnum nodeType) {
        LambdaQueryWrapper<ReportBusinessActivityDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReportBusinessActivityDetailEntity::getReportId, reportId);
        queryWrapper.eq(ReportBusinessActivityDetailEntity::getIsDeleted, 0);
        List<ReportBusinessActivityDetailEntity> entities = detailMapper.selectList(queryWrapper);
        if (Objects.isNull(nodeType)) {
            return entities;
        }
        ReportEntity reportEntity = reportMapper.selectById(reportId);
        if (Objects.nonNull(reportEntity) && StringUtils.isNotBlank(reportEntity.getScriptNodeTree())) {
            List<ScriptNode> nodeList = JsonPathUtil.getNodeListByType(reportEntity.getScriptNodeTree(), nodeType);
            if (CollectionUtils.isNotEmpty(nodeList)) {
                List<String> xpathMd5List = nodeList.stream().filter(Objects::nonNull)
                        .map(ScriptNode::getXpathMd5).collect(Collectors.toList());
                return entities.stream().filter(Objects::nonNull)
                        .filter(entity -> xpathMd5List.contains(entity.getBindRef()))
                        .collect(Collectors.toList());
            }
        }
        return null;
    }

    @Override
    public ReportResult selectByJobId(Long jobId) {
        if (Objects.isNull(jobId)) {
            return null;
        }
        ReportEntity entity = reportMapper.selectOne(
                Wrappers.lambdaQuery(ReportEntity.class)
                        .eq(ReportEntity::getJobId, jobId)
        );
        return BeanUtil.copyProperties(entity, ReportResult.class);
    }

    @Override
    public ReportResult selectByResourceId(String resourceId) {
        if (StringUtils.isBlank(resourceId)) {
            return null;
        }
        ReportEntity entity = reportMapper.selectOne(
                Wrappers.lambdaQuery(ReportEntity.class)
                        .eq(ReportEntity::getResourceId, resourceId)
        );
        return BeanUtil.copyProperties(entity, ReportResult.class);
    }

    @Override
    public List<ReportEntity> queryReportBySceneIds(List<Long> sceneIds) {
        if (CollectionUtils.isEmpty(sceneIds)) {
            return null;
        }
        return reportMapper.queryBySceneIds(sceneIds);
    }

    @Override
    public List<ReportBusinessActivityDetailEntity> getActivityByReportIds(List<Long> reportIds) {
        LambdaQueryWrapper<ReportBusinessActivityDetailEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ReportBusinessActivityDetailEntity::getReportId, reportIds);
        return detailMapper.selectList(wrapper);
    }
}
