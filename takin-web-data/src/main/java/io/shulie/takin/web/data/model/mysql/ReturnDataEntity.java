package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

/**
 * 数据回传JAR配置
 */
@Data
@TableName(value = "t_return_data")
public class ReturnDataEntity extends TenantBaseEntity {
    /**
     * 回传id
     */
    @TableId(value = "TRD_ID", type = IdType.INPUT)
    private Long trdId;

    /**
     * JAR包名称
     */
    @TableField(value = "INC_DATA_JAR_NAME")
    private String incDataJarName;

    /**
     * JAR包保存路径
     */
    @TableField(value = "INC_DATA_JAR_PATH")
    private String incDataJarPath;

    /**
     * 负责人工号
     */
    @TableField(value = "PRINCIPAL_NO")
    private String principalNo;

    /**
     * 链路ID
     */
    @TableField(value = "LINK_ID")
    private Long linkId;

    /**
     * 启动类
     */
    @TableField(value = "START_CLASS")
    private String startClass;

    /**
     * 抽数状态(1表示正在运行,2表示运行结束)
     */
    @TableField(value = "LOAD_STATUS")
    private Integer loadStatus;

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
}
