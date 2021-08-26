package io.shulie.takin.web.data.result.probe;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author liuchuan
 * @date 2021/6/7 11:36 上午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class ProbeDetailResult extends ProbeListResult {

    /**
     * 文件地址
     */
    private String path;

}
