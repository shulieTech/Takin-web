package io.shulie.takin.web.biz.service.dsManage;

import io.shulie.takin.web.biz.convert.db.parser.style.StyleTemplate;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsCreateInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsDeleteInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsEnableInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInput;
import io.shulie.takin.web.biz.pojo.output.application.ApplicationDsDetailOutput;
import io.shulie.takin.web.biz.pojo.response.application.ShadowDetailResponse;
import io.shulie.takin.web.common.common.Response;

/**
 * @author HengYu
 * @className AbstractDsService
 * @date 2021/4/12 9:25 下午
 * @description 数据源存储抽象服务
 */
public abstract class AbstractDsService implements StyleTemplate {

    /**
     * 添加数据源
     *
     * @param createRequest 创建对象
     *
     * @return 响应对象
     */
    public abstract Response dsAdd(ApplicationDsCreateInput createRequest);

    /**
     * 更新数据源
     *
     * @param updateRequest 更新对象
     *
     * @return 响应对象
     */
    public abstract Response dsUpdate(ApplicationDsUpdateInput updateRequest);

    /**
     * 查询数据源类型
     *
     * @param dsId         -
     * @param isOldVersion -
     *
     * @return -
     */
    public abstract Response<ApplicationDsDetailOutput> dsQueryDetail(Long dsId, boolean isOldVersion);

    /**
     * 启用配置
     *
     * @return -
     */
    public abstract Response enableConfig(ApplicationDsEnableInput enableRequest);

    /**
     * 删除数据源
     *
     * @return -
     */
    public abstract Response dsDelete(ApplicationDsDeleteInput dsDeleteRequest);

    /**
     * 老数据映射成新的结构
     * @param recordId
     * @return
     */
    public ShadowDetailResponse convertDetailByTemplate(Long recordId) {
        return null;
    }

}
