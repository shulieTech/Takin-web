package io.shulie.takin.web.ext.entity.ecloud;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckPackageRespExt {

    private Boolean checkStatus;

    private String errorDetail;

    private String solution;
}
