package io.shulie.takin.web.biz.service.pressureresource;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelateDsInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelateDsRequest;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceRelateDsVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 压测资源配置-数据源
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:53 PM
 */
public interface PressureResourceDsService {
    /**
     * 数据源新增
     */
    void add(PressureResourceRelateDsInput input);

    /**
     * 数据源新增
     */
    String getDsKey(Long dsId);

    /**
     * 数据源新增
     */
    void update(PressureResourceRelateDsInput input);

    /**
     * 数据源删除
     */
    void del(Long dsId);

    /**
     * 数据源视图页面
     */
    PagingList<PressureResourceRelateDsVO> listByDs(PressureResourceRelateDsRequest request);

    /**
     * 应用视图页面
     */
    PagingList<PressureResourceRelateDsVO> listByApp(PressureResourceRelateDsRequest request);

    /**
     * 导入
     *
     * @param file
     * @param resouceId
     */
    void importDsConfig(MultipartFile file, Long resouceId);

    /**
     * 导出
     *
     * @param response
     * @param resourceId
     */
    void export(HttpServletResponse response, Long resourceId);
}
