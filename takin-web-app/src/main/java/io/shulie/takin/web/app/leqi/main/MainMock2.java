package io.shulie.takin.web.app.leqi.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.shulie.takin.web.app.leqi.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class MainMock2 {

    public static HttpResponse doPost(Object[] args) {
        HttpResquest httpRequest =  (HttpResquest) args[0];
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setHttpCode(Integer.valueOf(200));
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Takin_PT");
        headers.put("Date", new Date().toGMTString());
        headers.put("Content-Type", "text/json;charset=UTF-8");
        String SECRET_KEY = "036a6a04e748b702dbbeb6e4b91e5ff5";
        try {
            if (httpRequest.getUrl().endsWith("/access/newsandbox/v2/invoke/203059/FPCY_NEW")) {
                JSONObject jsonContent = new JSONObject();
                jsonContent.put("returncode", "00");
                String base64Decoded = TdcBase64Util.decode(httpRequest.getContent(), "UTF-8");
                String jsonString = TdcSM4Util.decryptEcb(SECRET_KEY, base64Decoded);
                JSONObject jsonObjectReq = JSON.parseObject(jsonString);
                String fphm = jsonObjectReq.getString("fphm");
                String returnJsonString = "{\"fjysxx\":\"\",\"xfnsrlxdm\":\"\",\"tdyslxDm\":\"\",\"xhfdz\":\"黑龙江省大庆市大同区温舒园36-1号商服\",\"gmfmc\":\"国网黑龙江省电力有限公司\",\"gmfdz\":\"黑龙江省黑河市嫩江市嫩江镇嫩兴路\",\"skrxm\":\"汪雨霏\",\"xsfkhh\":\"\",\"kjhzfpdydlzfphm\":\"\",\"kce\":\"\",\"hjje\":\"18534.51\",\"cpyycbs\":\"\",\"fppzDm\":\"81\",\"xmmxhs\":\"\",\"zzsjzjtDm\":\"\",\"kprq\":\" 2024-09-20 10:49:29\",\"gmfkhh\":\"\",\"bz\":\"\",\"zzfphm\":\"\",\"kjhzfpdydzzfpDm\":\"\",\"jsfsDm\":\"\",\"gmfdh\":\"\",\"id\":\"24232000000049999993\",\"xsfdh\":\"\",\"xsfnsrsbh\":\"91230900MA1BDT4L60\",\"hjse\":\"2409.49\",\"zzfpDm\":\"\",\"sflzfp\":\"Y\",\"xsfzh\":\"\",\"fhrxm\":\"汪雨霏\",\"kpr\":\"汪雨霏\",\"gmfnsrsbh\":\"912300001269709935\",\"gmfzh\":\"\",\"xsfmc\":\"七台河辰能生物质发电有限公司\",\"jshjdx\":\"贰万零玖佰肆拾肆圆零角零分\",\"cktslDm\":\"\",\"jshj\":\"20944.00\",\"kjhzfpdydzzfphm\":\"\",\"hwxx\":[{\"ggxh\":\"\",\"kce\":\"\",\"xh\":\"1\",\"spfwjc\":\"\",\"se\":\"2409.49\",\"xmmc\":\"*发电*风力发电\",\"sphfwssflhbbm\":\"\",\"dw\":\"千瓦时\",\"fpjydj\":\"0.37\",\"fpjysl\":\"56000\",\"hwhyslwfwmc\":\"\",\"je\":\"20944.00\",\"slv\":\"0.13\"}],\"fphm\":\"24232000000049999993\"}";
                JSONObject jsonObjectReturn = JSON.parseObject(returnJsonString);
                jsonObjectReturn.put("fphm", fphm);
                JSONObject jsonCode = new JSONObject();
                JSONObject jsonResponse = new JSONObject();
                JSONObject jsonData = new JSONObject();
                jsonContent.put("cyjgxx", TdcLqZipAndBase64Utils.zipAndBase64Encode(jsonObjectReturn.toJSONString(), "1.json"));
                jsonData.put("RequestId", UUID.randomUUID().toString().replace("-", ""));
                jsonData.put("Data", TdcSM4Util.encryptEcb(SECRET_KEY, jsonContent.toJSONString()));
                jsonResponse.put("Response", jsonData.toJSONString());
                jsonCode.put("body", jsonResponse.toJSONString());
                jsonCode.put("httpStatusCode", "00");
                httpResponse.setContent(jsonCode.toJSONString().getBytes("UTF-8"));
                httpResponse.setHeaders(headers);
                return httpResponse;
            } else if (httpRequest.getUrl().endsWith("/access/newsandbox/v2/invoke/202007/QDFPSC")) {
                JSONObject jsonContent = new JSONObject();
                jsonContent.put("returnmsg", "成功");
                jsonContent.put("returncode", "00");
                String uuid = UUID.randomUUID().toString().replace("-", "");
                jsonContent.put("sllsh", uuid);
                JSONObject jsonCode = new JSONObject();
                JSONObject jsonResponse = new JSONObject();
                JSONObject jsonData = new JSONObject();
                jsonData.put("RequestId", uuid);
                jsonData.put("Data", TdcSM4Util.encryptEcb(SECRET_KEY, jsonContent.toJSONString()));
                jsonResponse.put("Response", jsonData.toJSONString());
                jsonCode.put("body", jsonResponse.toJSONString());
                jsonCode.put("httpStatusCode", "00");
                httpResponse.setContent(jsonCode.toJSONString().getBytes("UTF-8"));
                httpResponse.setHeaders(headers);

                String base64Decoded = TdcBase64Util.decode(httpRequest.getContent(), "UTF-8");
                String jsonReqString = TdcSM4Util.decryptEcb(SECRET_KEY, base64Decoded);
                JSONArray jsonArray = JSON.parseArray(jsonReqString);
                List<String> invoiceNoList = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String invoiceNo = jsonObject1.getString("fphm");
                    invoiceNoList.add(invoiceNo);
                }
                LoadTestCacheUtil.putInvoiceNo(uuid, invoiceNoList);

                return httpResponse;
            } else if (httpRequest.getUrl().endsWith("/access/newsandbox/v2/invoke/202007/CXQDFPSCJG")) {
                JSONObject jsonContent = new JSONObject();
                jsonContent.put("returnmsg", "成功");
                jsonContent.put("returncode", "00");
                String base64Decoded = TdcBase64Util.decode(httpRequest.getContent(), "UTF-8");
                String jsonReqString = TdcSM4Util.decryptEcb(SECRET_KEY, base64Decoded);
                JSONObject jsonObjectReq = JSON.parseObject(jsonReqString);
                String sllsh = jsonObjectReq.getString("sllsh");
                List<String> invoiceNoList = LoadTestCacheUtil.getInvoiceNo(sllsh);
                JSONArray jsonArray = new JSONArray();
                for (String invoiceNo : invoiceNoList){
                    JSONObject jsonObjectResult = new JSONObject();
                    jsonObjectResult.put("fphm", invoiceNo);
                    jsonObjectResult.put("status", "00");
                    jsonObjectResult.put("message", "上传成功");
                    jsonArray.add(jsonObjectResult);
                }
                jsonContent.put("resultList", jsonArray.toJSONString());
                JSONObject jsonCode = new JSONObject();
                JSONObject jsonResponse = new JSONObject();
                JSONObject jsonData = new JSONObject();
                jsonData.put("RequestId", UUID.randomUUID().toString().replace("-", ""));
                jsonData.put("Data", TdcSM4Util.encryptEcb(SECRET_KEY, jsonContent.toJSONString()));
                jsonResponse.put("Response", jsonData.toJSONString());
                jsonCode.put("body", jsonResponse.toJSONString());
                jsonCode.put("httpStatusCode", "00");
                httpResponse.setContent(jsonCode.toJSONString().getBytes("UTF-8"));
                httpResponse.setHeaders(headers);
                return httpResponse;
            } else if (httpRequest.getUrl().endsWith("/access/newsandbox/v2/invoke/202007/CXSXED")) {
                String jsonString = "{\"ztsxbz\":\"N\",\"bysxed\":\"1000000000\",\"returnmsg\":\"成功\",\"kysyed\":\"500000000\",\"returncode\":\"00\",\"yxzed\":\"500000000\",\"yxzwsyed\":\"500000000\",\"sq\":\"202411\"}";
                JSONObject jsonCode = new JSONObject();
                JSONObject jsonResponse = new JSONObject();
                JSONObject jsonData = new JSONObject();
                jsonData.put("RequestId", UUID.randomUUID().toString().replace("-", ""));
                jsonData.put("Data", TdcSM4Util.encryptEcb(SECRET_KEY, jsonString));
                jsonResponse.put("Response", jsonData.toJSONString());
                jsonCode.put("body", jsonResponse.toJSONString());
                jsonCode.put("httpStatusCode", "00");
                httpResponse.setContent(jsonCode.toJSONString().getBytes("UTF-8"));
                httpResponse.setHeaders(headers);
                return httpResponse;
            }
        } catch (Exception e) {

        }
        httpResponse.setHttpCode(Integer.valueOf(999));
        return httpResponse;
    }



    public static void main(String[] args) {
        HttpResquest httpRequest = new HttpResquest();
        httpRequest.setUrl("hjjjjj/access/newsandbox/v2/invoke/203059/FPCY_NEW");
        Object[] objs = new Object[]{httpRequest};
        doPost(objs);
    }
}
