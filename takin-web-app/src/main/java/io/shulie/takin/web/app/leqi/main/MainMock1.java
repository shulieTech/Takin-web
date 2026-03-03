package io.shulie.takin.web.app.leqi.main;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.shulie.takin.web.app.leqi.HttpResponse;
import io.shulie.takin.web.app.leqi.HttpResquest;
import io.shulie.takin.web.app.leqi.LeQiMain;

public class MainMock1 {

    public static void main(String[] args) {
//        HttpResquest httpRequest1 = new HttpResquest();
//        httpRequest1.setUrl("hjjjjj/access/newsandbox/v2/invoke/203059/FPCY_NEW");
//        httpRequest1.setContent("RTYwbGQ2TGlOSXc4TklNRk50akhPbS83SEpUSkNQRytRVC9ZNzUwNnQ0UU54ckM2SGlzUU1RQzVGdU5Kc2V3VmhRK21wUU5SdEx0MWQraGxXN2tGZHVvTFFRamFudTcvb1htcmlsVitjSkduU3d5N3lUYkJ2SGUvUTRKMGowN1V3Vjc4eGo3S3R2YzlrOHhrUzdQZEVBPT0=");
//        Object[] objs1 = new Object[]{httpRequest1};
//        HttpResponse response1 = MainMock2.doPost(objs1);
//        System.out.println(JSON.toJSONString(response1));
//        System.out.println(new String(response1.getContent()));
//        JSONObject jsonObject1 = JSON.parseObject(new String(response1.getContent()));
//        try {
//            LeQiMain.parseFPCY_NEW_Resp(jsonObject1.getJSONObject("body").getJSONObject("Response").getString("Data"));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

//        HttpResquest httpRequest2 = new HttpResquest();
//        httpRequest2.setUrl("aaa/access/newsandbox/v2/invoke/202007/QDFPSC");
//        httpRequest2.setContent("UDVVOXIyUmlTZHRHaVVSdnhGOExDeE41NW5sTTlOSGczNFU3MlZmWFhSYzVBdDlBdm5EWWhkb0ljYUhUUFJvR05QZEFSYXZFaFFqTkhPMzdEYjJYOTVCb2VRUVNLeVhnajQ4MHZsVSt0NXlGV1Z5ZnlJYmpQeDhiK1ZxSTFNdlhXdnNXNjRMUWNXZzNXT1RXeXlqcDJCK1lrMmVKM1lBbDhJdTFkbnVOQ0NzNEJQTGVTYnlmVEhNUkt5NlRJYlNHNXZoWHBCU0QrZUt2UWFuQ2tVcC9KZVRzdHhqTlRlZFQrSFdieUFiQzlWR000eXljeUxJZTBxa3RTQm5PNUVkb0JKWXpkeFZZN3IxUmhyR0tLVUszQU50cEFNUUNTQ0xqOHBrQWZTWHptQmFzek1TbmFxdGQ4ZDgvZXBERXNDanI2RnIzWnc1TjdOaVM5bG5JN05OQjFvaUlmdzVNRVNuY0ZWUkZIVkpsdWU4VWEyMXc0aXNnQ0VibkNqb25DaFM4YlByYkdTV1VkQWZLeC9tMk9qVXBMVS9yRzJzK0hGQ3hyZGRwam9zcnBuYjluMFIrQUxGeng4MGJSOXRTdGdwQlBIMUswTHJLTVovMkJMY3NXZTNhZ0EvSFRMNHNSLzQxZ0NRWEV0aWtqWnhjR25FeGMzaDNxMUdQY09xMHRmanJmN3llZVpKdnhWUTZuUlpKNnpUcUJJVHg3MkxYRUsvZzEyT3k4d041WUZvSTh3d3piZy8zS0I4UG9pdStxTHB5dDV4akcwcW5BMEFlSVM0cDdBV045ZXl4OGppTU5zdUdjOTRnSis1VEcrcXpGSFRZNGN3TmowRTZ5K05zeDhmMWRGejBMS1JPUE85TkFaakdjY1YxMFVxTjBSWlhvV3VNWlVsUEEzejBIUVhkbTRZbUJNWjdLUkhjUnpNVm5iS0hKSTVhV29rUGtPdzRGQTVveThkY0JKS2VXRmZtQ082cWZRczZYbWRPS0pFYzRjd3hXRHBzWlNyRDBHTTN1MlladlFlUFNrcERIUGVONG5HKy90R0xJOFNmZmNVcG5uVDIvRHc5THRXNlFSVE9VMndnUmQ2dkpuQVpyMTNCbWdwV1JKNVdDRFJIMlZmeTMyZHoyelNLVkVrcVorWjJCSERyM3FzVFN3SlRYeHVHbDArZStWbnBGR0V1bHVXSXV0WktpUlZ4WldJYUVmQzlCSUxQc1hBZ09heE52R1pvRm5FTUdjV3dSQzgzOUFnaEgyUnJqZThIUGZlTXB1c0l1TXNPUzByWUlpTXlEZXhZRTZyN3BYTEVEZFRFWFp1YmF6ODVMSTFrQjZPckx4VUk5WUJERVF1MlhTeWQxL2Q3MmF6amVGNENXajVQOHN2TTc5bEpBT3V4cTdrVEFjOTB2MVErTkVKd2c2Tm5jL2FiMnV0eXN2WmcydjZiQTlzSDZ5UjhSMzZ0ekpDbWxDUXBsWTJSaUV6M3JMYS91Z1dQUmhVU0IzQ2hZc0M1c21HQ2J0YWVRUGNOVU9CczJqRG9EM2pnaElQbDg5YmgyR3F0WTJTWlJOcExhcTRNeEVPMGU2cVBIeUZYWll4VXNWcWhFQTlYaWZ3K0d6ZmN2clR4aXRsMHlkam1XbUxLY3NxMkhHVjk4M0h6Q004WlNpRjFvS1B3OTZ2WkE3V255blB3VDFsV3BUcTN1eldoazlmM25kbjByMHdyc2J4ZGhBN3N5WWZZOHhUaytnb2t3d3lYTUdvS2Zja3pVdXVsZlpvcGVnNk5DN0ZNcnhRY1RZZDEvVXl1Z2FmUGpKcExkT2JnMnBjdGpkcEJXTkNFT09jN0JRRnNGbnluWUVsUDhNYzc0TVY0b1paNG5iamNQYUZQbVprdjlQOURQaGU4M0xSeVZnYzB3UFlZV3lCRHpHbHlKT3NhczYzUXN5YWhmbDVlRnFXRzJ3eDZhZHhRUktCQStwZU95VEl0V0RESTV5UE5zV0tiZ2xvd0h6eUl0UGxMbDN0RkxYWHltaEM2a2pFMW92VVpVdEJJSHVhMXNvWXBCanZPUGUzdXVydzVjc3dmRlgzNDU0U2dEUkY3R29lYU14ZFJqR1d2MTNSS3h1TTRsWTZPNnFVL0UyVDkyVElYZEFLdGZLUHBJZTB1aTltNTlxQ3JqMUpjNERhV2tZM2J4Qlp3Zms1R1NtNW0wUVVxVkF0MFJXejlMTlVRRlgwRVdmN0E2Znpubi96WWY5K2pqS1lkNWp3cWtYSTBiZlJXdHFGWlE0VWh0YTIzTUpudy9Ka21Td08yTVpHNWFWQ2dKcm9aV3pCNkRzdUJqdUpNR0JmcEozYTk4MFo5NmI5VjBTVkxhQnpZUlU5aUh5dkJmYnY0Sng0RnFDTE9pVmU4RnNpNkt0TXVGU3o4bFViSWtENHpOamdnTFF2RzU2RVZwcmpYbkpubWZqdnBpSnlXbTg0S25mM21lV2hTWXJaTWdTUWg=");
//        Object[] objs2 = new Object[]{httpRequest2};
//        HttpResponse response2 = MainMock2.doPost(objs2);
//        System.out.println(JSON.toJSONString(response2));
//        System.out.println(new String(response2.getContent()));
//        JSONObject jsonObject2 = JSON.parseObject(new String(response2.getContent()));
//        try {
//            LeQiMain.parseQDFPSC_Req(jsonObject2.getJSONObject("body").getJSONObject("Response").getString("Data"));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        HttpResquest httpRequest3 = new HttpResquest();
//        httpRequest3.setUrl("aaa/access/newsandbox/v2/invoke/202007/CXQDFPSCJG");
//        httpRequest3.setContent("TVNaV2lSQXdPUGFtNEUreERvM1pjUEV3TG1jOGdBc3VBUCtpcUZBUFk1RUpOMmY4YXAwcE95bStFOUdoQU9RSg==");
//        Object[] objs3 = new Object[]{httpRequest3};
//        HttpResponse response3 = MainMock2.doPost(objs3);
//        System.out.println(JSON.toJSONString(objs3));
//        System.out.println(new String(response3.getContent()));
//        JSONObject jsonObject3 = JSON.parseObject(new String(response3.getContent()));
//        try {
//            LeQiMain.parseQDFPSC_Req(jsonObject3.getJSONObject("body").getJSONObject("Response").getString("Data"));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        HttpResquest httpRequest4 = new HttpResquest();
        httpRequest4.setUrl("aaa/access/newsandbox/v2/invoke/202007/CXSXED");
        httpRequest4.setContent("Z2c5THRxdDdoSi9qWjhrVkoya0xobHA5VkM0akZaUjZCOHFTRGFsWUpMST0=");
        Object[] objs4 = new Object[]{httpRequest4};
        HttpResponse response4 = MainMock2.doPost(objs4);
        System.out.println(JSON.toJSONString(response4));
        System.out.println(new String(response4.getContent()));
        JSONObject jsonObject4 = JSON.parseObject(new String(response4.getContent()));
        try {
            LeQiMain.parseQDFPSC_Req(jsonObject4.getJSONObject("body").getJSONObject("Response").getString("Data"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
