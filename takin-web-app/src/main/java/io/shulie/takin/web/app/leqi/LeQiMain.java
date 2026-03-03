package io.shulie.takin.web.app.leqi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.UUID;

public class LeQiMain {

    public static final String SECRET_KEY = "036a6a04e748b702dbbeb6e4b91e5ff5";

    public static void parseFPCY_NEW_Resp(String data) throws Exception {
        String jsonString = TdcSM4Util.decryptEcb(SECRET_KEY, data);
        System.out.println("FPCY_NEW响应1:" + jsonString);
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String cyjgxx = jsonObject.getString("cyjgxx");
        System.out.println("FPCY_NEW响应2:" + TdcLqZipAndBase64Utils.unzipAndBase64Decode(cyjgxx));
    }

    public static void parseQDFPSC_Req(String data) throws Exception {
        String QDFPSC_RES_STR = TdcSM4Util.decryptEcb(SECRET_KEY, data);
        System.out.println("QDFPSC响应:" + QDFPSC_RES_STR);
    }

    public static void main(String[] args) {
        String str = "eyJib2R5Ijoie1wiUmVzcG9uc2VcIjp7XCJSZXF1ZXN0SWRcIjpcImUwYzRhZTRjZmI1Y2NiMTFcIixcIkRhdGFcIjpcImVUYnNuenpJN2x4RDlUam1LRll5OURNMDBhVzd2ek15ZVg4dVUyUktaUlVyNEs2SEpNc3FIa2xCMS8zNjdXUzVFcy85aTUvVlBRZk10bUN4R3FzOTduWGdEKzVweDZIQTFsKzZWaXRtbTJIdWhkNDRoNUd5N1d6SkpkbTk2d3kweS8rVTJyOXRTSTRYblVkUkJUdFRweGRYRTdtcm0rbTRJcWJrbzhFZHJVYjUvMTlNUzU2VEdVMWx2RnZKVEs4SDMyZ2RtMEpzWlFJU2tZaFdBaG44dFpuKzRlR1Y5OGRjd3FoU2FoRzFTeHBhL25XWml4aW5XRTh3RGRFQzhiQ3JvZ202OVQvODYwYUZpV3pQalhiLzdZNnp3LzlzV3k0YXJDVzJWcUxKbGROdjRjbmRrY0dlSVU5bGhONXg0K21vaXRJNHVuN2dYQ2FaMDFDU0I3L3Zxb3ZjOWZRMjdXYTFYZXNzZVlTbmdUdFRvaEdUblNoUnMvbzAxQjIvcUxXSGpJUjNnYU9sRDhWN0syU0tKMy9PYmxVczNyMXFrQ3pmZmlOU1h0MzNlcVpEb05lWllic0FWNlhJby9xVHRpM2U2Qk9HUktTVWZWMHY2NXdJVW5nSml6UTNFN3NLLzhtM3VuRi9Id1d5TkplR0Qxem9QRENORWYveTEvd0ExdDZqM05vdzIweHBmWWxkR3huRjVmQmY5ZlA0VDdTK2E5Zm4zNDlOWUEwNDRFUndDeWtkMHJEL0hMRUNndWxhckpyNUg1UUxjeTkrOHBwZEdsNEdpS1pMRmNXR08wTzJ4a3pIRUphQzJaYTBrc2twcG1VVlJYRVpPcUI2TEgrbnQ0eWJHbzJCU25BU2tFdmpoK3gyQkMrV0x5QVh4MW1STkhISHJLeXNvcWdVTUFjMWJ0Mmkza3JxaitxYXpYYjk2YVZzYS9Gc0NHZjlXaWxMRkw4eUp1VDJYelIwR09raG9zWDUvZTRQQmN1WVB4K1VIWm1GY1piLzMveWRuMTdzNnRsZGd6N1BqeHR1N3FvV2h4THlrc0dYbThoOGNndDlBcDg3NCtYdnpTem9hTW1xQlhSUUxBbEVWVSthNGVZZzdvQ2ZlQ2NCbkM4TFRBc0c2bGlaUSt0QkNDOVBlOVVhVHhSd0pwcU9keGZuKzNmVDJQU1cwN3hZRzZhWWZ2alkxZEJtZGtSWjBzTzhXb0s2MVVMcndVUVBWRVpnU0NUNnVCR2tqaEFxS0JROStmZWhmWlpzeGZCYkVVMkJuazJNRW1OamlJdUF6c212ZTExaUhmMC80VTc4U25MNy93OUxLejljSDZ1VVVma3plalB1aUkzTUMvNUZRalNEQksvaXZBbnMzTzYvSWV4NkFtc3k0RVl6aFcrN0w2ajAwcmJKUkVxTzJ4NG9MWWlCYUN5WGtzS2EvR0IzMTRuSm1KWXNyYnVXRk5kUUZyYkNDblNmbnkySTVJM2xXVVpONnhkTmR0ZDgybG1JOTBzb1BuSjRUODdnTHpyUW05Z3Rsc1o2NUxxeXF6dnlSWXBSNm1GYlZmaHRSU25rSjhDR2VLUVFQM1I2bTNPTGJWdG5acjMvQk1SSFErZS9sRCtOSy9lUHcrTGZxeSs5cmRqNG1pRXRMYi9WTmV4YXVsTFBUTHhQU2FDZkwrRHV5dHJKeml4MTl2NCt0NHg3TUtiQnZVZHEweWhkT2VIbXlMMktTL3ZwOUY2VDROb2g1eXliV29DT2xHVHVyZjZ5Q0JKeW9SMWZXTjFxMHZEMHlBNHpWZkNQVEV0d3ZvbkpRSEJhR01RZ2ZEVWxGVFNoc2FCOXhta3IrZ2tkNS90U0pxQ1JmWS9EWGxidUh5Q2RESTBQYW1kcDJyVlBCazkyVzBUZXBUT25CYkpaSlA0YVR1S2hIcXNCcS93QUpXU2ZNK3JaR3dvPVwifX0iLCJodHRwU3RhdHVzQ29kZSI6IjIwMCJ9";
        System.out.println(TdcBase64Util.decode(str, TdcBase64Util.ENCODING));
        System.out.println("---------------------------------------------------");
        /**
         * FPCY_NEW
         */
        try {
            String FPCY_NEW_REQ = "RTYwbGQ2TGlOSXc4TklNRk50akhPbS83SEpUSkNQRytRVC9ZNzUwNnQ0UU54ckM2SGlzUU1RQzVGdU5Kc2V3VmhRK21wUU5SdEx0MWQraGxXN2tGZHVvTFFRamFudTcvb1htcmlsVitjSkduU3d5N3lUYkJ2SGUvUTRKMGowN1V3Vjc4eGo3S3R2YzlrOHhrUzdQZEVBPT0=";
            String base64Decoded = TdcBase64Util.decode(FPCY_NEW_REQ, TdcBase64Util.ENCODING);
            System.out.println("FPCY_NEW入参:" + TdcSM4Util.decryptEcb(SECRET_KEY, base64Decoded));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            String FPCY_NEW_RES = "7e9HrOJE629J7V+6gMCkkuz4yqShms8c9ZY7aDL9YSZGyhDUlvjDwuD/6VqdIuanBtK0BHWRJXQS/IhoqzdeENWBshd4ZhP8h8dGerTRAwCpCERRJQMDBB6EN0O+MGXPuPduD2GJrEE+FPJDgu0BwpLvAH5Dp50PGH+yva7ddvUkPqF87yj2yRQJLjZmYlGeLMQT+0AO7zJxGYnhTFfJT78wy4wBOemqWBtfo3zBD93smTHdZz8W5p0VjYb+KFGk4sufTuwiw9SdtsHk/oCLZaD32EXDPURYsMmL08tzsiaA1t2f8xNthdlQsV1Zc2OicF5g73rSjMn+PVTN+8lM/5Xl/C8EwDTzjJYbAY7P5iRpOxpaCFj6A+0XGCLfnMJbIddj8OjYsdV3+MVQnqUHUg0Stsl1LGcjqGxpqPSSpnMmujbgY1dAaXIPy1tcwzkhsBRz7b6XK3clyhPR2aJIKOG42kglwBy+UT5Hl0kbFyM0wmxJVzfeNRjjRBYoY2h/PgBiYF1BECYA3XOUYdiy2vZ1TbXD4YNkCtKzSD1CWRZNkg2kOEBJHf6gPfG758KBhu9Fo3nL0P7BAxXiJUUtb2niqXAWiLLM2rLOjsHUzcqZnjR/pIH4/vfu9OSGTgsxF+XO5C/9K7HTUF+rHInJqJ3RTWUYesrAlVS3/2jNNBbjKcR9ZftyekNXEo0kDcwnPBWTc1EN5ic8R0971A2jEliUVIx7TgpNsU8pSUpq74TvpmcPwZj8Z1kJDmJKg6x9U/loSI5GgIX0IxhE6oifNXgunAJpa+b0jLMyqZMQDc42nweKFGjgti2G5uIWeIuN1xS2Xv8Vfb1xloq8utO+ABOGX/7aGNDwvssaSzPvI053b2WdSF4b2WydoCOq+Orq01CdH8RqFTfYmZyxWerCypuuyHAPAaHAdLjWCl52w2tFQhYnRM76LpcXR1twEKdD4xtcFcFES+seCIZX9G+f5Cubu5zAAs90mFbFezLk63eM0vfV0Z3sRmwAP8IipT8h0hbDFbodrQ/U89zzO9lPFlN0TRqLQ21YDzMRUyx+Mk21yeSJbSKBuQPoRiSfilaHMVNaGbicPjMqZtBtuC97xpIt59dS2nOVjDWwXFSjMOIzuul2Ga5LrB791BT3amO1xfe0MO5J5uiwhojEBMZvNa86J8IcKaP3+G36ri32TKTgwKKnxpzM1bPfdQKcZyb6dZbLxpyQo64zaOgMIbyx3XV0fBE9BukB/paAzcM2btQG0rQEdZEldBL8iGirN14Q1jJzdczhtASbG8QNOO8L0MMHqzFA0DkKvxS0vZKbEllTGSq5wBQeTWxdpkQ5Csv+";
            String jsonString = TdcSM4Util.decryptEcb(SECRET_KEY, FPCY_NEW_RES);
            System.out.println("FPCY_NEW响应1:" + jsonString);
            JSONObject jsonObject = JSON.parseObject(jsonString);
            String cyjgxx = jsonObject.getString("cyjgxx");
            System.out.println("FPCY_NEW响应2:" + TdcLqZipAndBase64Utils.unzipAndBase64Decode(cyjgxx));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("---------------------------------------------------");
        /**
         * QDFPSC
         */
        try {
            String QDFPSC_REQ = "UDVVOXIyUmlTZHRHaVVSdnhGOExDeE41NW5sTTlOSGczNFU3MlZmWFhSYzVBdDlBdm5EWWhkb0ljYUhUUFJvR05QZEFSYXZFaFFqTkhPMzdEYjJYOTVCb2VRUVNLeVhnajQ4MHZsVSt0NXlGV1Z5ZnlJYmpQeDhiK1ZxSTFNdlhXdnNXNjRMUWNXZzNXT1RXeXlqcDJCK1lrMmVKM1lBbDhJdTFkbnVOQ0NzNEJQTGVTYnlmVEhNUkt5NlRJYlNHNXZoWHBCU0QrZUt2UWFuQ2tVcC9KZVRzdHhqTlRlZFQrSFdieUFiQzlWR000eXljeUxJZTBxa3RTQm5PNUVkb0JKWXpkeFZZN3IxUmhyR0tLVUszQU50cEFNUUNTQ0xqOHBrQWZTWHptQmFzek1TbmFxdGQ4ZDgvZXBERXNDanI2RnIzWnc1TjdOaVM5bG5JN05OQjFvaUlmdzVNRVNuY0ZWUkZIVkpsdWU4VWEyMXc0aXNnQ0VibkNqb25DaFM4YlByYkdTV1VkQWZLeC9tMk9qVXBMVS9yRzJzK0hGQ3hyZGRwam9zcnBuYjluMFIrQUxGeng4MGJSOXRTdGdwQlBIMUswTHJLTVovMkJMY3NXZTNhZ0EvSFRMNHNSLzQxZ0NRWEV0aWtqWnhjR25FeGMzaDNxMUdQY09xMHRmanJmN3llZVpKdnhWUTZuUlpKNnpUcUJJVHg3MkxYRUsvZzEyT3k4d041WUZvSTh3d3piZy8zS0I4UG9pdStxTHB5dDV4akcwcW5BMEFlSVM0cDdBV045ZXl4OGppTU5zdUdjOTRnSis1VEcrcXpGSFRZNGN3TmowRTZ5K05zeDhmMWRGejBMS1JPUE85TkFaakdjY1YxMFVxTjBSWlhvV3VNWlVsUEEzejBIUVhkbTRZbUJNWjdLUkhjUnpNVm5iS0hKSTVhV29rUGtPdzRGQTVveThkY0JKS2VXRmZtQ082cWZRczZYbWRPS0pFYzRjd3hXRHBzWlNyRDBHTTN1MlladlFlUFNrcERIUGVONG5HKy90R0xJOFNmZmNVcG5uVDIvRHc5THRXNlFSVE9VMndnUmQ2dkpuQVpyMTNCbWdwV1JKNVdDRFJIMlZmeTMyZHoyelNLVkVrcVorWjJCSERyM3FzVFN3SlRYeHVHbDArZStWbnBGR0V1bHVXSXV0WktpUlZ4WldJYUVmQzlCSUxQc1hBZ09heE52R1pvRm5FTUdjV3dSQzgzOUFnaEgyUnJqZThIUGZlTXB1c0l1TXNPUzByWUlpTXlEZXhZRTZyN3BYTEVEZFRFWFp1YmF6ODVMSTFrQjZPckx4VUk5WUJERVF1MlhTeWQxL2Q3MmF6amVGNENXajVQOHN2TTc5bEpBT3V4cTdrVEFjOTB2MVErTkVKd2c2Tm5jL2FiMnV0eXN2WmcydjZiQTlzSDZ5UjhSMzZ0ekpDbWxDUXBsWTJSaUV6M3JMYS91Z1dQUmhVU0IzQ2hZc0M1c21HQ2J0YWVRUGNOVU9CczJqRG9EM2pnaElQbDg5YmgyR3F0WTJTWlJOcExhcTRNeEVPMGU2cVBIeUZYWll4VXNWcWhFQTlYaWZ3K0d6ZmN2clR4aXRsMHlkam1XbUxLY3NxMkhHVjk4M0h6Q004WlNpRjFvS1B3OTZ2WkE3V255blB3VDFsV3BUcTN1eldoazlmM25kbjByMHdyc2J4ZGhBN3N5WWZZOHhUaytnb2t3d3lYTUdvS2Zja3pVdXVsZlpvcGVnNk5DN0ZNcnhRY1RZZDEvVXl1Z2FmUGpKcExkT2JnMnBjdGpkcEJXTkNFT09jN0JRRnNGbnluWUVsUDhNYzc0TVY0b1paNG5iamNQYUZQbVprdjlQOURQaGU4M0xSeVZnYzB3UFlZV3lCRHpHbHlKT3NhczYzUXN5YWhmbDVlRnFXRzJ3eDZhZHhRUktCQStwZU95VEl0V0RESTV5UE5zV0tiZ2xvd0h6eUl0UGxMbDN0RkxYWHltaEM2a2pFMW92VVpVdEJJSHVhMXNvWXBCanZPUGUzdXVydzVjc3dmRlgzNDU0U2dEUkY3R29lYU14ZFJqR1d2MTNSS3h1TTRsWTZPNnFVL0UyVDkyVElYZEFLdGZLUHBJZTB1aTltNTlxQ3JqMUpjNERhV2tZM2J4Qlp3Zms1R1NtNW0wUVVxVkF0MFJXejlMTlVRRlgwRVdmN0E2Znpubi96WWY5K2pqS1lkNWp3cWtYSTBiZlJXdHFGWlE0VWh0YTIzTUpudy9Ka21Td08yTVpHNWFWQ2dKcm9aV3pCNkRzdUJqdUpNR0JmcEozYTk4MFo5NmI5VjBTVkxhQnpZUlU5aUh5dkJmYnY0Sng0RnFDTE9pVmU4RnNpNkt0TXVGU3o4bFViSWtENHpOamdnTFF2RzU2RVZwcmpYbkpubWZqdnBpSnlXbTg0S25mM21lV2hTWXJaTWdTUWg=";
            String base64Decoded = TdcBase64Util.decode(QDFPSC_REQ, TdcBase64Util.ENCODING);
            String jsonString = TdcSM4Util.decryptEcb(SECRET_KEY, base64Decoded);
            System.out.println("QDFPSC入参:" + jsonString);
            JSONArray jsonArray = JSON.parseArray(jsonString);
            for(int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                System.out.println("QDFPSC发票号码:" + jsonObject.getString("fphm"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            String QDFPSC_RES = "eTbsnzzI7lxD9TjmKFYy9DM00aW7vzMyeX8uU2RKZRUpfrPmMYEHE6alipiQBOlMqbBMnYSGOKu86JmTbsKlGNyNCfX3tDEb1GKPD1JsmZMlk9D94ou0P4HrZyzdQT7H";
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
        /**
         * CXQDFPSCJG
         */
        try {
            String CXQDFPSCJG_REQ = "TVNaV2lSQXdPUGFtNEUreERvM1pjUEV3TG1jOGdBc3VBUCtpcUZBUFk1RUpOMmY4YXAwcE95bStFOUdoQU9RSg==";
            String base64Decoded = TdcBase64Util.decode(CXQDFPSCJG_REQ, TdcBase64Util.ENCODING);
            String jsonString = TdcSM4Util.decryptEcb(SECRET_KEY, base64Decoded);
            System.out.println("CXQDFPSCJG入参:" + jsonString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            String CXQDFPSCJG_RES = "eTbsnzzI7lxD9TjmKFYy9DM00aW7vzMyeX8uU2RKZRWjwyqpffIo01moYZi208/Cy+rjHwMc8fVZclWc47o4NQI2UhbfmN3FiXqUljFOa+zlWoKzg0xF0MVzo6WmIoZHiHtbSdkXqbEmTDSnT5bjjhcPyl+WRH0NBN41pQPrV6A=";
            String CXQDFPSCJG_RES_STR = TdcSM4Util.decryptEcb(SECRET_KEY, CXQDFPSCJG_RES);
            System.out.println("CXQDFPSCJG响应:" + CXQDFPSCJG_RES_STR);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        byte[] bytes = new byte[]{1, 2, 3, 4, 5};
        new String(bytes);
    }
}
