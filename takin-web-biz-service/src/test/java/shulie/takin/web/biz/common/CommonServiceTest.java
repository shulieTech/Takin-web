package shulie.takin.web.biz.common;

import com.alibaba.druid.filter.config.ConfigTools;

import io.shulie.takin.web.biz.common.CommonService;

/**
 * @author 何仲奇
 * @date 2020/9/2 9:21 下午
 * @see CommonService
 */
public class CommonServiceTest {
    public static void main(String[] args) throws Exception {
        //        System.out.println("数据执行开始===============================");
        //        CommonService commonService = new CommonService();
        //        Conf conf = new Conf();
        //        conf.setDriverClassName("com.mysql.jdbc.Driver");
        //        conf.setUsername("takin");
        //        conf.setUrl("jdbc:mysql://10.230.44
        //        .195:3306/takindb?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true");
        //        conf.setPublicKey
        //        ("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKHGwq7q2RmwuRgKxBypQHw0mYu4BQZ3eMsTrdK8E6igRcxsobUC7uT0SoxIjl1WveWniCASejoQtn/BY6hVKWsCAwEAAQ
        //        ==");
        //        conf.setPasswd("VWDp5x/5WngodWOMMevcGud8SzKYcL30x1CUxEmcVWNUQ0fB1YMKD
        //        +7k9loyc0YoQksT2EjoprCtmwpoUX5ZVA==");
        //
        //        Connection connection = commonService.getConnection(conf);
        //
        //        String sql = "insert into user(id,username,password,user_group,create_date)values(?,?,?,?,now())";
        //        StringBuffer stringBuffer = new StringBuffer(sql);
        //
        //        List<List<Object>> allcolumnList = Lists.newArrayListWithCapacity(10000);
        //
        //        for (int i = 1; i <= 1000000; i++) {
        //            List<Object> columnList = Lists.newArrayListWithCapacity(4);
        //            columnList.add(i);
        //            columnList.add("JasonYan_" + i);
        //            columnList.add(TroRandomUtil.getRandomString(9));
        //            columnList.add("takin_GROUP_" + i);
        //            allcolumnList.add(columnList);
        //        }
        //        commonService.batchExecute(sql, allcolumnList, conf);
        //
        //        System.out.println("数据执行完成===============================");
        //        String publickey =
        //        "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKHGwq7q2RmwuRgKxBypQHw0mYu4BQZ3eMsTrdK8E6igRcxsobUC7uT0SoxIjl1WveWniCASejoQtn/BY6hVKWsCAwEAAQ==";
        //        String decrypt = ConfigTools.decrypt
        //        ("VWDp5x/5WngodWOMMevcGud8SzKYcL30x1CUxEmcVWNUQ0fB1YMKD+7k9loyc0YoQksT2EjoprCtmwpoUX5ZVA==");
        //        System.out.println(decrypt);
        String encrypt = ConfigTools.encrypt("athene.admin");
        System.out.println(encrypt);
        System.out.println(ConfigTools.decrypt(encrypt));
        //        System.out.println(ConfigTools.decrypt(publickey,
        //        "IkoYifewrqLgzh3ncFNwnEqcw5yyjyaYeofaG9S1os0EDK3Yt44d37q1Wi2mjNiOQM8SEU5q0jcnbmpHEx2EFg=="));
    }

}
