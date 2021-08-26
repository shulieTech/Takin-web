package com.pamirs.takin.common.util;

import io.shulie.takin.web.common.util.whitelist.WhitelistUtil;
import org.junit.Test;

public class WhitelistUtilTest {

    @Test
    public void checkWhiteFormat() {
        // 空格
        String interfaceName = " ";
        assert  WhitelistUtil.checkWhiteFormatError(interfaceName,5);
        interfaceName = "*";
        assert  WhitelistUtil.checkWhiteFormatError(interfaceName,5);
        interfaceName = "/";
        assert  WhitelistUtil.checkWhiteFormatError(interfaceName,5);
        interfaceName = "//";
        assert  WhitelistUtil.checkWhiteFormatError(interfaceName,5);
        interfaceName = "// ";
        assert  WhitelistUtil.checkWhiteFormatError(interfaceName,5);
        interfaceName = "//asaas";
        assert  !WhitelistUtil.checkWhiteFormatError(interfaceName,5);
    }
}
