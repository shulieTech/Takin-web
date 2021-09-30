package com.pamirs.takin.common.util;

import io.shulie.takin.web.common.secure.SecureUtil;
import io.shulie.takin.web.common.util.RedisHelper;
import org.junit.Assert;
import org.junit.Test;

public class SecureUtilTest {

    @Test
    public void encrypt() {
        String test = "abc";
        String decrypt = SecureUtil.encryption(test);
        String decrypt1 = SecureUtil.decrypt(decrypt);
        Assert.assertEquals(decrypt1,test);
    }


    @Test
    public void test(){
        System.out.println(RedisHelper.generateRedisKey("ee", "xx", "xx2"));
    }

}