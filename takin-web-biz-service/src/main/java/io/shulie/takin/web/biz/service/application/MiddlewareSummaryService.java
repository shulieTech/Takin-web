package io.shulie.takin.web.biz.service.application;

import com.baomidou.mybatisplus.extension.service.IService;
import io.shulie.takin.web.data.model.mysql.MiddlewareSummaryEntity;

public interface MiddlewareSummaryService extends IService<MiddlewareSummaryEntity> {
    void edit(MiddlewareSummaryEntity middlewareSummaryEntity);
}
