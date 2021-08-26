package io.shulie.takin.web.biz.service.linkManage.impl;

import io.shulie.takin.web.common.vo.whitelist.WhiteListVO;
import org.junit.Assert;
import org.junit.Test;

public class WhiteListServiceImplTest {

    @Test
    public void whiteListCompare() {
        WhiteListServiceImpl serviceImpl = new WhiteListServiceImpl();
        WhiteListVO o1 = new WhiteListVO();
        o1.setInterfaceName("abc");
        WhiteListVO o2 = new WhiteListVO();
        o2.setInterfaceName("bc");
        int i = serviceImpl.whiteListCompare(o1, o2);
        Assert.assertEquals(-1, i);
    }
}