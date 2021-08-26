package io.shulie.takin.web.biz.service;

import io.shulie.takin.web.biz.pojo.request.file.FileUploadRequest;
import io.shulie.takin.web.biz.pojo.response.common.FileUploadResponse;
import io.shulie.takin.web.biz.pojo.response.common.IsNewAgentResponse;

/**
 * 公共api的服务层
 *
 * @author liuchuan
 * @date 2021/6/4 11:24 上午
 */
public interface ApiService {

    /**
     * 文件根据目录上传到服务器上
     * 上传到临时文件目录
     * 其他用到的地方复制, 或剪切
     *
     * @param request 上传所需参数, 文件, 目录
     * @return 文件保存的路径
     */
    FileUploadResponse uploadFile(FileUploadRequest request);

    /**
     * 应用agent是新版本
     *
     * @param applicationId 应用id
     * @return 响应参数
     */
    IsNewAgentResponse isNewAgentByApplication(Long applicationId);

}
