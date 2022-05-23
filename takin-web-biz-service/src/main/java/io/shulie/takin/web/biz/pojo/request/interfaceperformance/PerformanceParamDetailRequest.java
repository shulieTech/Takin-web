package io.shulie.takin.web.biz.pojo.request.interfaceperformance;

import lombok.Data;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/20 2:32 下午
 */
@Data
public class PerformanceParamDetailRequest {
    private Long configId;

    /**
     * 配置Id
     */
    private List<Long> fileIds;

    /**
     * 临时文件路径
     */
    private List<String> filePaths;
}
