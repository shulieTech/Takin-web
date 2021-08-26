package io.shulie.takin.web.biz.pojo.response.linkmanage;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/10/23 2:33 下午
 */
@Data
public class SupportStatusResponse {
    /**
     * 0：未支持 1：未收录 2:已支持 3:无需支持
     */
    private String label;
    private Integer value;

    public static SupportStatusResponse buildStatus(Integer value) {
        SupportStatusResponse supportStatusResponse = new SupportStatusResponse();
        supportStatusResponse.setValue(value);
        switch (value) {
            case 0:
                supportStatusResponse.setLabel("未支持");
                break;
            case 1:
                supportStatusResponse.setLabel("未收录");
                break;
            case 2:
                supportStatusResponse.setLabel("已支持");
                break;
            case 3:
                supportStatusResponse.setLabel("无需支持");
                break;
            default:
        }
        return supportStatusResponse;
    }
}
