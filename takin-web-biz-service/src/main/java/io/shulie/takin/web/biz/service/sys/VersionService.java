package io.shulie.takin.web.biz.service.sys;

import java.util.Map;
import java.util.Properties;

import io.shulie.takin.web.data.model.mysql.VersionEntity;
import io.shulie.takin.web.data.result.system.VersionVo;

public interface VersionService {

    String GIT_PROPERTIES_FILE = "git.properties";
    String WEB = "takin-web";
    String EE = "takin-web-ee";
    String E2E = "takin-e2e";
    String AMDB = "amdb";
    String SURGE = "surge";

    String BRANCH = "git.branch";
    String COMMIT_ID = "git.commit.id.abbrev";
    String WEB_REGISTER_PATH = "/pradar/config/version/web";
    String WEB_EE_REGISTER_PATH = "/pradar/config/version/web_ee";
    String WEB_E2E_REGISTER_PATH = "/pradar/config/version/web_e2e";
    String AMDB_REGISTER_PATH = "/pradar/config/version/amdb";
    String SURGEREGISTER_PATH = "/pradar/config/version/surge";

    boolean publish(VersionEntity entity);

    VersionVo selectVersion();

    void confirm();

    boolean ignore();

    Map<String, Properties> gitVersions();

    void initGitVersion();

    Map<String, String> queryZkVersionData();
}
