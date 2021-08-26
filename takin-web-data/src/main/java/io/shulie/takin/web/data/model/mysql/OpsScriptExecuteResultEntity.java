package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 运维脚本执行结果(OpsScriptExecuteResult)实体类
 *
 * @author caijy
 * @date 2021-06-16 10:35:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_ops_script_execute_result")
public class OpsScriptExecuteResultEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -97573133771422558L;

    /**
     * 文件上传ID
     */
    private String uploadId;
    /**
     * 批次号id
     */
    private Long batchId;

    /**
     * 运维脚本ID
     */
    private Long opsScriptId;

    /**
     * 日志文件路径
     */
    private String logFilePath;

    /**
     * 执行人id
     */
    private Long excutorId;

    /**
     * 执行时间
     */
    private Date executeTime;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtUpdate;

}
