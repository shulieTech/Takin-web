package io.shulie.takin.web.entrypoint.controller.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.Message;
import io.shulie.takin.web.biz.constant.BizOpConstants.ModuleCode;
import io.shulie.takin.web.biz.constant.BizOpConstants.Modules;
import io.shulie.takin.web.biz.constant.BizOpConstants.OpTypes;
import io.shulie.takin.web.biz.constant.BizOpConstants.SubModules;
import io.shulie.takin.web.biz.pojo.request.application.MiddlewareJarRequest;
import io.shulie.takin.web.biz.pojo.response.application.MiddlewareCompareResponse;
import io.shulie.takin.web.biz.pojo.response.application.MiddlewareImportResponse;
import io.shulie.takin.web.biz.pojo.response.application.MiddlewareJarResponse;
import io.shulie.takin.web.biz.service.application.MiddlewareJarService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.shulie.takin.web.data.model.mysql.MiddlewareJarEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 中间件信息的导入和对比
 *
 * @author liqiyu
 */
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL + APIUrls.MIDDLEWARE_JAR)
@Api(tags = "接口: 中间件信息")
@Slf4j
public class MiddlewareJarController {
    public static final String MIDDLEWARE_MANAGE_DIR = "middleware_manage";

    @Autowired
    private MiddlewareJarService middlewareJarService;

    /**
     * 上传文件的路径
     */
    @Value("${data.path}")
    private String uploadPath;

    /**
     * 对比
     *
     * @return 对比结果
     */
    @ApiOperation("|_ 比对")
    @PostMapping("compare")
    @ModuleDef(
        moduleName = Modules.MIDDLEWARE_MANAGE,
        subModuleName = SubModules.MIDDLEWARE_MANAGE_JAR,
        logMsgKey = Message.MIDDLEWARE_MANAGE_COMPARE
    )
    public MiddlewareCompareResponse compareMj(
        @ApiParam(name = "files", value = "多个文件", required = true) @RequestParam List<MultipartFile> files) {
        Assert.isTrue(CollectionUtil.isNotEmpty(files), "文件不存在!");
        Assert.isTrue(files.size() <= 12, "文件个数太多, 请选择12个以下进行比对!");
        final MiddlewareCompareResponse middlewareCompareResponse = middlewareJarService.compareMj(files);
        OperationLogContextHolder.operationType(OpTypes.COMPARE);
        OperationLogContextHolder.addVars("total", String.valueOf(middlewareCompareResponse.getTotal()));
        OperationLogContextHolder.addVars("fail", String.valueOf(middlewareCompareResponse.getFail()));
        OperationLogContextHolder.addVars("supported", String.valueOf(middlewareCompareResponse.getSupported()));
        OperationLogContextHolder.addVars("notSupported", String.valueOf(middlewareCompareResponse.getNotSupported()));
        OperationLogContextHolder.addVars("unknown", String.valueOf(middlewareCompareResponse.getUnknown()));
        return middlewareCompareResponse;
    }

    /**
     * 导入
     *
     * @return 导入失败的文件
     */
    @ApiOperation("|_ 导入")
    @AuthVerification(
        moduleCode = ModuleCode.MIDDLEWARE,
        needAuth = ActionTypeEnum.UPDATE
    )
    @PostMapping("import")
    @ModuleDef(
        moduleName = Modules.MIDDLEWARE_MANAGE,
        subModuleName = SubModules.MIDDLEWARE_MANAGE_JAR,
        logMsgKey = Message.MIDDLEWARE_MANAGE_IMPORT
    )
    public MiddlewareImportResponse importMj(
        @ApiParam(name = "file", value = "单个文件", required = true) @RequestParam MultipartFile file)
        throws IOException {
        // response 是为了设置响应格式，以及设置导入的成功、失败的条数
        final MiddlewareImportResponse middlewareImportResponse = middlewareJarService.importMj(file);
        OperationLogContextHolder.operationType(OpTypes.IMPORT);
        OperationLogContextHolder.addVars("total", String.valueOf(middlewareImportResponse.getTotal()));
        OperationLogContextHolder.addVars("success", String.valueOf(middlewareImportResponse.getSuccess()));
        OperationLogContextHolder.addVars("fail", String.valueOf(middlewareImportResponse.getFail()));
        return middlewareImportResponse;
    }

    @ApiOperation("|_ 根据id更新")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.MIDDLEWARE,
        needAuth = ActionTypeEnum.UPDATE
    )
    @PutMapping("update")
    @ModuleDef(
        moduleName = Modules.MIDDLEWARE_MANAGE,
        subModuleName = SubModules.MIDDLEWARE_MANAGE_JAR,
        logMsgKey = Message.MIDDLEWARE_MANAGE_JAR_EDIT
    )
    public void update(@RequestBody MiddlewareJarRequest middlewareJarRequest) {
        MiddlewareJarEntity middlewareJarEntity = BeanUtil.copyProperties(middlewareJarRequest,
            MiddlewareJarEntity.class);
        Assert.isTrue(middlewareJarEntity.getId() != null, "id 不能为空");
        middlewareJarService.edit(middlewareJarEntity);
    }

    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.MIDDLEWARE,
        needAuth = ActionTypeEnum.QUERY
    )
    @ApiOperation("|_ 分页查询")
    @GetMapping("list")
    public PagingList<MiddlewareJarResponse> list(
        @ApiParam(name = "current", value = "页码", required = true) Integer current,
        @ApiParam(name = "pageSize", value = "页大小", required = true) Integer pageSize,
        @ApiParam(name = "artifactId", value = "artifactId", required = true) String artifactId,
        @ApiParam(name = "groupId", value = "groupId", required = true) String groupId) {
        final QueryWrapper<MiddlewareJarEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MiddlewareJarEntity::getArtifactId, artifactId);
        queryWrapper.lambda().eq(MiddlewareJarEntity::getGroupId, groupId);
        queryWrapper.lambda().last(
            " order by case when status = 4 then 9 when status =2 then 8 when status = 1 then 7 when status = 3 then "
                + "3 else status end desc, id desc");
        final Page<MiddlewareJarEntity> page = middlewareJarService.page(new Page<>(current + 1, pageSize),
            queryWrapper);
        List<MiddlewareJarResponse> collect = page.getRecords().parallelStream()
            .map(item -> BeanUtil.copyProperties(item, MiddlewareJarResponse.class))
            .collect(Collectors.toList());

        if (!collect.isEmpty()) {
            boolean canEdit = WebPluginUtils.getUpdateAllowUserIdList() == null || WebPluginUtils.getUser() == null
                || WebPluginUtils.getUpdateAllowUserIdList().contains(WebPluginUtils.getUser().getId());
            collect.forEach(response -> response.setCanEdit(canEdit));
        }

        return PagingList.of(collect, page.getTotal());
    }

    @GetMapping("file/{fileName}")
    public void getFile(@PathVariable String fileName, HttpServletResponse response)
        throws FileNotFoundException {
        File file = new File(uploadPath + File.separator + MIDDLEWARE_MANAGE_DIR + File.separator + fileName);
        if (!file.exists()) {
            return;
        }

        // 响应头设置
        response.setHeader("Content-Disposition", String.format("attachment;filename=%s", file.getName()));
        response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        try (
            // 使用sendfile:读取磁盘文件，并网络发送
            ServletOutputStream servletOutputStream = response.getOutputStream();
            FileChannel channel = new FileInputStream(file).getChannel();) {
            response.setHeader("Content-Length", String.valueOf(channel.size()));
            channel.transferTo(0, channel.size(), Channels.newChannel(servletOutputStream));
        } catch (Exception e) {
            log.error("agent 下载探针包 --> 错误: {}", e.getMessage(), e);
        } finally {
            // 下载完成，删除文件。
            file.delete();
        }
    }
}
