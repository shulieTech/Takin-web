package io.shulie.takin.web.biz.pojo.response.application.export;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.util.Objects;

/**
 * 链路配置导出-mq相关
 */
@Data
public class LinkMqExportVO {

    @ColumnWidth(20)
    @ExcelProperty(value ="请求入口url",index = 0)
    private String entranceUrl;

    @ColumnWidth(10)
    @ExcelProperty(value ="topic",index = 1)
    private String topic;

    @ColumnWidth(10)
    @ExcelProperty(value ="生产/消费组",index = 2)
    private String group;

    @ColumnWidth(10)
    @ExcelProperty(value ="MQ类型",index = 3)
    private String mqType;

    @ColumnWidth(25)
    @ExcelProperty(value ="调用应用",index = 4)
    private String sourceApplication;

    @ColumnWidth(10)
    @ExcelProperty(value ="生产/消费",index = 5)
    private String type;

    @ExcelProperty(value ="隔离方案",index = 6)
    private String quarantineMethod;

    @ColumnWidth(15)
    @ExcelProperty(value ="是否影子集群",index = 7)
    private String isCluster;

    @ColumnWidth(20)
    @ExcelProperty(value ="集群地址",index = 8)
    private String clusterAddress;

    @ExcelProperty(value ="集群名称",index = 9)
    private String clusterName;

    @ColumnWidth(10)
    @ExcelProperty(value ="消费线程数",index = 10)
    private String consumerThreadNum;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkMqExportVO that = (LinkMqExportVO) o;
        return Objects.equals(entranceUrl, that.entranceUrl) && Objects.equals(topic, that.topic) && Objects.equals(group, that.group) && Objects.equals(mqType, that.mqType) && Objects.equals(sourceApplication, that.sourceApplication) && Objects.equals(type, that.type) && Objects.equals(quarantineMethod, that.quarantineMethod) && Objects.equals(isCluster, that.isCluster) && Objects.equals(clusterAddress, that.clusterAddress) && Objects.equals(clusterName, that.clusterName) && Objects.equals(consumerThreadNum, that.consumerThreadNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entranceUrl, topic, group, mqType, sourceApplication, type, quarantineMethod, isCluster, clusterAddress, clusterName, consumerThreadNum);
    }
}
