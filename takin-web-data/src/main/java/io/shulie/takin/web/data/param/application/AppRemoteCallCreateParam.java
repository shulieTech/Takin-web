package io.shulie.takin.web.data.param.application;

import java.util.Date;

import io.shulie.takin.web.common.vo.application.AppRemoteCallVO;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/5/29 12:18 上午
 */
@Data
public class AppRemoteCallCreateParam extends AppRemoteCallVO {

    public AppRemoteCallCreateParam() {
        super(new Date(),new Date());
    }
}
