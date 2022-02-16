package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/25 4:13 下午
 */
@Data
public class PluginConfigDetailResponse {
    @ApiModelProperty(name = "type", value = "插件类型")
    private String type;

    @ApiModelProperty(name = "name", value = "插件名称")
    private String name;

    @ApiModelProperty(name = "id", value = "插件id")
    private String id;

    @ApiModelProperty(name = "version", value = "插件版本")
    private String version;

    /**
     * 为了兼容老版本，老版本使用name，值填充了id，新版本使用id作为id本身，name目前没有具体左右，先这样改动
     * @return
     */
    public String getName() {
        if (id != null){
            return id;
        }
        return name;
    }
}
