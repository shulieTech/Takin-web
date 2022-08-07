package io.shulie.takin.adapter.api.model.request.pressure;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.IdUtil;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptAnnounceRequest extends ContextExt {

    private final String attach = IdUtil.fastSimpleUUID();
    private String callbackUrl;
    private List<String> watchmanIdList;
    private List<FileItem> fileList = new ArrayList<>();

    @Data
    @Builder
    public static class FileItem {
        private String path; // 文件路径
        private String sign; // md5
        private String downloadUrl; // 下载地址
    }
}
