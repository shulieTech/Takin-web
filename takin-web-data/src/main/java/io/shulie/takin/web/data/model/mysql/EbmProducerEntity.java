package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * ESB/IBM生产消息数据表
 */
@Data
@TableName(value = "t_ebm_producer")
public class EbmProducerEntity {
    /**
     * ESB/IBM生产者id
     */
    @TableId(value = "TEP_ID", type = IdType.INPUT)
    private Long tepId;

    /**
     * 消息类型(1表示ESB,2表示IBM)
     */
    @TableField(value = "MSG_TYPE")
    private String msgType;

    /**
     * 数据字典类型（ID值）
     */
    @TableField(value = "DICT_TYPE")
    private String dictType;

    /**
     * 消息地址
     */
    @TableField(value = "MSG_HOST")
    private String msgHost;

    /**
     * 消息端口
     */
    @TableField(value = "MSG_PORT")
    private String msgPort;

    /**
     * 队列管理器
     */
    @TableField(value = "QUEUE_MANAGER")
    private String queueManager;

    /**
     * 系统队列通道
     */
    @TableField(value = "QUEUE_CHANNEL")
    private String queueChannel;

    /**
     * 编码字符集标识符
     */
    @TableField(value = "CCSID")
    private String ccsid;

    /**
     * ESBCODE
     */
    @TableField(value = "ESBCODE")
    private String esbcode;

    /**
     * REQUESTCOMOUT
     */
    @TableField(value = "REQUEST_COMOUT")
    private String requestComout;

    /**
     * 消息休眠时间,默认为毫秒
     */
    @TableField(value = "SLEEP_TIME")
    private Long sleepTime;

    /**
     * 消息发送数量,默认为条
     */
    @TableField(value = "MSG_COUNT")
    private Long msgCount;

    /**
     * 消息发送成功条数
     */
    @TableField(value = "MSG_SUCCESS_COUNT")
    private Long msgSuccessCount;

    /**
     * 线程数量
     */
    @TableField(value = "THREAD_COUNT")
    private Long threadCount;

    /**
     * 消息体大小
     */
    @TableField(value = "MESSAGE_SIZE")
    private Long messageSize;

    /**
     * 生产消息状态, 0未生产消息 1正在生产消息 2已停止生产消息 3生产消息失败 4正在生产但是有发送失败的数据
     */
    @TableField(value = "PRODUCE_STATUS")
    private String produceStatus;

    /**
     * 开始生产消息时间
     */
    @TableField(value = "PRODUCE_START_TIME")
    private LocalDateTime produceStartTime;

    /**
     * 停止生产消息时间
     */
    @TableField(value = "PRODUCE_END_TIME")
    private LocalDateTime produceEndTime;

    /**
     * 上次停止生产时间
     */
    @TableField(value = "LAST_PRODUCE_TIME")
    private LocalDateTime lastProduceTime;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME")
    private LocalDateTime updateTime;

    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
