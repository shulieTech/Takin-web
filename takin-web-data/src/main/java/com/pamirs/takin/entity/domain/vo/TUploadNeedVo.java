package com.pamirs.takin.entity.domain.vo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * @author 298403
 * @date 2019-1-15
 */
@Data
public class TUploadNeedVo {

    /**
     * appName
     */

    @NotBlank
    private String appName;

    /**
     * 上传大小
     */
    @NotNull
    private Integer size;
}
