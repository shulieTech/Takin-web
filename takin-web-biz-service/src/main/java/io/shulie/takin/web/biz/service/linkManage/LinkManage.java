package io.shulie.takin.web.biz.service.linkManage;

import java.util.List;
import java.util.Optional;

import com.pamirs.takin.entity.domain.entity.linkmanage.figure.LinkEdge;
import com.pamirs.takin.entity.domain.entity.linkmanage.figure.RpcType;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author vernon
 * @date 2019/12/10 02:06
 */

@Slf4j
@Component
public class LinkManage {

    /**
     * 组装root顶点信息中的入口名
     *
     * @param edge 顶级节点
     * @return
     */
    public static String generateEntrance(LinkEdge edge) {
        StringBuilder builder = new StringBuilder();
        builder.append(edge.getApplicationName())
            .append("|")
            .append(RpcType.getByValue(edge.getRpcType().intValue(), null))
            .append("|")
            .append(edge.getServiceName());
        return builder.toString();
    }

    public static String generateEntranceWithoutAppName(LinkEdge edge) {
        StringBuilder builder = new StringBuilder();
        builder.append(
                RpcType.getByValue(edge.getRpcType().intValue(), null)
            )
            .append("|")
            .append(edge.getServiceName());
        return builder.toString();
    }

    /**
     * 过滤并组装root顶点信息中的入口名
     *
     * @return
     */
    public static String generateEntrance(List<LinkEdge> edges) {
        String result = "";
        Optional<LinkEdge> optional
            = edges.stream().filter(edge -> "0".equals(edge.getRpcId())).findFirst();
        if (optional.get() == null) {
            log.info("获取顶级节点失败...");
            return result;
        }
        LinkEdge root = optional.get();
        result = generateEntrance(root);
        return result;

    }

    /**
     * 组装节点信息
     *
     * @return
     */
    private static String buildNodeInfo(LinkEdge edge) {
        StringBuilder builder = new StringBuilder();
        builder.append(edge.getApplicationName())
            .append("|")
            .append(RpcType.getByValue(edge.getRpcType().intValue(), null))
            .append("|")
            .append(edge.getServiceName());
        return builder.toString();
    }

    public static String buildNodeInfo(List<LinkEdge> edges) {
        StringBuilder builder = new StringBuilder();
        for (LinkEdge edge : edges) {
            builder.append(buildNodeInfo(edge))
                .append(";");
        }
        return builder.toString();
    }

    /**
     * 统计str中包含多少个key
     *
     * @return
     */
    public static int getSubCount(String str, String key) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(key, index)) != -1) {
            index = index + key.length();

            count++;
        }
        return count;
    }

}
