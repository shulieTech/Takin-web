package io.shulie.takin.web.data.result.statistics;

import java.util.Date;

import lombok.Data;

/**
 * @author 无涯
 * @date 2020/11/30 9:23 下午
 */
@Data
public class PressureListTotalResult {
    private Long id;
    private String name;
    private String tags;
    private String scriptVersion;
    private Date gmtCreate;
    private String createName;
    private Integer count;
    private Integer success;
    private Integer fail;
}
