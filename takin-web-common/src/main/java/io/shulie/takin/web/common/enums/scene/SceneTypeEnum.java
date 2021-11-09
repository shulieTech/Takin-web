package io.shulie.takin.web.common.enums.scene;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务流程枚举类型
 */
@Getter
@AllArgsConstructor
public enum SceneTypeEnum {

    NORMAL_SCENE(0,"手工创建枚举"),
    JMETER_UPLOAD_SCENE(1,"jmeter上传创建");
    private Integer type;
    private String desc;
}
