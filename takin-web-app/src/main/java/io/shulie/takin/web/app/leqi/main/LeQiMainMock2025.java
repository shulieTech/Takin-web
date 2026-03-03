package io.shulie.takin.web.app.leqi.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.shulie.takin.web.app.leqi.HttpResponse;
import io.shulie.takin.web.app.leqi.TdcBase64Util;
import io.shulie.takin.web.app.leqi.TdcSM4Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LeQiMainMock2025 {

    public static final String SECRET_KEY = "67def66e0262a72807b0da099b37e91a"; //036a6a04e748b702dbbeb6e4b91e5ff5";
    public static void main(String[] args) {
        try {
            String prod_content = "MGNiSEU3Yk5Na1lNcDhVL0FJQU1pQWRqUXpETzBvY1lmVE9oNHp1a3orbGNWeHEya1AzRkduTmJ5VGhPZWpsdg==";
            //String fz_content = "N2pQYlpDTkJOTWNTUnBZby9UaWNyT0dBVlNGTm80REZFQmlOYUhTdjQzVHRlTUJCUjcwUHA3R2tHUDhralN6dERPNnJXQXBHSGVaZUtPTjBFcUxlQ0pCb2VRUVNLeVhnajQ4MHZsVSt0NXlGV1Z5ZnlJYmpQeDhiK1ZxSTFNdlhXdnNXNjRMUWNXZzNXT1RXeXlqcDJCK1lrMmVKM1lBbDhJdTFkbnVOQ0N1Q2ZrSDV3TEsvVWxGemppY0VrNlViNXZoWHBCU0QrZUt2UWFuQ2tVcC9KZVRzdHhqTlRlZFQrSFdieUFiQzlWR000eXljeUxJZTBxa3RTQm5PNUVkb0JKWXpkeFZZN3IxUmhyR0tLVUszQU95bEhqa1IwY29NZW9IT1VPcUNrMW9ZNFQxbmg3aTlzbzR5UjhEQWticDlJdVdXb1JCbUFmZ29wSy9pZ3pYeC9iRjRnUnhRK1gxZjdvMEtqSnhrUngvaVVoTWJlWm1BZXdka1VOdkhDaXdMT0VxbHZNOXNpRU84WGFSYmdxVWZYWXNpVUtUdk9VSEVSYzJhblF1QmJQKzBwOVRrelA1dTUwekYvMEU3WUdPN3Q0aXpDWDRGbE8rQndmZnZoZ0U4WUZXSm1FU3RwMkYrSWREbFA2b0d0cEp1N2Y1eGRaRVhmdUVmb3A5SytxN0UrUEtaa3FFRnhIbWc0MGczdGU5VFpyV0ZXdVJtVEM5WUZoVnZqalB6Uis1TGJkR08rTzQ5NWRTdDlQb3ZYL0J4Y0ozRi9rQmhxQXZJU1N1eVlKRE94Tzkxek9EcFV1cm9oVjBZMDBxVmtqZ0FseFRPMWYwVWNkeHNrRHNIZ25HZFdoV2FRUlh1NTNHdmpoYk8ra1ExZ1NPL2ZTVDBTQ3o3SURIOEU0ek9GRlJWbU1mT1RJVmJ3WXpMSEsxeWdLdjg4bm1lZTdteFpTSjdMb1lOakhGdkZMYU1HbE40Nk1zUHAwTkk0QXBLaURBQU85cHVyRkZtNXZkb3IxVERSUGE5NzlQNEtYd1FpNzRIVTlyRXVuT3pxZ1UxdVdXKzJlSnoxWE9OZnpIaldtc1NCK0tNdlhLZW85TW5Od0czbnVER1hwNm1PTzJXTmpMS0xSa01rUFJMUzhja0lQWnhoNk9XaGRQUW1BR1R0cytMcElDVlhzN2poQ1hzVHBxbjEwZTNQczltTFhhV21OU3VGNWJHcENWNml3aEprblJBbHNZT2s1WGFZVlh1MDZhS0llSzFaN01rYmdWWnZ3VkpVWkN5WkVsQUFmQXRvUXg4NzNUK0tpd3pMalRvMi9aekY3dGx6NmdoUnlKUGtIaXpHbmhNdzZyTGR5RysrMXgwbVFUck1uN1laVG5uTTdPTnBDNE9HM29iV0xQQ3A5dlJjWmFDWXVmbFpQMElZVUJwY3I3RVF5bWYxTjdUY2RvdUM3cUVNb0NJc1FUSkFoZHR1dWxuT1ppL2lMdGhvT2dHNGNteDRna2hzQStUdnhEc1g1NXN2ZWxueXEyTG5nTVE4S2MzZGVNcVNhNVQ0TmlMM3hvZmpQa3oydDZrTzV3QjlxQndwSDUyRkZXdTBLWjMzVVk0TlBEcUk1VVlEeHYrK0d6SVhWbGdpVEVRSXZoN2FGczlUT1FpYTE3R1l2VXoxOGo3alpsSDg4TExZV1hpMGErTzhMc1ZPWll1VVFKSU5qWTJoeVRMbURtTitKaVprbzZucURkWWNOYU5MZjVtSzJXdXNsZDI0Z3JsM3NpOWFjNkpxTzNtcDNTUFNNR1FrNUFQa21GRzBPUXBvdW5sWHc1dnh6cm5MRTZlRjI0MWRiWXBPRnhSeTRJQWU3eXFDQndQaXdwVExib2VDY0RZUUNtbzdjODhwTWQ5MFUxOWd0WUVnbHNZS21kRHRqODg0SnJ5cmtYK3VRYXRjU1VrQ0tDSDMvQnNHblB2dWxCQ1lvRTJZT2ROQ2phTHZ4ZnhFU1k0dzdhbnJ1c3N1eS9xeFRGTTJnTHR2UjUya1IyZHRaVFdlRGVoaVFhaHZzaG41SWhIUjJ4RXdqQ25XTnozdTN5N041SjYxWHdiLzdlNlRBbUcvQnYvVHpTYnlmT3ZCbkdFNWtiVkN4ZG9USXhKY0pTNk5EaVVvQWJYaUtISndITVVBL2RoWGlmVVpTblJSbkRZWCtWWnF1dC93RWIwdVd5andISjlsNzdTR05rY2Z1cEJueXpVK3UxSkRzN2F2ODBHaW1KcUpObEdYbG5WYjI4N3NVWlN4QjYvRzhTVzFuUVNBTG03M3ZZWTZNY2FBejNpeXlYZ0ZPdG92eDdXNkQ3NUlxaStpd0QzYnlqQXcxQUlSa1h5NG1PaVhLekxrZGttSUlJRk4wb0sxZElxMmpVWm9TUkZFZHBqYUN4QVN4U1ZVTERQVFE9PQ==";
            HttpResponse httpResponse = new HttpResponse();
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
            jsonCode.put("httpStatusCode", "200");
            httpResponse.setContent(jsonCode.toJSONString().getBytes("UTF-8"));
            String base64Decoded = TdcBase64Util.decode(prod_content, "UTF-8");
            String jsonReqString = TdcSM4Util.decryptEcb(SECRET_KEY, base64Decoded);
            JSONArray jsonArray = JSON.parseArray(jsonReqString);
            List<String> invoiceNoList = new ArrayList();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String invoiceNo = jsonObject1.getString("fphm");
                invoiceNoList.add(invoiceNo);
            }
            System.out.println(JSON.toJSONString(invoiceNoList));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("---------------------------------------------------");
    }
}
