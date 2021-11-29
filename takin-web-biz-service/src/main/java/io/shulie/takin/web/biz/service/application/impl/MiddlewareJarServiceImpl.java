package io.shulie.takin.web.biz.service.application.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import com.pamirs.takin.entity.domain.vo.middleware.ImportExcelFixVO;
import com.pamirs.takin.entity.domain.vo.middleware.ImportExcelVO;
import io.shulie.takin.web.biz.cache.DictionaryCache;
import io.shulie.takin.web.biz.constant.BizOpConstants.OpTypes;
import io.shulie.takin.web.biz.pojo.dto.application.CompareApplicationMiddlewareDTO;
import io.shulie.takin.web.biz.pojo.response.application.MiddlewareCompareResponse;
import io.shulie.takin.web.biz.pojo.response.application.MiddlewareImportResponse;
import io.shulie.takin.web.biz.service.application.MiddlewareJarService;
import io.shulie.takin.web.biz.service.application.MiddlewareSummaryService;
import io.shulie.takin.web.biz.utils.FunctionUtils;
import io.shulie.takin.web.common.constant.APIUrls;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.enums.application.ApplicationMiddlewareStatusEnum;
import io.shulie.takin.web.data.mapper.mysql.MiddlewareJarMapper;
import io.shulie.takin.web.data.model.mysql.MiddlewareJarEntity;
import io.shulie.takin.web.data.model.mysql.MiddlewareSummaryEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import static io.shulie.takin.web.biz.utils.FunctionUtils.ifBlankDone;
import static io.shulie.takin.web.common.enums.application.ApplicationMiddlewareStatusEnum.NOT_SUPPORTED;
import static io.shulie.takin.web.common.enums.application.ApplicationMiddlewareStatusEnum.NO_SUPPORT_REQUIRED;
import static io.shulie.takin.web.common.enums.application.ApplicationMiddlewareStatusEnum.SUPPORTED;
import static io.shulie.takin.web.common.enums.application.ApplicationMiddlewareStatusEnum.UNKNOWN;
import static io.shulie.takin.web.common.enums.application.ApplicationMiddlewareStatusEnum.getByCode;
import static io.shulie.takin.web.common.enums.application.ApplicationMiddlewareStatusEnum.getByDesc;

/**
 * @author liqiyu
 */
@Service
@Slf4j
public class MiddlewareJarServiceImpl extends ServiceImpl<MiddlewareJarMapper, MiddlewareJarEntity>
    implements MiddlewareJarService {
    public static final String MIDDLEWARE_MANAGE_DIR = "middleware_manage";
    @Autowired
    private MiddlewareSummaryService middlewareSummaryService;

    @Resource
    private MiddlewareJarMapper middlewareJarMapper;

    @Autowired
    private DictionaryCache dictionaryCache;

    private volatile List<String> middlewareTypeList = null;

    private List<String> getMiddlewareTypeList() {
        if (middlewareTypeList == null) {
            middlewareTypeList = Optional.ofNullable(dictionaryCache.getDicMap(null).get("MIDDLEWARE_TYPE")).orElse(
                    Collections.emptyList()).parallelStream()
                .map(EnumResult::getValue).collect(
                    Collectors.toList());
        }
        return middlewareTypeList;
    }

    /**
     * 上传文件的路径
     */
    @Value("${data.path}")
    private String uploadPath;

    private static final String COMPARE_LOCK_KEY = MiddlewareJarService.class.getSimpleName() + ":compare:%s";

    private static final String EXACT_MATCH = "100%精确匹配";
    private static final String PREFIX_TWO_MATCH = "前两位匹配到已支持版本";

    @Override
    public void appCompare(List<CompareApplicationMiddlewareDTO> compareApplicationMiddlewareList) {
        AtomicReference<List<ImportExcelVO>> importExcelVoListRef = new AtomicReference<>();
        compareApplicationMiddlewareList.parallelStream().map(compareApplicationMiddlewareDTO -> {
                final ImportExcelVO importExcelVO = new ImportExcelVO();
                importExcelVO.setAppCompareId(compareApplicationMiddlewareDTO.getId());
                importExcelVO.setArtifactId(compareApplicationMiddlewareDTO.getArtifactId());
                importExcelVO.setGroupId(compareApplicationMiddlewareDTO.getGroupId());
                importExcelVO.setVersion(compareApplicationMiddlewareDTO.getVersion());
                return importExcelVO;
            })
            // 对接收到的参数进行处理：清空不应该有的字段、去两侧空格。
            .peek(importExcelVO -> {
                importExcelVO.setName(null);
                importExcelVO.setType(null);
                importExcelVO.setRemark(null);
                importExcelVO.setStatusDesc(null);
                importExcelVO.setArtifactId(StringUtils.trimToNull(importExcelVO.getArtifactId()));
                importExcelVO.setGroupId(StringUtils.trimToNull(importExcelVO.getGroupId()));
                importExcelVO.setVersion(StringUtils.trimToNull(importExcelVO.getVersion()));
            })
            // 拿到导入数据
            .collect(Collectors.collectingAndThen(Collectors.<ImportExcelVO>toList(), list -> {
                importExcelVoListRef.set(list);
                return list;
            })).parallelStream()
            // 标记 缺少 artifactId 的情况
            .peek(item -> ifBlankDone(item.getArtifactId(), () -> item.addRemark("缺少 artifactId")))
            // 标记 缺少 version 的情况
            .peek(item -> ifBlankDone(item.getVersion(), () -> item.addRemark("缺少 version")))
            // 过滤掉 不完整的数据
            .filter(item -> item.getRemarkList().isEmpty())
            // 填充groupId
            .peek(this::fillGroupId)
            // 标记 缺少 groupId 的情况
            .peek(item -> ifBlankDone(item.getGroupId(), () -> item.addRemark("缺少 groupId")))
            // 设置 标记
            .peek(item -> item.setRemark(StringUtils.join(item.getRemarkList(), ", ")))
            // 过滤掉 不完整的数据
            .filter(item -> item.getRemarkList().isEmpty())
            // 加载明细表用来对比，组装成数组。数据结构为 [用户导入的数据,明细表的数据]
            .map(importExcelVO -> {
                final QueryWrapper<MiddlewareJarEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(MiddlewareJarEntity::getAgv,
                    joinAgv(importExcelVO.getArtifactId(), importExcelVO.getGroupId(), importExcelVO.getVersion()));
                // 因为 agv在表 t_middleware_jar 中设置了唯一主键，所以这里getOne不会有问题。
                final MiddlewareJarEntity one = this.getOne(queryWrapper);
                // 不确定查询为空的情况会不会返回null，所以对空结果做一次处理
                return new Object[] {importExcelVO, one == null ? new MiddlewareJarEntity() : one};
            })
            // 加载汇总表用来对比，组装成数组。数据结构为 [用户导入的数据,明细表的数据,汇总表的数据]
            .map(objects -> {
                ImportExcelVO importExcelVO = (ImportExcelVO)objects[0];
                final QueryWrapper<MiddlewareSummaryEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(MiddlewareSummaryEntity::getAg,
                    joinAgv(importExcelVO.getArtifactId(), importExcelVO.getGroupId()));
                // 因为 agv在表 t_middleware_summary 中设置了唯一主键，所以这里getOne不会有问题。
                final MiddlewareSummaryEntity one = middlewareSummaryService.getOne(queryWrapper);
                // 不确定查询为空的情况会不会返回null，所以对空结果做一次处理
                return new Object[] {objects[0], objects[1], one == null ? new MiddlewareSummaryEntity() : one};
            })
            // 根据查询到的信息来设置状态以及做标记
            .peek(objects -> {
                // 取出三个实体
                ImportExcelVO importExcelVO = (ImportExcelVO)objects[0];
                MiddlewareJarEntity middlewareJarEntity = (MiddlewareJarEntity)objects[1];
                MiddlewareSummaryEntity middlewareSummaryEntity = (MiddlewareSummaryEntity)objects[2];

                if (middlewareSummaryEntity.getId() != null) {
                    importExcelVO.setName(middlewareSummaryEntity.getName());
                    importExcelVO.setType(middlewareSummaryEntity.getType());
                }

                if (NO_SUPPORT_REQUIRED.getCode().equals(middlewareSummaryEntity.getStatus())) {
                    // 如果汇总信息 已经是无需支持，则设置对比的状态是无需支持。不需要往下执行了。
                    importExcelVO.setStatusDesc(NO_SUPPORT_REQUIRED.getDesc());
                    return;
                }
                // 先判断是是否是精确匹配的
                if (middlewareJarEntity.getId() != null) {
                    // 确实是精确匹配
                    importExcelVO.addRemark(EXACT_MATCH);
                    importExcelVO.setStatusDesc(getByCode(middlewareJarEntity.getStatus()).getDesc());
                } else if (middlewareSummaryEntity.getId() != null) { // 判断汇总信息有没有的,只要上报过这个中间件，汇总信息肯定会有的。
                    // 除了 已支持 外，其它的按照 汇总信息的状态来。
                    if (!SUPPORTED.getCode().equals(middlewareSummaryEntity.getStatus())) {
                        importExcelVO.setStatusDesc(getByCode(middlewareSummaryEntity.getStatus()).getDesc());
                    } else {// 汇总信息是已支持。但是没有精确匹配版本的。应该是属于未支持的状态。要进一步判断版本号是否是前两位匹配。做标记。
                        // 这种状态是 不支持的。
                        importExcelVO.setStatusDesc(NOT_SUPPORTED.getDesc());

                        // 接下来只是做个标记。
                        // 先判断version是否是合规的。被 点 分割为 三部分
                        final String[] versionSplit = StringUtils.split(importExcelVO.getVersion(), ".");
                        if (versionSplit.length == 3) {
                            // 查询根据ag查询明细表，是否有前两位、一位匹配的 已支持 的中间件
                            final QueryWrapper<MiddlewareJarEntity> queryWrapper = new QueryWrapper<>();
                            final LambdaQueryWrapper<MiddlewareJarEntity> lambda = queryWrapper.lambda();
                            lambda.eq(MiddlewareJarEntity::getArtifactId, importExcelVO.getArtifactId());
                            lambda.eq(MiddlewareJarEntity::getGroupId, importExcelVO.getGroupId());
                            lambda.eq(MiddlewareJarEntity::getStatus, SUPPORTED.getCode());
                            lambda.likeLeft(MiddlewareJarEntity::getVersion,
                                versionSplit[0] + "." + versionSplit[1] + ".");
                            final int count = this.count(queryWrapper);
                            if (count != 0) {
                                importExcelVO.addRemark(PREFIX_TWO_MATCH);
                            } else {
                                importExcelVO.addRemark("没有匹配到已支持版本");
                            }
                        } else {
                            // 不符合的 直接标注 为 无找到对应版本
                            importExcelVO.addRemark("版本不符合规范");
                        }
                    }
                } else {
                    // 第一次上报上来的这个中间件。需要添加到中间件明细表和汇总表中
                    importExcelVO.setStatusDesc(UNKNOWN.getDesc());
                    importExcelVO.addRemark("没有匹配到对应的中间件");
                }
            })
            // 设置 标记
            .peek(objects -> ((ImportExcelVO)objects[0])
                .setRemark(StringUtils.join(((ImportExcelVO)objects[0]).getRemarkList(), ", ")))
            // 状态和标记设置完成之后，除了精确匹配外，其它的都需要落库
            //.filter(objects -> !((ImportExcelVO)objects[0]).getRemarkList().contains(EXACT_MATCH))
            .filter(objects -> ((MiddlewareJarEntity)objects[1]).getId() == null)
            // 先落库明细表，如果没有汇总信息还要落库汇总信息表。
            // 对比的不存在修改，都是新增。
            // 保存明细信息
            .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                this.saveBatch(list.parallelStream().map(objects -> (ImportExcelVO)objects[0]).map(importExcelVO -> {
                        MiddlewareJarEntity middlewareJarEntity = BeanUtil.copyProperties(importExcelVO,
                            MiddlewareJarEntity.class);
                        middlewareJarEntity.setAgv(
                            joinAgv(middlewareJarEntity.getArtifactId(), middlewareJarEntity.getGroupId(),
                                middlewareJarEntity.getVersion()));
                        middlewareJarEntity.setStatus(
                            Objects.requireNonNull(getByDesc(importExcelVO.getStatusDesc())).getCode());
                        middlewareJarEntity.setGmtCreate(new Date());
                        middlewareJarEntity.setGmtUpdate(middlewareJarEntity.getGmtCreate());
                        return middlewareJarEntity;
                    }
                ).collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(MiddlewareJarEntity::getAgv))),
                    ArrayList::new)));
                return list;
            }))
            .parallelStream()
            // 转换成汇总信息
            .map(objects -> {
                ImportExcelVO importExcelVO = (ImportExcelVO)objects[0];
                MiddlewareSummaryEntity middlewareSummaryEntity = (MiddlewareSummaryEntity)objects[2];
                if (middlewareSummaryEntity.getId() == null) {
                    middlewareSummaryEntity.setName(importExcelVO.getName());
                    middlewareSummaryEntity.setType(importExcelVO.getType());
                    middlewareSummaryEntity.setArtifactId(importExcelVO.getArtifactId());
                    middlewareSummaryEntity.setGroupId(importExcelVO.getGroupId());
                    middlewareSummaryEntity.setAg(
                        joinAgv(middlewareSummaryEntity.getArtifactId(), middlewareSummaryEntity.getGroupId()));
                }
                return middlewareSummaryEntity;
            })
            // 汇总信息会有重复的
            .collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(MiddlewareSummaryEntity::getAg))),
                ArrayList::new))
            // 为了事物生效，所以没有用并发模式。
            .stream()
            .peek(this::reCompute).collect(
                Collectors.collectingAndThen(Collectors.toList(), middlewareSummaryService::saveOrUpdateBatch
                ));
        compareApplicationMiddlewareList.clear();
        compareApplicationMiddlewareList.addAll(
            importExcelVoListRef.get().parallelStream().map(importExcelVO -> {
                    final CompareApplicationMiddlewareDTO compareApplicationMiddlewareDTO = BeanUtil.copyProperties(
                        importExcelVO, CompareApplicationMiddlewareDTO.class);
                    compareApplicationMiddlewareDTO.setId(importExcelVO.getAppCompareId());
                    if (StringUtils.isNotBlank(importExcelVO.getStatusDesc())) {
                        final ApplicationMiddlewareStatusEnum byDesc = getByDesc(importExcelVO.getStatusDesc());
                        if (byDesc != null) {
                            compareApplicationMiddlewareDTO.setStatus(byDesc.getCode());
                        }
                    }
                    return compareApplicationMiddlewareDTO;
                })
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public MiddlewareCompareResponse compareMj(List<MultipartFile> files) {
        AtomicReference<List<ImportExcelVO>> importExcelVoListRef = new AtomicReference<>();
        files.parallelStream()
            // 从excel中读取数据到多个list
            .map(this::readDataFromFile)
            // 把多个list合并成单个list
            .flatMap(List::parallelStream)
            // 去除完全重复的数据
            .distinct()
            // 对接收到的参数进行处理：清空不应该有的字段、去两侧空格。
            .peek(importExcelVO -> {
                importExcelVO.setName(null);
                importExcelVO.setType(null);
                importExcelVO.setRemark(null);
                importExcelVO.setStatusDesc(null);
                importExcelVO.setArtifactId(StringUtils.isBlank(importExcelVO.getArtifactId()) ? null
                    : StringUtils.trimToNull(importExcelVO.getArtifactId()));
                importExcelVO.setGroupId(StringUtils.isBlank(importExcelVO.getGroupId()) ? null
                    : StringUtils.trimToNull(importExcelVO.getGroupId()));
                importExcelVO.setVersion(StringUtils.isBlank(importExcelVO.getVersion()) ? null
                    : StringUtils.trimToNull(importExcelVO.getVersion()));
            })
            .filter(importExcelVO -> importExcelVO.getArtifactId() != null || importExcelVO.getGroupId() != null
                || importExcelVO.getVersion() != null)
            // 拿到导入数据
            .collect(Collectors.collectingAndThen(Collectors.<ImportExcelVO>toList(), list -> {
                importExcelVoListRef.set(list);
                return list;
            })).parallelStream()
            // 标记 缺少 artifactId 的情况
            .peek(item -> ifBlankDone(item.getArtifactId(), () -> item.addRemark("缺少 artifactId")))
            // 标记 缺少 version 的情况
            .peek(item -> ifBlankDone(item.getVersion(), () -> item.addRemark("缺少 version")))
            // 过滤掉 不完整的数据
            .filter(item -> item.getRemarkList().isEmpty())
            // 填充groupId
            .peek(this::fillGroupId)
            // 标记 缺少 groupId 的情况
            .peek(item -> ifBlankDone(item.getGroupId(), () -> item.addRemark("缺少 groupId")))
            // 设置 标记
            .peek(item -> item.setRemark(StringUtils.join(item.getRemarkList(), ", ")))
            // 过滤掉 不完整的数据
            .filter(item -> item.getRemarkList().isEmpty())
            // 加载明细表用来对比，组装成数组。数据结构为 [用户导入的数据,明细表的数据]
            .map(importExcelVO -> {
                final QueryWrapper<MiddlewareJarEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(MiddlewareJarEntity::getAgv,
                    joinAgv(importExcelVO.getArtifactId(), importExcelVO.getGroupId(), importExcelVO.getVersion()));
                // 因为 agv在表 t_middleware_jar 中设置了唯一主键，所以这里getOne不会有问题。
                final MiddlewareJarEntity one = this.getOne(queryWrapper);
                // 不确定查询为空的情况会不会返回null，所以对空结果做一次处理
                return new Object[] {importExcelVO, one == null ? new MiddlewareJarEntity() : one};
            })
            // 加载汇总表用来对比，组装成数组。数据结构为 [用户导入的数据,明细表的数据,汇总表的数据]
            .map(objects -> {
                ImportExcelVO importExcelVO = (ImportExcelVO)objects[0];
                final QueryWrapper<MiddlewareSummaryEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(MiddlewareSummaryEntity::getAg,
                    joinAgv(importExcelVO.getArtifactId(), importExcelVO.getGroupId()));
                // 因为 agv在表 t_middleware_summary 中设置了唯一主键，所以这里getOne不会有问题。
                final MiddlewareSummaryEntity one = middlewareSummaryService.getOne(queryWrapper);
                // 不确定查询为空的情况会不会返回null，所以对空结果做一次处理
                return new Object[] {objects[0], objects[1], one == null ? new MiddlewareSummaryEntity() : one};
            })
            // 根据查询到的信息来设置状态以及做标记
            .peek(objects -> {
                // 取出三个实体
                ImportExcelVO importExcelVO = (ImportExcelVO)objects[0];
                MiddlewareJarEntity middlewareJarEntity = (MiddlewareJarEntity)objects[1];
                MiddlewareSummaryEntity middlewareSummaryEntity = (MiddlewareSummaryEntity)objects[2];

                if (middlewareSummaryEntity.getId() != null) {
                    importExcelVO.setName(middlewareSummaryEntity.getName());
                    importExcelVO.setType(middlewareSummaryEntity.getType());
                }

                if (NO_SUPPORT_REQUIRED.getCode().equals(middlewareSummaryEntity.getStatus())) {
                    // 如果汇总信息 已经是无需支持，则设置对比的状态是无需支持。不需要往下执行了。
                    importExcelVO.setStatusDesc(NO_SUPPORT_REQUIRED.getDesc());
                    return;
                }
                // 先判断是是否是精确匹配的
                if (middlewareJarEntity.getId() != null) {
                    // 确实是精确匹配
                    importExcelVO.addRemark(EXACT_MATCH);
                    importExcelVO.setStatusDesc(getByCode(middlewareJarEntity.getStatus()).getDesc());
                } else if (middlewareSummaryEntity.getId() != null) { // 判断汇总信息有没有的,只要上报过这个中间件，汇总信息肯定会有的。
                    // 除了 已支持 外，其它的按照 汇总信息的状态来。
                    if (!SUPPORTED.getCode().equals(middlewareSummaryEntity.getStatus())) {
                        importExcelVO.setStatusDesc(getByCode(middlewareSummaryEntity.getStatus()).getDesc());
                    } else {// 汇总信息是已支持。但是没有精确匹配版本的。应该是属于未支持的状态。要进一步判断版本号是否是前两位匹配。做标记。
                        // 这种状态是 不支持的。
                        importExcelVO.setStatusDesc(NOT_SUPPORTED.getDesc());

                        // 接下来只是做个标记。
                        // 先判断version是否是合规的。被 点 分割为 三部分
                        final String[] versionSplit = StringUtils.split(importExcelVO.getVersion(), ".");
                        if (versionSplit.length == 3) {
                            // 查询根据ag查询明细表，是否有前两位、一位匹配的 已支持 的中间件
                            final QueryWrapper<MiddlewareJarEntity> queryWrapper = new QueryWrapper<>();
                            final LambdaQueryWrapper<MiddlewareJarEntity> lambda = queryWrapper.lambda();
                            lambda.eq(MiddlewareJarEntity::getArtifactId, importExcelVO.getArtifactId());
                            lambda.eq(MiddlewareJarEntity::getGroupId, importExcelVO.getGroupId());
                            lambda.eq(MiddlewareJarEntity::getStatus, SUPPORTED.getCode());
                            lambda.likeLeft(MiddlewareJarEntity::getVersion,
                                versionSplit[0] + "." + versionSplit[1] + ".");
                            final int count = this.count(queryWrapper);
                            if (count != 0) {
                                importExcelVO.addRemark(PREFIX_TWO_MATCH);
                            } else {
                                importExcelVO.addRemark("没有匹配到已支持版本");
                            }
                        } else {
                            // 不符合的 直接标注 为 无找到对应版本
                            importExcelVO.addRemark("版本不符合规范");
                        }
                    }
                } else {
                    // 第一次上报上来的这个中间件。需要添加到中间件明细表和汇总表中
                    importExcelVO.setStatusDesc(UNKNOWN.getDesc());
                    importExcelVO.addRemark("没有匹配到对应的中间件");
                }
            })
            // 设置 标记
            .peek(objects -> ((ImportExcelVO)objects[0])
                .setRemark(StringUtils.join(((ImportExcelVO)objects[0]).getRemarkList(), ", ")))
            // 状态和标记设置完成之后，除了精确匹配外，其它的都需要落库
            //.filter(objects -> !((ImportExcelVO)objects[0]).getRemarkList().contains(EXACT_MATCH))
            .filter(objects -> ((MiddlewareJarEntity)objects[1]).getId() == null)
            // 先落库明细表，如果没有汇总信息还要落库汇总信息表。
            // 对比的不存在修改，都是新增。
            // 保存明细信息
            .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                this.saveBatch(list.parallelStream().map(objects -> (ImportExcelVO)objects[0]).map(importExcelVO -> {
                        MiddlewareJarEntity middlewareJarEntity = BeanUtil.copyProperties(importExcelVO,
                            MiddlewareJarEntity.class);
                        middlewareJarEntity.setAgv(
                            joinAgv(middlewareJarEntity.getArtifactId(), middlewareJarEntity.getGroupId(),
                                middlewareJarEntity.getVersion()));
                        middlewareJarEntity.setStatus(
                            Objects.requireNonNull(getByDesc(importExcelVO.getStatusDesc())).getCode());
                        middlewareJarEntity.setGmtCreate(new Date());
                        middlewareJarEntity.setGmtUpdate(middlewareJarEntity.getGmtCreate());
                        return middlewareJarEntity;
                    }
                ).collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(MiddlewareJarEntity::getAgv))),
                    ArrayList::new)));
                return list;
            }))
            .parallelStream()
            // 转换成汇总信息
            .map(objects -> {
                ImportExcelVO importExcelVO = (ImportExcelVO)objects[0];
                MiddlewareSummaryEntity middlewareSummaryEntity = (MiddlewareSummaryEntity)objects[2];
                if (middlewareSummaryEntity.getId() == null) {
                    middlewareSummaryEntity.setName(importExcelVO.getName());
                    middlewareSummaryEntity.setType(importExcelVO.getType());
                    middlewareSummaryEntity.setArtifactId(importExcelVO.getArtifactId());
                    middlewareSummaryEntity.setGroupId(importExcelVO.getGroupId());
                    middlewareSummaryEntity.setAg(
                        joinAgv(middlewareSummaryEntity.getArtifactId(), middlewareSummaryEntity.getGroupId()));
                }
                return middlewareSummaryEntity;
            })
            // 去重复、批量落库
            .collect(Collectors.collectingAndThen(Collectors
                    .toCollection(() -> new TreeSet<>(Comparator.comparing(MiddlewareSummaryEntity::getAg))),
                ArrayList::new))
            // 为了事物生效，所以没有用并发模式。
            .stream()
            .peek(this::reCompute).collect(Collectors.collectingAndThen(Collectors.toList(),
                middlewareSummaryService::saveOrUpdateBatch
            ));
        List<ImportExcelVO> importExcelVOList = importExcelVoListRef.get();

        final String filePath = uploadPath + (uploadPath.endsWith(File.separator) ? "" : File.separator)
            + MIDDLEWARE_MANAGE_DIR;
        final File pathFile = new File(filePath);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        final String fileUrl = UUID.fastUUID().toString(Boolean.TRUE) + "." + "xlsx";
        String fileName = filePath + File.separator + fileUrl;
        final MiddlewareCompareResponse middlewareCompareResponse = MiddlewareCompareResponse.builder()
            // 统计全部数据
            .total(importExcelVOList.size())
            // 统计不支持数据
            .notSupported(importExcelVOList.parallelStream()
                .filter(importExcelVO -> NOT_SUPPORTED.getDesc().equals(importExcelVO.getStatusDesc())).count())
            // 统计未知数据
            .unknown(importExcelVOList.parallelStream()
                .filter(importExcelVO -> UNKNOWN.getDesc().equals(importExcelVO.getStatusDesc())).count())
            // 统计已支持数据
            .supported(importExcelVOList.parallelStream()
                .filter(importExcelVO -> SUPPORTED.getDesc().equals(importExcelVO.getStatusDesc())).count())
            // 统计有问题的数据
            .fail(importExcelVOList.parallelStream().filter(importExcelVO -> importExcelVO.getStatusDesc() == null)
                .count())
            // 设置下载url
            .url("/" + APIUrls.MIDDLEWARE_JAR + "/file/" + fileUrl)
            .build();

        if (CollectionUtils.isEmpty(importExcelVOList)) {
            return middlewareCompareResponse;
        }
        final Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), ImportExcelVO.class,
            importExcelVOList);

        try (final FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            workbook.write(fileOutputStream);
        } catch (IOException e) {
            log.error("将excel写入磁盘报错", e);
        }
        return middlewareCompareResponse;
    }

    @Override
    public void reCompute(MiddlewareSummaryEntity middlewareSummaryEntity) {
        middlewareSummaryEntity.setGmtUpdate(new Date());
        MiddlewareSummaryEntity compute = Optional.ofNullable(
            middlewareJarMapper.computeNum(middlewareSummaryEntity)).orElseGet(() -> {
            log.error("不应该查询不到。应该是事物导致的。");
            final MiddlewareSummaryEntity ifNullObject = new MiddlewareSummaryEntity();
            ifNullObject.setTotalNum(0L);
            ifNullObject.setUnknownNum(0L);
            ifNullObject.setNotSupportedNum(0L);
            ifNullObject.setSupportedNum(0L);
            return ifNullObject;
        });
        middlewareSummaryEntity.setTotalNum(compute.getTotalNum());
        middlewareSummaryEntity.setNotSupportedNum(compute.getNotSupportedNum());
        middlewareSummaryEntity.setUnknownNum(compute.getUnknownNum());
        middlewareSummaryEntity.setSupportedNum(compute.getSupportedNum());
        if (middlewareSummaryEntity.getId() == null) {
            middlewareSummaryEntity.setGmtCreate(new Date());
            middlewareSummaryEntity.setGmtUpdate(middlewareSummaryEntity.getGmtCreate());
            if (middlewareSummaryEntity.getStatus() == null) {
                middlewareSummaryEntity.setStatus(UNKNOWN.getCode());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void edit(MiddlewareJarEntity middlewareJarEntity) {
        final MiddlewareJarEntity before = this.getById(middlewareJarEntity.getId());
        middlewareJarEntity.setGmtUpdate(new Date());
        this.updateById(middlewareJarEntity);
        OperationLogContextHolder.operationType(OpTypes.UPDATE);
        OperationLogContextHolder.addVars("beforeJSON", JSON.toJSONString(before));
        OperationLogContextHolder.addVars("afterJSON", JSON.toJSONString(middlewareJarEntity));
        if (before.getStatus().equals(middlewareJarEntity.getStatus())) {
            return;
        }
        final QueryWrapper<MiddlewareSummaryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MiddlewareSummaryEntity::getAg,
            joinAgv(middlewareJarEntity.getArtifactId(), middlewareJarEntity.getGroupId()));
        final MiddlewareSummaryEntity one = middlewareSummaryService.getOne(queryWrapper);
        Assert.isTrue(one != null, "状态异常");
        reCompute(one);
        middlewareSummaryService.updateById(one);
    }

    private String joinAgv(String... agv) {
        return Strings.join(Arrays.stream(agv).map(item -> item == null ? "" : item).collect(Collectors.toList()), '_');
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public MiddlewareImportResponse importMj(MultipartFile file) {

        AtomicReference<List<ImportExcelVO>> importExcelVoListRef = new AtomicReference<>();
        Arrays.stream(new MultipartFile[] {file})
            // 从excel中读取数据到多个list
            .map(this::readDataFromFile)
            // 把多个list合并成单个list
            .flatMap(List::parallelStream)
            // 对接收到的参数进行处理：去两侧空格。
            .peek(importExcelVO -> {
                importExcelVO.setArtifactId(StringUtils.trimToNull(importExcelVO.getArtifactId()));
                importExcelVO.setGroupId(StringUtils.trimToNull(importExcelVO.getGroupId()));
                importExcelVO.setVersion(StringUtils.trimToNull(importExcelVO.getVersion()));
                importExcelVO.setName(StringUtils.trimToNull(importExcelVO.getName()));
                importExcelVO.setType(StringUtils.trimToNull(importExcelVO.getType()));
                importExcelVO.setStatusDesc(StringUtils.trimToNull(importExcelVO.getStatusDesc()));
            })
            .filter(importExcelVO -> importExcelVO.getArtifactId() != null || importExcelVO.getGroupId() != null
                || importExcelVO.getVersion() != null || importExcelVO.getName() != null
                || importExcelVO.getType() != null || importExcelVO.getStatusDesc() != null)
            // 拿到导入数据
            .collect(Collectors.collectingAndThen(Collectors.<ImportExcelVO>toList(), list -> {
                importExcelVoListRef.set(list);

                // 标记同一个文件中同一中间件字段不一致的问题
                list.parallelStream().collect(Collectors
                    .groupingBy(importExcelVO -> joinAgv(importExcelVO.getArtifactId(), importExcelVO.getGroupId()),
                        Collectors.toList())).values().parallelStream().forEach(importExcelVOList -> {
                    if (importExcelVOList.parallelStream().anyMatch(
                        importExcelVO -> NO_SUPPORT_REQUIRED.getDesc().equals(importExcelVO.getStatusDesc())) &&
                        importExcelVOList.parallelStream().anyMatch(
                            importExcelVO -> !NO_SUPPORT_REQUIRED.getDesc().equals(importExcelVO.getStatusDesc()))) {
                        importExcelVOList.parallelStream().forEach(
                            importExcelVO -> importExcelVO.addRemark("同一中间件对比其它行存在无需支持和其它状态共存的问题"));
                    }
                    if (importExcelVOList.parallelStream().map(ImportExcelVO::getName).distinct().count() != 1) {
                        importExcelVOList.parallelStream().forEach(
                            importExcelVO -> importExcelVO.addRemark("同一中间件对比其它行存在中间件名字不一致"));
                    }
                    if (importExcelVOList.parallelStream().map(ImportExcelVO::getType).distinct().count() != 1) {
                        importExcelVOList.parallelStream().forEach(
                            importExcelVO -> importExcelVO.addRemark("和同一中间件对比其它行存在中间件类型不一致"));
                    }
                });
                return list;
            })).parallelStream()
            // 标记 缺少 artifactId 的情况
            .peek(item -> ifBlankDone(item.getArtifactId(), () -> item.addRemark("缺少 artifactId")))
            // 标记 缺少 groupId 的情况
            .peek(item -> ifBlankDone(item.getGroupId(), () -> item.addRemark("缺少 groupId")))
            // 标记 缺少 version 的情况
            .peek(item -> ifBlankDone(item.getVersion(), () -> item.addRemark("缺少 version")))
            // 标记 缺少 中间件状态
            .peek(item -> ifBlankDone(item.getStatusDesc(), () -> item.addRemark("缺少 中间件状态")))
            .peek(item -> FunctionUtils
                .ifDone(getByDesc(item.getStatusDesc()) == null, () -> item.addRemark("中间件状态 不正确")))
            .peek(item -> ifBlankDone(item.getName(), () -> item.addRemark("缺少 中间件名称")))
            .peek(item -> ifBlankDone(item.getType(), () -> item.addRemark("缺少 中间件类型")))
            .peek(item -> FunctionUtils
                .ifDone(!getMiddlewareTypeList().contains(item.getType()), () -> item.addRemark("中间件类型不在预设的范围内")))
            // 设置 标记
            .peek(item -> item.setRemark(StringUtils.join(item.getRemarkList(), ", ")))
            // 过滤掉 不完整的数据
            .filter(item -> item.getRemarkList().isEmpty())
            // 加载明细表用来对比，组装成数组。数据结构为 [用户导入的数据,明细表的数据]
            .map(importExcelVO -> {
                final QueryWrapper<MiddlewareJarEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(MiddlewareJarEntity::getAgv,
                    joinAgv(importExcelVO.getArtifactId(), importExcelVO.getGroupId(), importExcelVO.getVersion()));
                // 因为 agv在表 t_middleware_jar 中设置了唯一主键，所以这里getOne不会有问题。
                final MiddlewareJarEntity one = this.getOne(queryWrapper);
                // 不确定查询为空的情况会不会返回null，所以对空结果做一次处理
                return new Object[] {importExcelVO, one == null ? new MiddlewareJarEntity() : one};
            })
            // 加载汇总表用来对比，组装成数组。数据结构为 [用户导入的数据,明细表的数据,汇总表的数据]
            .map(objects -> {
                ImportExcelVO importExcelVO = (ImportExcelVO)objects[0];
                final QueryWrapper<MiddlewareSummaryEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(MiddlewareSummaryEntity::getAg,
                    joinAgv(importExcelVO.getArtifactId(), importExcelVO.getGroupId()));
                // 因为 agv在表 t_middleware_summary 中设置了唯一主键，所以这里getOne不会有问题。
                final MiddlewareSummaryEntity one = middlewareSummaryService.getOne(queryWrapper);
                // 不确定查询为空的情况会不会返回null，所以对空结果做一次处理
                return new Object[] {objects[0], objects[1], one == null ? new MiddlewareSummaryEntity() : one};
            })
            // 根据查询到的信息来设置状态以及做标记
            .peek(objects -> {
                // 取出三个实体
                ImportExcelVO importExcelVO = (ImportExcelVO)objects[0];
                MiddlewareJarEntity middlewareJarEntity = (MiddlewareJarEntity)objects[1];
                MiddlewareSummaryEntity middlewareSummaryEntity = (MiddlewareSummaryEntity)objects[2];

                if (middlewareJarEntity.getId() == null) {
                    BeanUtil.copyProperties(importExcelVO, middlewareJarEntity);
                    middlewareJarEntity.setAgv(
                        joinAgv(middlewareJarEntity.getArtifactId(), middlewareJarEntity.getGroupId(),
                            middlewareJarEntity.getVersion()));
                    middlewareJarEntity.setGmtCreate(new Date());
                    middlewareJarEntity.setGmtUpdate(middlewareJarEntity.getGmtCreate());
                } else {
                    middlewareJarEntity.setName(importExcelVO.getName());
                    middlewareJarEntity.setType(importExcelVO.getType());
                    middlewareJarEntity.setGmtUpdate(new Date());
                }
                middlewareJarEntity.setStatus(
                    Objects.requireNonNull(getByDesc(importExcelVO.getStatusDesc()), "中间件状态不能为空").getCode());

                // 检测数据是否合法
                if (middlewareSummaryEntity.getId() != null) {
                    if (StringUtils.isNotBlank(middlewareSummaryEntity.getName()) &&
                        !StringUtils.equals(middlewareJarEntity.getName(), middlewareSummaryEntity.getName())) {
                        importExcelVO.addRemark("跟已有的中间件名称不匹配");
                    }
                    if (StringUtils.isNotBlank(middlewareSummaryEntity.getType()) &&
                        !StringUtils.equals(middlewareJarEntity.getType(), middlewareSummaryEntity.getType())) {
                        importExcelVO.addRemark("跟已有的中间件类型不匹配");
                    }
                    if ((middlewareJarEntity.getStatus().equals(NO_SUPPORT_REQUIRED.getCode())
                        || importExcelVO.getStatusDesc().equals(NO_SUPPORT_REQUIRED.getDesc()))
                        && !middlewareJarEntity.getStatus().equals(
                        Objects.requireNonNull(getByDesc(importExcelVO.getStatusDesc()), "中间件状态不能为空").getCode())) {
                        importExcelVO.addRemark("无需支持和其它状态不能共存");
                    }
                }
            })
            // 状态和标记设置完成之后，除了精确匹配外，其它的都需要落库
            // 设置 标记
            .peek(objects -> ((ImportExcelVO)objects[0])
                .setRemark(StringUtils.join(((ImportExcelVO)objects[0]).getRemarkList(), ", ")))
            // 过滤掉 不合法数据
            .filter(objects -> ((ImportExcelVO)objects[0]).getRemarkList().isEmpty())
            .peek(objects -> ((ImportExcelVO)objects[0]).setRemark("导入成功"))
            // 先落库明细表，如果没有汇总信息还要落库汇总信息表。
            // 对比的不存在修改，都是新增。
            // 保存明细信息
            .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                this.saveOrUpdateBatch(
                    // 去重赋
                    list.parallelStream().map(objects -> (MiddlewareJarEntity)objects[1])
                        .collect(Collectors.collectingAndThen(Collectors
                                .toCollection(() -> new TreeSet<>(Comparator.comparing(MiddlewareJarEntity::getAgv))),
                            ArrayList::new)));
                return list;
            }))
            .parallelStream()
            // 转换成汇总信息
            .map(objects -> {
                ImportExcelVO importExcelVO = (ImportExcelVO)objects[0];
                MiddlewareSummaryEntity middlewareSummaryEntity = (MiddlewareSummaryEntity)objects[2];
                if (middlewareSummaryEntity.getId() == null) {
                    middlewareSummaryEntity.setType(importExcelVO.getType());
                    middlewareSummaryEntity.setName(importExcelVO.getName());
                    middlewareSummaryEntity.setArtifactId(importExcelVO.getArtifactId());
                    middlewareSummaryEntity.setGroupId(importExcelVO.getGroupId());
                    middlewareSummaryEntity.setAg(
                        joinAgv(middlewareSummaryEntity.getArtifactId(), middlewareSummaryEntity.getGroupId()));
                    middlewareSummaryEntity.setStatus(
                        Objects.requireNonNull(getByDesc(importExcelVO.getStatusDesc()), "中间件状态不能为空").getCode());
                } else {
                    // 因为agnet上报上来的 name和type是空的。但是导入的不可能为空。所以这里做一下同一的填充。
                    middlewareSummaryEntity.setName(importExcelVO.getName());
                    middlewareSummaryEntity.setType(importExcelVO.getType());
                }
                return middlewareSummaryEntity;
            })
            // 汇总信息会有重复的
            .distinct()
            // 批量落库
            .collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(MiddlewareSummaryEntity::getAg))),
                ArrayList::new))
            // 为了事物生效，所以没有用并发模式。
            .stream()
            .peek(this::reCompute)
            .peek(middlewareSummaryEntity -> {
                // 修改所有的 type和name
                final UpdateWrapper<MiddlewareJarEntity> updateWrapper = new UpdateWrapper<>();
                final LambdaUpdateWrapper<MiddlewareJarEntity> middlewareJarEntityLambdaUpdateWrapper = updateWrapper.lambda();
                middlewareJarEntityLambdaUpdateWrapper.set(MiddlewareJarEntity::getType, middlewareSummaryEntity.getType());
                middlewareJarEntityLambdaUpdateWrapper.set(MiddlewareJarEntity::getName, middlewareSummaryEntity.getName());
                middlewareJarEntityLambdaUpdateWrapper.set(MiddlewareJarEntity::getGmtUpdate, new Date());
                middlewareJarEntityLambdaUpdateWrapper.eq(MiddlewareJarEntity::getArtifactId, middlewareSummaryEntity.getArtifactId());
                middlewareJarEntityLambdaUpdateWrapper.eq(MiddlewareJarEntity::getGroupId, middlewareSummaryEntity.getGroupId());
                this.update(updateWrapper);
            })
            .collect(
                Collectors.collectingAndThen(Collectors.toList(), middlewareSummaryService::saveOrUpdateBatch
                ));
        final List<ImportExcelVO> importExcelVOList = importExcelVoListRef.get();

        final String filePath = uploadPath + (uploadPath.endsWith(File.separator) ? "" : File.separator)
            + MIDDLEWARE_MANAGE_DIR;
        final File pathFile = new File(filePath);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String fileUrl = UUID.fastUUID().toString(Boolean.TRUE) + "." + "xlsx";
        String fileName = filePath + File.separator + fileUrl;
        final long failNum = importExcelVOList.parallelStream()
            .filter(importExcelVO -> !importExcelVO.getRemarkList().isEmpty()).count();
        final MiddlewareImportResponse middlewareCompareResponse = MiddlewareImportResponse.builder()
            // 统计全部数据
            .total(importExcelVOList.size())
            // 统计失败数据
            .fail(failNum)
            .success(importExcelVOList.size() - failNum)
            // 设置下载url
            .url("/" + APIUrls.MIDDLEWARE_JAR + "/file/" + fileUrl)
            .build();


        final Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), ImportExcelVO.class,
            importExcelVOList);

        try (final FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            workbook.write(fileOutputStream);
        } catch (IOException e) {
            log.error("将excel写入磁盘报错", e);
        }
        return middlewareCompareResponse;
    }

    private List<ImportExcelVO> readDataFromFile(MultipartFile file) {
        List<ImportExcelVO> importExcelVOList = Collections.emptyList();
        try (InputStream inputStream = file.getInputStream()) {
            importExcelVOList = ExcelImportUtil.importExcel(inputStream, ImportExcelVO.class,
                new ImportParams());
        } catch (Exception e) {
            log.error("读取文件出错。", e);
        }

        if (importExcelVOList.parallelStream().map(ImportExcelVO::getVersion).allMatch(StringUtils::isBlank)) {
            try (InputStream inputStream = file.getInputStream()) {
                List<ImportExcelFixVO> importExcelFixVOList = ExcelImportUtil.importExcel(inputStream,
                    ImportExcelFixVO.class,
                    new ImportParams());
                importExcelVOList = importExcelFixVOList.parallelStream().map(
                    importExcelFixVO -> BeanUtil.copyProperties(importExcelFixVO, ImportExcelVO.class)).collect(
                    Collectors.toList());
            } catch (Exception e) {
                log.error("读取文件出错。", e);
            }
        }
        return importExcelVOList;
    }

    private static final String SEARCH_MAVEN_URL_ALIYUN
        = "https://maven.aliyun.com/artifact/aliyunMaven/searchArtifactByGav?groupId=&artifactId=%s&version=%s&repoId"
        + "=all&_input_charset=utf-8";
    private static final String[] BAD_PREFIX_TOKEN_ALIYUN = new String[] {"...", ";.", "."};

    /**
     * @param middlewareJarResult 需要填充的中间件信息
     */
    private void fillGroupId(ImportExcelVO middlewareJarResult) {
        if (StringUtils.isNotBlank(middlewareJarResult.getGroupId())) {
            return;
        }
        String groupId = searchGroupIdFromAliYun(middlewareJarResult.getArtifactId(),
            middlewareJarResult.getVersion());
        //if (StrUtil.isBlank(groupId)) {
        //    groupId = searchGroupIdFromOrg(middlewareJarResult.getArtifactId(), middlewareJarResult.getVersion());
        //}
        middlewareJarResult.setGroupId(groupId);
    }

    /**
     * @param artifactId artifactId
     * @param version    version
     * @return 经过查找并去除部分错误信息的groupId
     */
    private String searchGroupIdFromAliYun(String artifactId, String version) {
        String getGroupId = null;
        try {
            String responseString;
            responseString = HttpUtil.get(String.format(SEARCH_MAVEN_URL_ALIYUN, artifactId, version), 10 * 1000);
            final JSONObject jsonObject = JSONUtil.parseObj(responseString);
            if (jsonObject.getBool("successful")) {
                final JSONArray object = jsonObject.getJSONArray("object");
                if (object == null || object.size() == 0) {
                    log.info("searchGroupIdFromAliYun: search empty result,artifactId:{},response:{}", artifactId,
                        responseString);
                    return null;
                }
                for (int j = 0; j < object.size(); j++) {
                    getGroupId = object.get(j, JSONObject.class).getStr("groupId");
                    // drop bad groupId
                    if (getGroupId == null || getGroupId.contains("#") || getGroupId.contains("%")) {
                        continue;
                    }
                    // fix bad groupId
                    for (String badPrefixToken : BAD_PREFIX_TOKEN_ALIYUN) {
                        if (getGroupId.startsWith(badPrefixToken)) {
                            getGroupId = getGroupId.substring(badPrefixToken.length());
                            break;
                        }
                    }
                    break;
                }
            } else {
                log.info("searchGroupIdFromAliYun: search empty fail,artifactId:{},response:{}", artifactId,
                    responseString);
            }
        } catch (Exception e) {
            log.error(String.format("searchGroupIdFromAliYun: artifactId:%s,fillGroupId exception", artifactId), e);
        }
        return getGroupId;
    }

    private static final String SEARCH_MAVEN_URL_ORG
        = "https://search.maven.org/solrsearch/select?q=a:%s%%20AND%%20v:%s&start=0&rows=20";

    /**
     * @param artifactId artifactId
     * @param version    version
     * @return 查找到的groupId
     */
    private String searchGroupIdFromOrg(String artifactId, String version) {
        String getGroupId = null;
        try {
            String responseString;
            responseString = HttpUtil.get(String.format(SEARCH_MAVEN_URL_ORG, artifactId, version), 10 * 1000);
            final JSONObject jsonObject = JSONUtil.parseObj(responseString);
            final JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("docs");
            if (jsonArray == null || jsonArray.size() == 0) {
                log.info("searchGroupIdFromOrg: search empty result,artifactId:{},response:{}", artifactId,
                    responseString);
                return null;
            }
            getGroupId = jsonArray.getJSONObject(0).getStr("g");
        } catch (Exception e) {
            log.error(String.format("searchGroupIdFromOrg: artifactId:%s,fillGroupId exception", artifactId), e);
        }
        return getGroupId;
    }

}
