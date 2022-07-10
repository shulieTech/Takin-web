package io.shulie.takin.web.biz.service.activity;

import java.util.List;

import io.shulie.takin.web.biz.pojo.request.activity.ActivityCategoryCreateRequest;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCategoryUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityCategoryTreeResponse;

public interface ActivityCategoryService {

    ActivityCategoryTreeResponse list();

    Long addCategory(ActivityCategoryCreateRequest createRequest);

    void updateCategory(ActivityCategoryUpdateRequest updateRequest);

    void deleteCategory(Long categoryId);

    /**
     * 查找指定节点及其下级节点集合
     *
     * @param categoryId 节点Id
     * @return 节点及其下级节点集合
     */
    List<Long> findDescendants(Long categoryId);

    void move(Long fromCategoryId, Long toCategoryId);
}
