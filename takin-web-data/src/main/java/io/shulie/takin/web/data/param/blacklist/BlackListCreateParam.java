package io.shulie.takin.web.data.param.blacklist;

import java.util.Date;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/4 8:18 下午
 */
@Data
public class BlackListCreateParam {

    private long blistId;

    private String redisKey;

    private Integer useYn;

    private Date createTime;

    private Date updateTime;

}
