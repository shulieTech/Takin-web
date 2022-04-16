package io.shulie.takin.cloud.biz.notify.processor.pod;

import lombok.Data;

@Data
public class FilePosition {

    private Long taskId;
    private String fileName;
    private String podNum;
    private Long startPosition;
    private Long readPosition;
    private Long endPosition;

}
