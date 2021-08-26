-- 预删除
DROP PROCEDURE IF EXISTS `exec_init_menu`;
-- 声明
DELIMITER $$
CREATE PROCEDURE exec_init_menu()
BEGIN
    SET @button_count = (SELECT COUNT(1) FROM `t_base_config` WHERE `CONFIG_CODE`='ALL_BUTTON');
    SET @menu_count   = (SELECT COUNT(1) FROM `t_base_config` WHERE `CONFIG_CODE`='ALL_MENU');
    IF @button_count = 0 THEN
        insert into `t_base_config` (CONFIG_CODE, CONFIG_VALUE, CONFIG_DESC, USE_YN, CREATE_TIME, UPDATE_TIME)
        values  ('ALL_BUTTON', '[
    "appManage_2_create",
    "appManage_3_update",
    "appManage_4_delete",
    "appManage_6_enable_disable",
    "bottleneckConfig_3_update",
    "businessActivity_2_create",
    "businessActivity_3_update",
    "businessActivity_4_delete",
    "businessFlow_2_create",
    "businessFlow_3_update",
    "businessFlow_4_delete",
    "configCenter_authorityConfig_2_create",
    "configCenter_authorityConfig_3_update",
    "configCenter_authorityConfig_4_delete",
    "configCenter_bigDataConfig_3_update",
    "configCenter_blacklist_2_create",
    "configCenter_blacklist_3_update",
    "configCenter_blacklist_4_delete",
    "configCenter_blacklist_6_enable_disable",
    "configCenter_dataSourceConfig_2_create",
    "configCenter_dataSourceConfig_3_update",
    "configCenter_dataSourceConfig_4_delete",
    "configCenter_entryRule_2_create",
    "configCenter_entryRule_3_update",
    "configCenter_entryRule_4_delete",
    "configCenter_middlewareManage_3_update",
    "configCenter_pressureMeasureSwitch_6_enable_disable",
    "configCenter_whitelistSwitch_6_enable_disable",
    "debugTool_linkDebug_2_create",
    "debugTool_linkDebug_3_update",
    "debugTool_linkDebug_4_delete",
    "debugTool_linkDebug_5_start_stop",
    "exceptionNoticeManage_2_create",
    "exceptionNoticeManage_3_update",
    "exceptionNoticeManage_4_delete",
    "patrolBoard_2_create",
    "patrolBoard_3_update",
    "patrolBoard_4_delete",
    "patrolManage_2_create",
    "patrolManage_3_update",
    "patrolManage_4_delete",
    "patrolManage_5_start_stop",
    "pressureTestManage_pressureTestScene_2_create",
    "pressureTestManage_pressureTestScene_3_update",
    "pressureTestManage_pressureTestScene_4_delete",
    "pressureTestManage_pressureTestScene_5_start_stop",
    "scriptManage_2_create",
    "scriptManage_3_update",
    "scriptManage_4_delete",
    "scriptManage_7_download"
]', '全部按钮名称', 0, NOW(),NOW());
    END IF;
    IF @menu_count = 0 THEN
        insert into `t_base_config` (CONFIG_CODE, CONFIG_VALUE, CONFIG_DESC, USE_YN, CREATE_TIME, UPDATE_TIME)
        values  ('ALL_MENU', '[{
    	"title": "系统概览",
    	"path": "/dashboard",
    	"type": "Item",
    	"icon": "dashboard"
    }, {
    	"title": "仿真平台",
    	"type": "SubMenu",
    	"path": "/shop",
    	"icon": "shop",
    	"children": [{
    		"title": "应用配置",
    		"type": "SubMenu",
    		"path": "/appConfig",
    		"children": [{
    			"title": "应用管理",
    			"path": "/appManage",
    			"type": "Item",
    			"children": [{
    				"title": "应用详情",
    				"path": "/appManage/details",
    				"type": "NoMenu"
    			}]
    		}]
    	}, {
    		"title": "链路管理",
    		"type": "SubMenu",
    		"path": "/linkManage",
    		"children": [{
    			"title": "入口规则",
    			"path": "/configCenter/entryRule",
    			"type": "Item"
    		}, {
    			"title": "业务活动",
    			"path": "/businessActivity",
    			"type": "Item",
    			"children": [{
    				"title": "新增业务活动",
    				"path": "/businessActivity/addEdit",
    				"type": "NoMenu"
    			}]
    		}, {
    			"title": "业务流程",
    			"path": "/businessFlow",
    			"type": "Item",
    			"children": [{
    				"title": "新增业务流程",
    				"path": "/businessFlow/addBusinessFlow",
    				"type": "NoMenu"
    			}]
    		}]
    	}, {
    		"title": "脚本管理",
    		"type": "SubMenu",
    		"path": "/scriptManages",
    		"children": [{
    			"title": "测试脚本",
    			"path": "/scriptManage",
    			"type": "Item",
    			"children": [{
    				"title": "脚本配置",
    				"path": "/scriptManage/scriptConfig",
    				"type": "NoMenu"
    			}, {
    				"title": "脚本调试详情",
    				"path": "/scriptManage/scriptDebugDetail",
    				"type": "NoMenu"
    			}]
    		}, {
    			"title": "运维脚本",
    			"path": "/scriptOperation",
    			"type": "Item"
    		}]
    	}, {
    		"title": "数据源管理",
    		"type": "SubMenu",
    		"path": "/dataSourceManage",
    		"children": [{
    			"title": "数据源配置",
    			"path": "/configCenter/dataSourceConfig",
    			"type": "Item"
    		}]
    	}]
    }, {
    	"title": "压测平台",
    	"type": "SubMenu",
    	"path": "/hourglass",
    	"icon": "hourglass",
    	"children": [{
    		"title": "压测管理",
    		"type": "SubMenu",
    		"path": "/pressureTestManage",
    		"children": [{
    			"title": "压测场景",
    			"path": "/pressureTestManage/pressureTestScene",
    			"type": "Item",
    			"children": [{
    				"title": "压测场景配置",
    				"path": "/pressureTestManage/pressureTestScene/pressureTestSceneConfig",
    				"type": "NoMenu"
    			}]
    		}, {
    			"title": "压测报告",
    			"path": "/pressureTestManage/pressureTestReport",
    			"type": "Item",
    			"children": [{
    				"title": "压测实况",
    				"path": "/pressureTestManage/pressureTestReport/pressureTestLive",
    				"type": "NoMenu"
    			}, {
    				"title": "压测报告详请",
    				"path": "/pressureTestManage/pressureTestReport/details",
    				"type": "NoMenu"
    			}]
    		}]
    	}]
    }, {
    	"title": "设置中心",
    	"type": "SubMenu",
    	"path": "/setting",
    	"icon": "setting",
    	"children": [{
    		"title": "系统管理",
    		"type": "SubMenu",
    		"path": "/configCenter",
    		"children": [{
    			"title": "系统信息",
    			"path": "/configCenter/systemInfo",
    			"type": "Item"
    		}, {
    			"title": "全局配置",
    			"path": "/configCenter/globalConfig",
    			"type": "Item"
    		}, {
    			"title": "中间件库管理",
    			"path": "/configCenter/middlewareManage",
    			"type": "Item"
    		}, {
    			"title": "开关配置",
    			"path": "/configCenter/bigDataConfig",
    			"type": "Item"
    		}]
    	}]
    }]', '全部的菜单地址', 0, NOW(),NOW());
    END IF;
END $$
-- 执行
DELIMITER ;
CALL exec_init_menu();
-- 后置删除
DROP PROCEDURE IF EXISTS `exec_init_menu`;

