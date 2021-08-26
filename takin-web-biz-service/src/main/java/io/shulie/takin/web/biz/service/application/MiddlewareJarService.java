package io.shulie.takin.web.biz.service.application;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import io.shulie.takin.web.biz.pojo.dto.application.CompareApplicationMiddlewareDTO;
import io.shulie.takin.web.biz.pojo.response.application.MiddlewareCompareResponse;
import io.shulie.takin.web.biz.pojo.response.application.MiddlewareImportResponse;
import io.shulie.takin.web.data.model.mysql.MiddlewareJarEntity;
import io.shulie.takin.web.data.model.mysql.MiddlewareSummaryEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MiddlewareJarService extends IService<MiddlewareJarEntity> {

    MiddlewareCompareResponse compareMj(List<MultipartFile> files);

    MiddlewareImportResponse importMj(MultipartFile file);

    /**
     * 提供给application对比的接口
     */
    void appCompare(List<CompareApplicationMiddlewareDTO> compareApplicationMiddlewareList);

    void reCompute(MiddlewareSummaryEntity middlewareSummaryEntity);

    void edit(MiddlewareJarEntity middlewareJarEntity);
}
