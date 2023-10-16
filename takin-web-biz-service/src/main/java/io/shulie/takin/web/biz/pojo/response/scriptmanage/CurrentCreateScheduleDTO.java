package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import io.shulie.takin.web.biz.constant.ScriptCsvCreateTaskState;
import lombok.Data;

/**
* @Package io.shulie.takin.web.biz.pojo.response.scriptmanage
* @ClassName: CurrentCreateScheduleDTO
* @author hezhongqi
* @description:
* @date 2023/10/10 14:55
*/
@Data
public class CurrentCreateScheduleDTO {
    /**
     * 上传文件 位置
     */
    private String uploadId;
    /**
     * 上传文件地址
     */
    private String tempPath;

    /**
     * 记录当前获取的数据数量 同时也是实际数量
     */
    private Long current;

    /**
     * 页码数据
     */
    private Integer page;

    /**
     * 每页查询数量 默认 10000
     */
    private Integer size = 10000;

    /**
     * 总分页
     */
    private Integer totalPage;

    /**
     * 是否忽略首行
     */
    private Boolean ignoreFirstLine;


    /**
     * csv组件变量
     */
    private String  scriptCsvVariableName;

    /**
     * 数据总量
     */
    private Long total;


    /**
     * 前端设置需要数量多少
     */
    private Long count;


    public String parseCurrentCreateSchedule(Integer createStatus) {
        // 进度计算
        String currentCreateSchedule = "0%";
        if (ScriptCsvCreateTaskState.IN_FORMATION.equals(createStatus)) {
            Long count = this.count;
            Long current = this.current;
            if (!(current == null || count == 0)) {
                currentCreateSchedule = (current * 100 / count ) + "%";
            }
        } else if (ScriptCsvCreateTaskState.GENERATED.equals(createStatus)) {
            currentCreateSchedule = "100%";
        }
        return currentCreateSchedule;
    }

}
