package io.shulie.takin.web.data.result.linkmange;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/15 11:00 上午
 */
@Data
public class SceneResult {
    private Long id;
    private String sceneName;
    private Long tenantId;
    private Long userId;
}
