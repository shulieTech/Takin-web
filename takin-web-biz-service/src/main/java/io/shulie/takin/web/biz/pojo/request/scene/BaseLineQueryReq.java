package io.shulie.takin.web.biz.pojo.request.scene;

import io.shulie.takin.adapter.api.model.request.scenemanage.SceneBaseLineTypeEnum;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import lombok.Data;

/**
 * @author zhangz
 * Created on 2024/3/12 11:10
 * Email: zz052831@163.com
 */

@Data
public class BaseLineQueryReq {
    private Long sceneId;
    private Long reportId;
    private Long baseLineStartTime;
    private Long baseLineEndTime;
    private Integer lineTypeEnum;

    public static BaseLineQueryReq getBaseLineReq(SceneManageEntity sceneManageEntity) {
        BaseLineQueryReq baseLineQueryReq = new BaseLineQueryReq();
        baseLineQueryReq.setSceneId(sceneManageEntity.getId());
        if (sceneManageEntity.getLineTypeEnum() == SceneBaseLineTypeEnum.REPORT.getType()) {
            baseLineQueryReq.setReportId(sceneManageEntity.getBaseLineReportId());
        }
        baseLineQueryReq.setBaseLineStartTime(sceneManageEntity.getBaseLineStartTime().getTime());
        baseLineQueryReq.setBaseLineEndTime(sceneManageEntity.getBaseLineEndTime().getTime());
        baseLineQueryReq.setLineTypeEnum(sceneManageEntity.getLineTypeEnum());
        return baseLineQueryReq;
    }

    public static BaseLineQueryReq getCurrentLineReq(SceneManageEntity sceneManageEntity, ReportEntity reportEntity){
        BaseLineQueryReq baseLineQueryReq = new BaseLineQueryReq();
        baseLineQueryReq.setSceneId(sceneManageEntity.getId());
        baseLineQueryReq.setReportId(reportEntity.getId());
        baseLineQueryReq.setBaseLineStartTime(reportEntity.getStartTime().getTime());
        baseLineQueryReq.setBaseLineEndTime(reportEntity.getEndTime().getTime());
        baseLineQueryReq.setLineTypeEnum(sceneManageEntity.getLineTypeEnum());
        return baseLineQueryReq;
    }

    public static BaseLineQueryReq genReqBySceneManageEntity(SceneManageEntity sceneManageEntity){
        BaseLineQueryReq baseLineQueryReq = new BaseLineQueryReq();
        baseLineQueryReq.setSceneId(sceneManageEntity.getId());
        baseLineQueryReq.setReportId(sceneManageEntity.getBaseLineReportId());
        baseLineQueryReq.setBaseLineStartTime(sceneManageEntity.getBaseLineStartTime().getTime());
        baseLineQueryReq.setBaseLineEndTime(sceneManageEntity.getBaseLineEndTime().getTime());
        baseLineQueryReq.setLineTypeEnum(sceneManageEntity.getLineTypeEnum());
        return baseLineQueryReq;
    }
}
