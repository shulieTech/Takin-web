package shulie.takin.web.other;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author liuchuan
 * @date 2021/6/25 11:02 上午
 */
public class StringTest {

    @Test
    public void test() {
        String a = getA();
        String b = getB();

        Assert.assertEquals(a, b);
    }

    private String getA() {
        return "{\n"
            + "    \"error\": null,\n"
            + "    \"data\": {\n"
            + "        \"entryHostIp\": \"172.17..1\",\n"
            + "        \"startTime\": 1624536533339,\n"
            + "        \"traces\": [\n"
            + "            {\n"
            + "                \"id\": 0,\n"
            + "                \"interfaceName\": \"/users/user/add/addUser\",\n"
            + "                \"applicationName\": \"canaceaijun0617\",\n"
            + "                \"succeeded\": false,\n"
            + "                \"params\": \"{{\\\"name\\\":\\\"555aaa\\\",\\\"description\\\":\\\"hjgj\\\"}}\",\n"
            + "                \"costTime\": 19,\n"
            + "                \"offsetStartTime\": 0,\n"
            + "                \"nextNodes\": [\n"
            + "                    {\n"
            + "                        \"id\": 1,\n"
            + "                        \"interfaceName\": \"com.example.canacedemo.mapper.UserMapper"
            + ".insertSelective\",\n"
            + "                        \"applicationName\": \"canaceaijun0617\",\n"
            + "                        \"succeeded\": false,\n"
            + "                        \"params\": \"{User[id=89783,name=555aaa,description=hjgj]}\",\n"
            + "                        \"costTime\": 17,\n"
            + "                        \"offsetStartTime\": 0,\n"
            + "                        \"nextNodes\": [],\n"
            + "                        \"clusterTest\": true,\n"
            + "                        \"agentId\": \"172.17.0.1-22715\",\n"
            + "                        \"entryHostIp\": \"172.17..1\",\n"
            + "                        \"index\": null,\n"
            + "                        \"rpcId\": \"0.1\",\n"
            + "                        \"nodeSuccess\": false,\n"
            + "                        \"nodeIp\": \"172.17..1\",\n"
            + "                        \"response\": \"1\"\n"
            + "                    }\n"
            + "                ],\n"
            + "                \"clusterTest\": true,\n"
            + "                \"agentId\": null,\n"
            + "                \"entryHostIp\": null,\n"
            + "                \"index\": null,\n"
            + "                \"rpcId\": \"0\",\n"
            + "                \"nodeSuccess\": false,\n"
            + "                \"nodeIp\": \"172.17..1\",\n"
            + "                \"response\": \"\"\n"
            + "            }\n"
            + "        ],\n"
            + "        \"totalCost\": 19,\n"
            + "        \"clusterTest\": true\n"
            + "    },\n"
            + "    \"success\": true\n"
            + "}";
    }

    private String getB() {
        return "{\n"
            + "    \"error\": null,\n"
            + "    \"data\": {\n"
            + "        \"entryHostIp\": \"172.17..1\",\n"
            + "        \"traces\": [\n"
            + "            {\n"
            + "                \"id\": 0,\n"
            + "                \"interfaceName\": \"/users/user/add/addUser\",\n"
            + "                \"applicationName\": \"canaceaijun0617\",\n"
            + "                \"succeeded\": false,\n"
            + "                \"params\": \"{{\\\"name\\\":\\\"555aaa\\\",\\\"description\\\":\\\"hjgj\\\"}}\",\n"
            + "                \"costTime\": 19,\n"
            + "                \"offsetStartTime\": 0,\n"
            + "                \"nextNodes\": [\n"
            + "                    {\n"
            + "                        \"id\": 1,\n"
            + "                        \"interfaceName\": \"com.example.canacedemo.mapper.UserMapper"
            + ".insertSelective\",\n"
            + "                        \"applicationName\": \"canaceaijun0617\",\n"
            + "                        \"succeeded\": false,\n"
            + "                        \"params\": \"{User[id=89783,name=555aaa,description=hjgj]}\",\n"
            + "                        \"costTime\": 17,\n"
            + "                        \"offsetStartTime\": 0,\n"
            + "                        \"nextNodes\": [],\n"
            + "                        \"clusterTest\": true,\n"
            + "                        \"agentId\": \"172.17.0.1-22715\",\n"
            + "                        \"entryHostIp\": \"172.17..1\",\n"
            + "                        \"index\": null,\n"
            + "                        \"rpcId\": \"0.1\",\n"
            + "                        \"nodeSuccess\": false,\n"
            + "                        \"nodeIp\": \"172.17..1\",\n"
            + "                        \"response\": \"1\"\n"
            + "                    }\n"
            + "                ],\n"
            + "                \"clusterTest\": true,\n"
            + "                \"agentId\": null,\n"
            + "                \"entryHostIp\": null,\n"
            + "                \"index\": null,\n"
            + "                \"rpcId\": \"0\",\n"
            + "                \"nodeSuccess\": false,\n"
            + "                \"nodeIp\": \"172.17..1\",\n"
            + "                \"response\": \"\"\n"
            + "            }\n"
            + "        ],\n"
            + "        \"clusterTest\": true,\n"
            + "        \"startTime\": 1624536533339,\n"
            + "        \"totalCost\": 19\n"
            + "    },\n"
            + "    \"success\": true\n"
            + "}";
    }

}
