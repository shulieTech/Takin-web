package io.shulie.takin.web.data.param.leakverify;

import java.util.List;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/5 8:20 下午
 */
@Data
public class LeakVerifyDetailQueryParam {
    /**
     * 验证结果id列表
     */
    List<Long> resultIdList;
}
