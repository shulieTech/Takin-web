package io.shulie.takin.web.biz.pojo.response;

import lombok.Data;

@Data
public class FileInfo {
    private String fileName;
    private String filePath;
    private String fileSize;
}
