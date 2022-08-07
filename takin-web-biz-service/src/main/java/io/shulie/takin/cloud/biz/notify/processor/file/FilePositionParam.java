package io.shulie.takin.cloud.biz.notify.processor.file;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.shulie.takin.cloud.biz.notify.CloudNotifyParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("303")
public class FilePositionParam extends CloudNotifyParam {

    private FilePosition data;
}
