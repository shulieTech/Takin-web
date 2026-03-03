package io.shulie.takin.web.app.leqi.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.shulie.takin.web.app.leqi.TdcBase64Util;
import io.shulie.takin.web.app.leqi.TdcLqZipAndBase64Utils;
import io.shulie.takin.web.app.leqi.TdcSM4Util;

import java.util.UUID;

public class LeQiMain2025 {

    public static final String SECRET_KEY = "036a6a04e748b702dbbeb6e4b91e5ff5";
    public static void main(String[] args) {
        try {
            String QDFPSC_RES = "ZFdtYlYrQUt1eklIRzdQbTh2ZG5rRGJPUUV4b25VNXJ2RTM2ZnZ4RU1ZUmgrbXVUUFJZQzhDdmY5ZEFFYmh2bkNZWFUwVmFENmVYZzM5emNvbHlnKzlFZmV0ejJqOVcxM0YxcFpiYVQyOXg4OS9nWVhpYUUyZWdBRE5nNkgyemZJanVZWllUb1ZYRGdHL25GZERuY3ZEQjBJUFlyRG16UTI1U2ZlSXd6dnNGc0hsT3MycFF6Z0l4TGdjSnd5VExoWkNMMWdIZTQwSTlWV3Z3clk2MCtVQmZaTGltU0ZNWjhWVy9oSEM4QWwreXkzU1dwUUFRSmRFd1dNV2Fndkt6VjFZVFJ3dVFxalljSjR4ZGxXOHNxZzYwMnZUNkFabUw3Q0tmU2F6dWsvbm8rL0xuN3cvUk9lb0JycExwRENsY0dUTVZWQWdRMlN4My9UcTA2Q3QxaUZjSHJMaTFwOGJlK3BraTRVZEJNL0hvOWxBTkoycEU4R1FSZjltbmx0NEFUZml0TUdteUR1MzQybUUrN0cyZHRwY2lTOThJdU5jK3R2MVlkSnhsUndwN0tlRHJDaVJDUjBJMGU4VUJtZmVxdEI4RXJ6WnZqd0luNEdWR2p2RzN2dXBuMC91dTNTSWxDa0RXUHZvSC8vbmJpRHJraEtXcDRSNks0SGxRM1Z6bzBOR0RrUWt4UVRNR1BFUmU0WlVxRnhsejI2WlNVZ1lQOTJCaWJ5Nkk2eGQyL2RtRElOZEFkVUw2TW5WM1pRNzZreWdVSVQ4UEFxWXdkSjFWd0QzQlpCb0ZaUldDdjgxRDVpdzRKNHlYc3NYd0V3SHgvb0dVSE5kYWJaZ0ZmalNDdmJaUlA5SGduVW1oMmhJS3BZVzNtSURKaEZqOTBCQUVJVEdpVlNZbFJzRnJheWRoS3lTK2NncUM5eXRmNVJ0c21jZE5aL2QzK0l5NDA0aVQ4b2FOdHdXaFByQ25mb2ZtRnRaV1hMZk1xZkNxbmtRK0luc3pSenptUklPVmJIeGNqSmI0S0I1Z0YrNjJwUlZWOTlScE4wZVppK0pmVlN4ZS9Db0grd29VOVZ1NVZrdGRIZE9najhVR0gvS1BDYThVUU5rRWxUNG5pWlV2RFZlRWdobi9KQXdZaUpmaGtUWU5Yd2Y4Y2VRSUhFWEFxV3ZWSTUxNTNvaStJQ1F1WFYyVlNtdHNuSVRkWEN0VVJRSVpKcWNzdHRZdmVrS1dCeTRUODJ2UjR1ZWpiU29HRFZqT1BHNllyWG1oTXo1c3IwTExiSkQxUjAzWVh0WkFVY0tXVHdpV1VQTkZ1L0xUUFdCYjcxakFqTkdiWVNvdVpOcldjTVk3R2pDSHlKcFZVUjdMSFAyVUVhTFQvdE4xSEk1cXhiSDlDQjRNQnkrUDEwWTVGVTJNckdDZkhEcnpUVjdvYmo0dFpacnFuNmNTRGtoSmZKc0Z4VWh5QVNOUSt1dzNHRE0xaUt0bFUvQXhmaXhiVzV3SnhGOEdVQ25odzNNRlU1a1k5ZnVRV0wzd1FWZEtIdlhUNTZGV1dkVk1Jajltc3NGYnpGY25uNHlzbDFvT0ZXam1kRnFTcFZPN2dVVk1hWEo0VzN6ckprMmFGcERhMVlOMzZuQ28wNzJNMTM2eU4rbVkzSy8wWERNY3YvRkkxK3ozc01jSEIxM05BUmJQUWZ6bEYzWWJrZTM0QzhDVlJVZ3B2c2k1Mm5vdDJ6dmRwaGhMODNPUnl2S0FhcWVSRG5KQUxlN2lJZWM0dEU4TDFMUFFPSWRJRkR4TE12QnAwQnRYZUs3N0hrR2dhdm1mdnVNeEVzNXA3UktyUng4dDNCZ1FIWjlrR29pRmNwbXczY1pDVGtNNkRwaHRyQS8rVTZxUXM3L0x6amlxR2pBZ2FYTUFmQzREWkhSc0ppVWs2U09Fa1VFaUJzR1RTaUQ1OW9oejcwdEJlZjVaOXEzY2RxK2xCU0NjazlKUmh0M3NZNi83bC9aamcxQXh0aHlPRTZYVWVrc3BjSnVRWG91RmRlVktxbHIrdXdBT1hHSURqWGIyRGliZ0JZSzI1bXFuK3ZrQmdSQy9SNFdEbGc3ZGdINzRVS3Z4NFJyRTJwK0J3Q2FqTHRkU205QXo4aTRraExpS1V5LzlRdnR2WW93NTh2dnZQeG9wRVRLNm5SbHpxSW9CdmxGTFh6M0tmdmJocWd6TTJuYWJBK0lZc2ZtWjdiZkJGMFUwaXJqMTd4Y2xTeW5aRSt1U1NQdHIyT09JTGVjMTdqd1owRDNwck1PakxmUWJaYys1bC9ZQ0hNbGdHUVIzU2VvRE0vTitxcGFpK2FYcXM2TnJHa2tjTTJ5SEtTTzczL2dFdWI4SU9BSFlMNTdPOUxqQ2xNOUNMWHUyT0RLT0tiazczeTYxeGg4TWdlaEU1OHZ4LytMV0pIMXhxVjNBOHN2SkdJOWpvdk9uU1RKV2FQQVVxaGtVU0pUaTVZWStCWHl1cjNOYWRFcWY2SWIzTzMyelZZOExycjBBQmRmb3NvYUVLc1V1bUlBTld1eFdJa3YyV3FZNndhZGhR";
            String QDFPSC_RES_STR = TdcSM4Util.decryptEcb(SECRET_KEY, QDFPSC_RES);
            System.out.println("QDFPSC响应:" + QDFPSC_RES_STR);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("returnmsg", "成功");
            jsonObject.put("returncode", "00");
            jsonObject.put("sllsh", UUID.randomUUID().toString().replace("-", ""));
            System.out.println(TdcSM4Util.encryptEcb(SECRET_KEY, jsonObject.toJSONString()).equals(QDFPSC_RES));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("---------------------------------------------------");
    }
}
