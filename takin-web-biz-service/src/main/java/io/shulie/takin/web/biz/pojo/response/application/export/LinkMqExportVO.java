package io.shulie.takin.web.biz.pojo.response.application.export;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 链路配置导出-mq相关
 */
@Data
public class LinkMqExportVO {

    @ExcelProperty(value ="请求入口url",index = 0)
    private String entranceUrl;

    @ExcelProperty(value ="topic",index = 1)
    private String topic;

    @ExcelProperty(value ="生产/消费组",index = 2)
    private String group;

    @ExcelProperty(value ="MQ类型",index = 3)
    private String mqType;

    @ExcelProperty(value ="调用应用",index = 4)
    private String sourceApplication;

    @ExcelProperty(value ="生产/消费",index = 5)
    private String type;

    @ExcelProperty(value ="隔离方案",index = 6)
    private String quarantineMethod;

    @ExcelProperty(value ="是否影子集群",index = 7)
    private String isCluster;

    @ExcelProperty(value ="集群地址",index = 8)
    private String clusterAddress;

    @ExcelProperty(value ="集群名称",index = 9)
    private String clusterName;

    @ExcelProperty(value ="消费线程数",index = 10)
    private String consumerThreadNum;
}
