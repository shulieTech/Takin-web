package io.shulie.takin.web.entrypoint.controller.application;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pamirs.takin.entity.domain.vo.middleware.MiddlewareSummaryExportVO;
import io.shulie.takin.cloud.common.enums.middleware.MiddlewareJarStatusEnum;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.web.common.annocation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.Message;
import io.shulie.takin.web.biz.constant.BizOpConstants.Modules;
import io.shulie.takin.web.biz.constant.BizOpConstants.OpTypes;
import io.shulie.takin.web.biz.constant.BizOpConstants.SubModules;
import io.shulie.takin.web.biz.pojo.request.application.MiddlewareSummaryRequest;
import io.shulie.takin.web.biz.pojo.response.application.MiddlewareSummaryResponse;
import io.shulie.takin.web.biz.service.application.MiddlewareSummaryService;
import io.shulie.takin.web.biz.utils.PageUtils;
import io.shulie.takin.web.common.constant.APIUrls;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.enums.application.ApplicationMiddlewareStatusEnum;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.shulie.takin.web.data.model.mysql.MiddlewareSummaryEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 中间件汇总信息的导入和对比
 *
 * @author liqiyu
 */
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL + "application/middlewareSummary")
@Api(tags = "接口: 中间件汇总信息")
public class MiddlewareSummaryController {

    @Autowired
    private MiddlewareSummaryService middlewareSummaryService;


    @ApiOperation(("|_ 汇总信息"))
    @GetMapping("summary")
    public Object summary() {
        final int totalNum = middlewareSummaryService.count();
        final QueryWrapper<MiddlewareSummaryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MiddlewareSummaryEntity::getStatus, MiddlewareJarStatusEnum.SUPPORTED.getCode());
        final int supportedNum = middlewareSummaryService.count(queryWrapper);
        queryWrapper.clear();
        queryWrapper.lambda().eq(MiddlewareSummaryEntity::getStatus, MiddlewareJarStatusEnum.TO_BE_SUPPORTED.getCode());
        final int notSupportedNum = middlewareSummaryService.count(queryWrapper);
        queryWrapper.clear();
        queryWrapper.lambda().eq(MiddlewareSummaryEntity::getStatus, MiddlewareJarStatusEnum.TO_BE_VERIFIED.getCode());
        final int unknownNum = middlewareSummaryService.count(queryWrapper);
        queryWrapper.clear();
        queryWrapper.lambda().eq(MiddlewareSummaryEntity::getStatus, MiddlewareJarStatusEnum.NO_REQUIRED.getCode());
        final int noRequiredNum = middlewareSummaryService.count(queryWrapper);
        final HashMap<String, Integer> resultMap = new HashMap<>();
        resultMap.put("totalNum", totalNum);
        resultMap.put("supportedNum", supportedNum);
        resultMap.put("notSupportedNum", notSupportedNum);
        resultMap.put("unknownNum", unknownNum);
        resultMap.put("noRequiredNum", noRequiredNum);
        return resultMap;
    }

    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.MIDDLEWARE,
        needAuth = ActionTypeEnum.QUERY
    )
    @ApiOperation("|_ 分页查询")
    @GetMapping("list")
    public PagingList<MiddlewareSummaryResponse> list(
        @ApiParam(name = "current", value = "页码", required = true) Integer current,
        @ApiParam(name = "pageSize", value = "页大小", required = true) Integer pageSize,
        @ApiParam(name = "q", value = "搜索内容") String q,
        @ApiParam(name = "status", value = "状态") Integer status
    ) {
        PageUtils.clearPageHelper();
        final QueryWrapper<MiddlewareSummaryEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(q)) {
            queryWrapper.lambda().and(
                middlewareSummaryEntityLambdaQueryWrapper -> middlewareSummaryEntityLambdaQueryWrapper.like(
                    MiddlewareSummaryEntity::getArtifactId, q).or().like(
                    MiddlewareSummaryEntity::getGroupId, q));
        }
        if (null != status) {
            final ApplicationMiddlewareStatusEnum byCode = ApplicationMiddlewareStatusEnum.getByCode(status);
            org.springframework.util.Assert.isTrue(byCode != null, String.format("没有找到对应状态：%s", status));
            queryWrapper.lambda().eq(MiddlewareSummaryEntity::getStatus, status);
        }
        queryWrapper.lambda().last(
            " order by case when status = 4 then 9 when status =2 then 8 when status = 1 then 7 when status = 3 then "
                + "3 else status end desc, id desc");
        final Page<MiddlewareSummaryEntity> page = middlewareSummaryService.page(new Page<>(current + 1, pageSize),
            queryWrapper);
        final List<MiddlewareSummaryResponse> collect = page.getRecords().parallelStream()
            .map(item -> BeanUtil.copyProperties(item, MiddlewareSummaryResponse.class)).collect(
                Collectors.toList());
        // todo 修改
        boolean canEdit = WebPluginUtils.getUpdateAllowUserIdList() == null || WebPluginUtils.getUser() == null
            || WebPluginUtils.getUpdateAllowUserIdList().contains(WebPluginUtils.getUser().getId());
        collect.parallelStream().forEach(item -> item.setCanEdit(canEdit));
        return PagingList.of(collect, page.getTotal());
    }

    @ModuleDef(
        moduleName = Modules.MIDDLEWARE_MANAGE,
        subModuleName = SubModules.MIDDLEWARE_MANAGE_SUMMARY,
        logMsgKey = Message.MIDDLEWARE_MANAGE_EXPORT
    )
    @ApiOperation("|_ 导出")
    @GetMapping("export")
    public void export(
        @ApiParam(name = "q", value = "搜索内容") String q,
        @ApiParam(name = "status", value = "状态", required = true) Integer status,
        HttpServletResponse response
    ) throws IOException {
        final QueryWrapper<MiddlewareSummaryEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(q)) {
            queryWrapper.lambda().and(
                middlewareSummaryEntityLambdaQueryWrapper -> middlewareSummaryEntityLambdaQueryWrapper.like(
                    MiddlewareSummaryEntity::getArtifactId, q).or().like(
                    MiddlewareSummaryEntity::getGroupId, q));
            OperationLogContextHolder.addVars("q", q);
        } else {
            OperationLogContextHolder.addVars("q", "");
        }

        ApplicationMiddlewareStatusEnum byCode = null;
        if (null != status) {
            byCode = ApplicationMiddlewareStatusEnum.getByCode(status);
            Assert.isTrue(byCode != null, String.format("没有找到对应状态：%s", status));
            queryWrapper.lambda().eq(MiddlewareSummaryEntity::getStatus, status);
            OperationLogContextHolder.addVars("status", byCode.getDesc());
        } else {
            OperationLogContextHolder.addVars("status", "");
        }

        OperationLogContextHolder.operationType(OpTypes.EXOPRT);
        queryWrapper.lambda().last(
            " order by case when status = 4 then 9 when status =2 then 8 when status = 1 then 7 when status = 3 then "
                + "3 else status end desc, id desc");
        final List<MiddlewareSummaryEntity> list = middlewareSummaryService.list(
            queryWrapper);
        OperationLogContextHolder.addVars("totalNum", String.valueOf(list.size()));
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 下载文件的名称
        response.setHeader("Content-Disposition", "attachment;filename=middlewareSummary.xlsx");
        final Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), MiddlewareSummaryExportVO.class,
            list.parallelStream().map(item -> {
                final MiddlewareSummaryExportVO middlewareSummaryExportVO = BeanUtil.copyProperties(item,
                    MiddlewareSummaryExportVO.class);
                middlewareSummaryExportVO.setStatusDesc(
                    ApplicationMiddlewareStatusEnum.getByCode(item.getStatus()).getDesc());
                return middlewareSummaryExportVO;
            }).collect(Collectors.toList()));
        workbook.write(response.getOutputStream());
    }

    @ModuleDef(
        moduleName = Modules.MIDDLEWARE_MANAGE,
        subModuleName = SubModules.MIDDLEWARE_MANAGE_SUMMARY,
        logMsgKey = Message.MIDDLEWARE_MANAGE_SUMMARY_EDIT
    )
    @ApiOperation("|_ 根据id更新")
    @PutMapping("update")
    public void update(@RequestBody MiddlewareSummaryRequest middlewareSummaryRequest) {
        final MiddlewareSummaryEntity middlewareSummaryEntity = BeanUtil.copyProperties(middlewareSummaryRequest,
            MiddlewareSummaryEntity.class);
        Assert.isTrue(middlewareSummaryEntity.getId() != null, "id 不能为空");
        Assert.isTrue(StringUtils.isNotBlank(middlewareSummaryEntity.getArtifactId()), "artifactId 不能为空");
        Assert.isTrue(StringUtils.isNotBlank(middlewareSummaryEntity.getGroupId()), "groupId 不能为空");
        Assert.isTrue(StringUtils.isNotBlank(middlewareSummaryEntity.getType()), "中间件类型不能为空");
        middlewareSummaryService.edit(middlewareSummaryEntity);
    }

}
