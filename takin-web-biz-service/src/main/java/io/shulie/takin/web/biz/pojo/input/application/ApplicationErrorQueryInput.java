package io.shulie.takin.web.biz.pojo.input.application;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author fanxx
 * @date 2020/10/16 11:39 上午
 */
@Data
@NoArgsConstructor
public class ApplicationErrorQueryInput {

    @NonNull
    private Long applicationId;

    public ApplicationErrorQueryInput(@NonNull Long applicationId) {
        this.applicationId = applicationId;
    }
}

