package io.shulie.takin.web.biz.service.scene;

import java.util.List;

/**
 * @author qianshui
 * @date 2020/8/12 上午9:50
 */
public interface ApplicationBusinessActivityService {

    /**
     * 根据业务活动id,查询关联应用名
     *
     * @return
     */
    List<String> processAppNameByBusinessActiveId(Long businessActivityId);

}
