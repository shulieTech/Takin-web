package io.shulie.takin.web.biz.service.sys;

import io.shulie.takin.web.data.model.mysql.VersionEntity;
import io.shulie.takin.web.data.result.system.VersionVo;

public interface VersionService {

    boolean publish(VersionEntity entity);

    VersionVo selectVersion();

    void confirm();

    boolean ignore();
}
