package io.shulie.takin.web.data.dao.application;

import com.baomidou.mybatisplus.extension.service.IService;
import io.shulie.takin.web.data.model.mysql.ShadowJobConfigEntity;

import java.util.List;

/**
 * 影子任务配置 dao 层
 *
 * @author liuchuan
 * @date 2021/4/8 10:50 上午
 */
public interface ShadowJobConfigDAO extends IService<ShadowJobConfigEntity> {

    /**
     * 通过应用id, 获得影子任务
     *
     * @param applicationId 应用id
     * @return 任务列表
     */
    List<ShadowJobConfigEntity> listByApplicationId(Long applicationId);

}
