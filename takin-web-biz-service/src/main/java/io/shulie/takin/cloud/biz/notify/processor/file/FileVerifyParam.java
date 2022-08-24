package io.shulie.takin.cloud.biz.notify.processor.file;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.shulie.takin.cloud.biz.notify.CloudNotifyParam;
import io.shulie.takin.cloud.model.callback.script.ResultReport;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("500")
public class FileVerifyParam extends CloudNotifyParam {

    private ResultReport.Data data;
}
