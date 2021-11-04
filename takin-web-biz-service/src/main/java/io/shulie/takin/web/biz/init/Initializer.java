package io.shulie.takin.web.biz.init;

import io.shulie.takin.web.biz.init.fix.*;
import io.shulie.takin.web.biz.init.sync.ConfigSynchronizer;
import io.shulie.takin.web.biz.service.ApplicationPluginsConfigService;
import io.shulie.takin.web.biz.service.OpsScriptManageService;
import io.shulie.takin.web.biz.service.pradar.PradarConfigService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-10-10
 */
@Component
public class Initializer implements InitializingBean {

    @Autowired
    private ConfigSynchronizer configSynchronizer;

    @Autowired
    private LinkManageFixer linkManageFixer;

    @Autowired
    private PradarConfigService pradarConfigService;

    @Autowired
    private BlacklistDataFixer blacklistDataFixer;

    @Autowired
    private WhitelistDataFixer whitelistDataFixer;

    @Autowired
    private WhitelistEffectAppNameDataFixer whitelistEffectAppNameDataFixer;

    @Autowired
    private RemoteCallFixer remoteCallFixer;

    @Autowired
    ApplicationPluginsConfigService configService;

    @Autowired
    OpsScriptManageService opsScriptManageService;

    @Autowired
    ActivityFixer activityFixer;

    /**
     * 所有项目启动需要做的事情都统一注册在这里
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 将agent需要的配置同步到文件、redis、zk等
        new Thread(() -> configSynchronizer.initSyncAgentConfig()).start();
        new Thread(() -> linkManageFixer.fix()).start();
        // 黑名单数据补全
        new Thread(() -> blacklistDataFixer.fix()).start();
        // 白名单生效应用数据订正
        new Thread(() -> whitelistEffectAppNameDataFixer.fix()).start();
        new Thread(() -> pradarConfigService.initZooKeeperData()).start();
        // 白名单数据修复
        new Thread(() -> whitelistDataFixer.fix()).start();
        //插件管理->给老版本的应用设置默认影子key过期时间
        new Thread(() -> configService.init()).start();
        // 白名单数据迁移
        //new Thread(() -> remoteCallFixer.fix()).start();
        // 校验是否有此用户，没有则创建。用于用户执行运维脚本
        new Thread(() -> opsScriptManageService.init()).start();
        //将历史数据业务活动字段entrance字段中的applicationName拆分出来
        new Thread(() -> activityFixer.fix()).start();

    }
}
