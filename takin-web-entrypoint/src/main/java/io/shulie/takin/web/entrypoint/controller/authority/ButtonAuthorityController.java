package io.shulie.takin.web.entrypoint.controller.authority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.pamirs.takin.common.ResponseError;
import com.pamirs.takin.common.ResponseOk;
import io.shulie.takin.web.common.constant.ApiUrls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 说明: takin按钮权限控制
 *
 * @author shulie
 * @version v1.0
 * @date Create in 2018/6/11 16:50
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
public class ButtonAuthorityController {

    private static final Map<String, List<JSONObject>> MENU = new HashMap<>();
    private static final String TEXT = "[\n" +
        "    {\n" +
        "        \"id\":\"takin_FUNCTION_00001\",\n" +
        "        \"text\":\"配置中心\",\n" +
        "        \"leaf\":false,\n" +
        "        \"parentId\":\"0\",\n" +
        "        \"checked\":null,\n" +
        "        \"entity\":null,\n" +
        "        \"children\":null,\n" +
        "        \"uri\":null,\n" +
        "        \"expandable\":true,\n" +
        "        \"expend\":null,\n" +
        "        \"iconCls\":\"null\",\n" +
        "        \"cls\":\"null\",\n" +
        "        \"displayOrder\":\"1\",\n" +
        "        \"nodes\":[\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00004\",\n" +
        "                \"text\":\"应用管理\",\n" +
        "                \"leaf\":true,\n" +
        "                \"parentId\":\"takin_FUNCTION_00001\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":\"/applicationManager\",\n" +
        "                \"expandable\":true,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"1\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00006\",\n" +
        "                \"text\":\"白名单管理\",\n" +
        "                \"leaf\":true,\n" +
        "                \"parentId\":\"takin_FUNCTION_00001\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":\"/whiteList\",\n" +
        "                \"expandable\":true,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"2\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00005\",\n" +
        "                \"text\":\"黑名单管理\",\n" +
        "                \"leaf\":true,\n" +
        "                \"parentId\":\"takin_FUNCTION_00001\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":\"/blackList\",\n" +
        "                \"expandable\":true,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"3\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00003\",\n" +
        "                \"text\":\"链路管理\",\n" +
        "                \"leaf\":true,\n" +
        "                \"parentId\":\"takin_FUNCTION_00001\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":\"/linkManager\",\n" +
        "                \"expandable\":true,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"4\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00016\",\n" +
        "                \"text\":\"一级链路管理\",\n" +
        "                \"leaf\":true,\n" +
        "                \"parentId\":\"takin_FUNCTION_00001\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":\"/primaryLinkManager\",\n" +
        "                \"expandable\":true,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"5\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00017\",\n" +
        "                \"text\":\"二级链路管理\",\n" +
        "                \"leaf\":true,\n" +
        "                \"parentId\":\"takin_FUNCTION_00001\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":\"/businessLinkManager\",\n" +
        "                \"expandable\":true,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"6\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00114\",\n" +
        "                \"text\":\"数据字典\",\n" +
        "                \"leaf\":true,\n" +
        "                \"parentId\":\"takin_FUNCTION_00001\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":\"/takin-dictionary\",\n" +
        "                \"expandable\":true,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"7\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00120\",\n" +
        "                \"text\":\"影子库表管理\",\n" +
        "                \"leaf\":true,\n" +
        "                \"parentId\":\"takin_FUNCTION_00001\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":\"/shadowTableMnt\",\n" +
        "                \"expandable\":true,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"8\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00130\",\n" +
        "                \"text\":\"上传链路关系配置\",\n" +
        "                \"leaf\":true,\n" +
        "                \"parentId\":\"takin_FUNCTION_00001\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":\"/importLinkTopology\",\n" +
        "                \"expandable\":false,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"9\"\n" +
        "            }\n" +
        "        ]\n" +
        "    },\n" +
        "    {\n" +
        "        \"id\":\"takin_FUNCTION_00045\",\n" +
        "        \"text\":\"压测控制\",\n" +
        "        \"leaf\":false,\n" +
        "        \"parentId\":\"0\",\n" +
        "        \"checked\":null,\n" +
        "        \"entity\":null,\n" +
        "        \"children\":null,\n" +
        "        \"uri\":null,\n" +
        "        \"expandable\":true,\n" +
        "        \"expend\":null,\n" +
        "        \"iconCls\":\"null\",\n" +
        "        \"cls\":\"null\",\n" +
        "        \"displayOrder\":\"2\",\n" +
        "        \"nodes\":[\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00058\",\n" +
        "                \"text\":\"压测总览图\",\n" +
        "                \"leaf\":true,\n" +
        "                \"parentId\":\"takin_FUNCTION_00045\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":\"/pressureTestOverview\",\n" +
        "                \"expandable\":false,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"1\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00059\",\n" +
        "                \"text\":\"压测前后置准备\",\n" +
        "                \"leaf\":false,\n" +
        "                \"parentId\":\"takin_FUNCTION_00045\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":null,\n" +
        "                \"expandable\":true,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"2\",\n" +
        "                \"nodes\":[\n" +
        "                    {\n" +
        "                        \"id\":\"takin_FUNCTION_00060\",\n" +
        "                        \"text\":\"数据构建\",\n" +
        "                        \"leaf\":true,\n" +
        "                        \"parentId\":\"takin_FUNCTION_00059\",\n" +
        "                        \"checked\":null,\n" +
        "                        \"entity\":null,\n" +
        "                        \"children\":null,\n" +
        "                        \"uri\":\"/dataConstruction\",\n" +
        "                        \"expandable\":true,\n" +
        "                        \"expend\":null,\n" +
        "                        \"iconCls\":\"null\",\n" +
        "                        \"cls\":\"null\",\n" +
        "                        \"displayOrder\":\"1\"\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"id\":\"takin_FUNCTION_00061\",\n" +
        "                        \"text\":\"压测检查\",\n" +
        "                        \"leaf\":true,\n" +
        "                        \"parentId\":\"takin_FUNCTION_00059\",\n" +
        "                        \"checked\":null,\n" +
        "                        \"entity\":null,\n" +
        "                        \"children\":null,\n" +
        "                        \"uri\":\"/pressureTestInspection\",\n" +
        "                        \"expandable\":false,\n" +
        "                        \"expend\":null,\n" +
        "                        \"iconCls\":\"null\",\n" +
        "                        \"cls\":\"null\",\n" +
        "                        \"displayOrder\":\"2\"\n" +
        "                    }\n" +
        "                ]\n" +
        "            },\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00056\",\n" +
        "                \"text\":\"压测执行\",\n" +
        "                \"leaf\":true,\n" +
        "                \"parentId\":\"takin_FUNCTION_00045\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":\"/pressureTestExecute\",\n" +
        "                \"expandable\":false,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"3\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00057\",\n" +
        "                \"text\":\"压测报告\",\n" +
        "                \"leaf\":true,\n" +
        "                \"parentId\":\"takin_FUNCTION_00045\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":\"/pressureTestReport\",\n" +
        "                \"expandable\":false,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"4\"\n" +
        "            }\n" +
        "        ]\n" +
        "    },\n" +
        "    {\n" +
        "        \"id\":\"takin_FUNCTION_00012\",\n" +
        "        \"text\":\"监控\",\n" +
        "        \"leaf\":false,\n" +
        "        \"parentId\":\"0\",\n" +
        "        \"checked\":null,\n" +
        "        \"entity\":null,\n" +
        "        \"children\":null,\n" +
        "        \"uri\":null,\n" +
        "        \"expandable\":true,\n" +
        "        \"expend\":null,\n" +
        "        \"iconCls\":\"null\",\n" +
        "        \"cls\":\"null\",\n" +
        "        \"displayOrder\":\"3\",\n" +
        "        \"nodes\":[\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00013\",\n" +
        "                \"text\":\"业务监控\",\n" +
        "                \"leaf\":true,\n" +
        "                \"parentId\":\"takin_FUNCTION_00012\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":\"/businessMonitoring\",\n" +
        "                \"expandable\":false,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"1\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00014\",\n" +
        "                \"text\":\"系统监控\",\n" +
        "                \"leaf\":true,\n" +
        "                \"parentId\":\"takin_FUNCTION_00012\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":\"/alarm\",\n" +
        "                \"expandable\":false,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"2\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00015\",\n" +
        "                \"text\":\"告警列表\",\n" +
        "                \"leaf\":true,\n" +
        "                \"parentId\":\"takin_FUNCTION_00012\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":\"/alarmList\",\n" +
        "                \"expandable\":false,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"3\"\n" +
        "            }" +
        //            ",\n" +
        //            "            {\n" +
        //            "                \"id\":\"takin_FUNCTION_00131\",\n" +
        //            "                \"text\":\"压测流量监控\",\n" +
        //            "                \"leaf\":true,\n" +
        //            "                \"parentId\":\"takin_FUNCTION_00012\",\n" +
        //            "                \"checked\":null,\n" +
        //            "                \"entity\":null,\n" +
        //            "                \"children\":null,\n" +
        //            "                \"uri\":\"/pressureOverview\",\n" +
        //            "                \"expandable\":false,\n" +
        //            "                \"expend\":null,\n" +
        //            "                \"iconCls\":\"null\",\n" +
        //            "                \"cls\":\"null\",\n" +
        //            "                \"displayOrder\":\"4\"\n" +
        //            "            }\n" +
        "        ]\n" +
        "    },\n" +
        "    {\n" +
        "        \"id\":\"takin_FUNCTION_00063\",\n" +
        "        \"text\":\"压测辅助\",\n" +
        "        \"leaf\":false,\n" +
        "        \"parentId\":\"0\",\n" +
        "        \"checked\":null,\n" +
        "        \"entity\":null,\n" +
        "        \"children\":null,\n" +
        "        \"uri\":null,\n" +
        "        \"expandable\":true,\n" +
        "        \"expend\":null,\n" +
        "        \"iconCls\":\"null\",\n" +
        "        \"cls\":\"null\",\n" +
        "        \"displayOrder\":\"4\",\n" +
        "        \"nodes\":[\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00065\",\n" +
        "                \"text\":\"MQ topic 虚拟消费\",\n" +
        "                \"leaf\":false,\n" +
        "                \"parentId\":\"takin_FUNCTION_00063\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":null,\n" +
        "                \"expandable\":true,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"1\",\n" +
        "                \"nodes\":[\n" +
        "                    {\n" +
        "                        \"id\":\"takin_FUNCTION_00069\",\n" +
        "                        \"text\":\"ESBMQ\",\n" +
        "                        \"leaf\":true,\n" +
        "                        \"parentId\":\"takin_FUNCTION_00065\",\n" +
        "                        \"checked\":null,\n" +
        "                        \"entity\":null,\n" +
        "                        \"children\":null,\n" +
        "                        \"uri\":\"/esbmq\",\n" +
        "                        \"expandable\":false,\n" +
        "                        \"expend\":null,\n" +
        "                        \"iconCls\":\"null\",\n" +
        "                        \"cls\":\"null\",\n" +
        "                        \"displayOrder\":\"1\"\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"id\":\"takin_FUNCTION_00067\",\n" +
        "                        \"text\":\"IBMMQ\",\n" +
        "                        \"leaf\":true,\n" +
        "                        \"parentId\":\"takin_FUNCTION_00065\",\n" +
        "                        \"checked\":null,\n" +
        "                        \"entity\":null,\n" +
        "                        \"children\":null,\n" +
        "                        \"uri\":\"/ibmmq\",\n" +
        "                        \"expandable\":false,\n" +
        "                        \"expend\":null,\n" +
        "                        \"iconCls\":\"null\",\n" +
        "                        \"cls\":\"null\",\n" +
        "                        \"displayOrder\":\"2\"\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"id\":\"takin_FUNCTION_00068\",\n" +
        "                        \"text\":\"ROCKETMQ\",\n" +
        "                        \"leaf\":true,\n" +
        "                        \"parentId\":\"takin_FUNCTION_00065\",\n" +
        "                        \"checked\":null,\n" +
        "                        \"entity\":null,\n" +
        "                        \"children\":null,\n" +
        "                        \"uri\":\"/rocketmq\",\n" +
        "                        \"expandable\":false,\n" +
        "                        \"expend\":null,\n" +
        "                        \"iconCls\":\"null\",\n" +
        "                        \"cls\":\"null\",\n" +
        "                        \"displayOrder\":\"3\"\n" +
        "                    }\n" +
        "                ]\n" +
        "            },\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00111\",\n" +
        "                \"text\":\"MQ topic 虚拟生产\",\n" +
        "                \"leaf\":false,\n" +
        "                \"parentId\":\"takin_FUNCTION_00063\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":null,\n" +
        "                \"expandable\":true,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"2\",\n" +
        "                \"nodes\":[\n" +
        "                    {\n" +
        "                        \"id\":\"takin_FUNCTION_00112\",\n" +
        "                        \"text\":\"ESB/IBM\",\n" +
        "                        \"leaf\":true,\n" +
        "                        \"parentId\":\"takin_FUNCTION_00111\",\n" +
        "                        \"checked\":null,\n" +
        "                        \"entity\":null,\n" +
        "                        \"children\":null,\n" +
        "                        \"uri\":\"/produce/ebm\",\n" +
        "                        \"expandable\":false,\n" +
        "                        \"expend\":null,\n" +
        "                        \"iconCls\":\"null\",\n" +
        "                        \"cls\":\"null\",\n" +
        "                        \"displayOrder\":\"1\"\n" +
        "                    },\n" +
        "                    {\n" +
        "                        \"id\":\"takin_FUNCTION_00113\",\n" +
        "                        \"text\":\"ROCKETMQ\",\n" +
        "                        \"leaf\":true,\n" +
        "                        \"parentId\":\"takin_FUNCTION_00111\",\n" +
        "                        \"checked\":null,\n" +
        "                        \"entity\":null,\n" +
        "                        \"children\":null,\n" +
        "                        \"uri\":\"/produce/rocketmq\",\n" +
        "                        \"expandable\":false,\n" +
        "                        \"expend\":null,\n" +
        "                        \"iconCls\":\"null\",\n" +
        "                        \"cls\":\"null\",\n" +
        "                        \"displayOrder\":\"2\"\n" +
        "                    }\n" +
        "                ]\n" +
        "            },\n" +
        "            {\n" +
        "                \"id\":\"takin_FUNCTION_00110\",\n" +
        "                \"text\":\"数据加工回传\",\n" +
        "                \"leaf\":true,\n" +
        "                \"parentId\":\"takin_FUNCTION_00063\",\n" +
        "                \"checked\":null,\n" +
        "                \"entity\":null,\n" +
        "                \"children\":null,\n" +
        "                \"uri\":\"/dataCallbackList\",\n" +
        "                \"expandable\":false,\n" +
        "                \"expend\":null,\n" +
        "                \"iconCls\":\"null\",\n" +
        "                \"cls\":\"null\",\n" +
        "                \"displayOrder\":\"3\"\n" +
        "            }\n" +
        "        ]\n" +
        "    }\n" +
        //            "    {\n" +
        //            "        \"id\":\"takin_FUNCTION_00125\",\n" +
        //            "        \"text\":\"防作弊\",\n" +
        //            "        \"leaf\":false,\n" +
        //            "        \"parentId\":\"0\",\n" +
        //            "        \"checked\":null,\n" +
        //            "        \"entity\":null,\n" +
        //            "        \"children\":null,\n" +
        //            "        \"uri\":null,\n" +
        //            "        \"expandable\":true,\n" +
        //            "        \"expend\":null,\n" +
        //            "        \"iconCls\":\"null\",\n" +
        //            "        \"cls\":\"null\",\n" +
        //            "        \"displayOrder\":\"5\",\n" +
        //            "        \"nodes\":[\n" +
        //            "            {\n" +
        //            "                \"id\":\"takin_FUNCTION_00128\",\n" +
        //            "                \"text\":\"检查开关\",\n" +
        //            "                \"leaf\":true,\n" +
        //            "                \"parentId\":\"takin_FUNCTION_00125\",\n" +
        //            "                \"checked\":null,\n" +
        //            "                \"entity\":null,\n" +
        //            "                \"children\":null,\n" +
        //            "                \"uri\":\"/appSwitchConfigure\",\n" +
        //            "                \"expandable\":false,\n" +
        //            "                \"expend\":null,\n" +
        //            "                \"iconCls\":\"null\",\n" +
        //            "                \"cls\":\"null\",\n" +
        //            "                \"displayOrder\":\"1\"\n" +
        //            "            },\n" +
        //            "            {\n" +
        //            "                \"id\":\"takin_FUNCTION_00129\",\n" +
        //            "                \"text\":\"应用上传信息\",\n" +
        //            "                \"leaf\":true,\n" +
        //            "                \"parentId\":\"takin_FUNCTION_00125\",\n" +
        //            "                \"checked\":null,\n" +
        //            "                \"entity\":null,\n" +
        //            "                \"children\":null,\n" +
        //            "                \"uri\":\"/stackExceptionReport\",\n" +
        //            "                \"expandable\":false,\n" +
        //            "                \"expend\":null,\n" +
        //            "                \"iconCls\":\"null\",\n" +
        //            "                \"cls\":\"null\",\n" +
        //            "                \"displayOrder\":\"2\"\n" +
        //            "            }\n" +
        //            "        ]\n" +
        //            "    },\n" +
        //            "    {\n" +
        //            "        \"id\":\"takin_FUNCTION_00132\",\n" +
        //            "        \"text\":\"物理隔离\",\n" +
        //            "        \"leaf\":false,\n" +
        //            "        \"parentId\":\"0\",\n" +
        //            "        \"checked\":null,\n" +
        //            "        \"entity\":null,\n" +
        //            "        \"children\":null,\n" +
        //            "        \"uri\":null,\n" +
        //            "        \"expandable\":true,\n" +
        //            "        \"expend\":null,\n" +
        //            "        \"iconCls\":\"null\",\n" +
        //            "        \"cls\":\"null\",\n" +
        //            "        \"displayOrder\":\"6\",\n" +
        //            "        \"nodes\":[\n" +
        //            "            {\n" +
        //            "                \"id\":\"takin_FUNCTION_00133\",\n" +
        //            "                \"text\":\"隔离配置\",\n" +
        //            "                \"leaf\":true,\n" +
        //            "                \"parentId\":\"takin_FUNCTION_00132\",\n" +
        //            "                \"checked\":null,\n" +
        //            "                \"entity\":null,\n" +
        //            "                \"children\":null,\n" +
        //            "                \"uri\":\"/isolateConfigure\",\n" +
        //            "                \"expandable\":true,\n" +
        //            "                \"expend\":null,\n" +
        //            "                \"iconCls\":\"null\",\n" +
        //            "                \"cls\":\"null\",\n" +
        //            "                \"displayOrder\":\"1\"\n" +
        //            "            }\n" +
        //            "        ]\n" +
        //            "    }"+
        "]";
    private static JSONArray jsonArray = null;

    static {
        jsonArray = JSONArray.parseArray(TEXT);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONArray nodes = jsonObject.getJSONArray("nodes");

            List<JSONObject> subList = new ArrayList<>();
            for (int j = 0; j < nodes.size(); j++) {
                JSONObject jsonObject1 = nodes.getJSONObject(j);
                JSONArray nodes1 = jsonObject1.getJSONArray("nodes");

                if (null != nodes1) {
                    jsonObject1.remove("nodes");
                    List<JSONObject> subsubList = new ArrayList<>();
                    for (int k = 0; k < nodes1.size(); k++) {
                        JSONObject jsonObject2 = nodes1.getJSONObject(k);
                        subsubList.add(jsonObject2);
                    }
                    MENU.put(jsonObject1.getString("id"), subsubList);
                }
                subList.add(jsonObject1);
            }

            jsonObject.remove("nodes");
            List<JSONObject> objectList = Optional.ofNullable(MENU.get("0")).orElse(new ArrayList<>());
            objectList.add(jsonObject);
            MENU.put("0", objectList);
            MENU.put(jsonObject.getString("id"), subList);
        }
    }

    public final Logger LOGGER = LoggerFactory.getLogger(ButtonAuthorityController.class);

    /**
     * 说明: 根据按钮url查看按钮权限
     *
     * @param buttonUrl 按钮url
     * @return false代表没有权限, true代表有权限
     * @author shulie
     * @date 2018/6/12 15:20
     */
    @GetMapping(value = ApiUrls.API_TAKIN_BUTTON_AUTHORITY_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> queryButtonUrlAuthority(@RequestParam("buttonUrl") String buttonUrl) {
        try {

            return ResponseOk.create(true);
        } catch (Exception e) {
            return ResponseError.create(1040100101, "根据按钮url校验按钮权限错误");
        }
    }

    @GetMapping(value = "/authority/loadTree", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> loginTree(@RequestParam(value = "node") String id) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("nodes", MENU.get(id));
        return ResponseEntity.ok(result);
    }
}
