package shulie.takin.web.biz.service.linkTopology;

import io.shulie.amdb.common.enums.EdgeTypeEnum;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author 无涯
 * @date 2021/3/23 4:59 下午
 */
public class LinkTopologyServiceTest {
    @Test
    public void EdgeTypeEnumTest() {
        System.out.println(EdgeTypeEnum.getEdgeTypeEnum("weblogic") != EdgeTypeEnum.UNKNOWN);
        Assert.assertEquals(EdgeTypeEnum.getEdgeTypeEnum("weblogic") != EdgeTypeEnum.UNKNOWN, true);
    }
}
