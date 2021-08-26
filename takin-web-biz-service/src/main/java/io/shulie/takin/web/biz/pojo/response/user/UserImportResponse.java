package io.shulie.takin.web.biz.pojo.response.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/3/8 2:50 下午
 */
@Data
public class UserImportResponse {

    @ApiModelProperty(name = "success", value = "是否导入成功")
    private Boolean success;

    @ApiModelProperty(name = "errorMsg", value = "错误消息")
    private String errorMsg;

    @ApiModelProperty(name = "writeBack", value = "是否需要回写文件")
    private Boolean writeBack;

    @ApiModelProperty(name = "filePath", value = "文件地址")
    private String filePath;

}
