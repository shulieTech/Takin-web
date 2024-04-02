package io.shulie.takin.web.biz.pojo.request.scene;

import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangz
 * Created on 2024/4/1 18:03
 * Email: zz052831@163.com
 */
@Data
public class SceneBaseLineQueryDTO {
    private Long sceneId;
    private Long reportId;
    private Date BaseLineStartTime;
    private Date BaseLineEndTime;
    private int lineTypeEnum;

    public static SceneBaseLineQueryDTO getInstance(SceneManageEntity entity){
        SceneBaseLineQueryDTO sceneBaseLineQueryDTO = new SceneBaseLineQueryDTO();
        sceneBaseLineQueryDTO.setSceneId(entity.getId());
        sceneBaseLineQueryDTO.setReportId(entity.getBaseLineReportId());
        sceneBaseLineQueryDTO.setBaseLineStartTime(entity.getBaseLineStartTime());
        sceneBaseLineQueryDTO.setBaseLineEndTime(entity.getBaseLineEndTime());
        sceneBaseLineQueryDTO.setLineTypeEnum(entity.getLineTypeEnum());
        return sceneBaseLineQueryDTO;
    }
}
