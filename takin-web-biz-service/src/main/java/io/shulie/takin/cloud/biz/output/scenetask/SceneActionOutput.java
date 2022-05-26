package io.shulie.takin.cloud.biz.output.scenetask;

import java.util.List;

import lombok.Data;

/**
 * @author qianshui
 * @date 2020/11/13 上午10:58
 */
@Data
public class SceneActionOutput {

    private Long data;

    private Long reportId;

    private List<String> msg;
}
