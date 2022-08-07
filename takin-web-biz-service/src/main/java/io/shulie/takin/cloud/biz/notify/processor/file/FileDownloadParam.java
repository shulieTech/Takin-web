package io.shulie.takin.cloud.biz.notify.processor.file;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.shulie.takin.cloud.biz.notify.CloudNotifyParam;
import io.shulie.takin.cloud.model.callback.file.ProgressReport;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("400")
public class FileDownloadParam extends CloudNotifyParam {

    private ProgressReport.Data data;
}
