package io.shulie.takin.web.data.dao.activity;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import io.shulie.takin.web.data.model.mysql.ActivityCategoryEntity;
import org.apache.commons.lang3.StringUtils;

public interface ActivityCategoryDAO extends IService<ActivityCategoryEntity> {

    long ROOT_ID = 0; // 前端默认0=全部(根节点)
    long ROOT_PARENT_ID = -1;
    String ROOT_NAME = "全部";
    String RELATION_CODE_DELIMITER = "_";
    String ROOT_RELATION_PATH = String.valueOf(ROOT_ID);

    static String completedEndIfNecessary(String relationCode) {
        if (StringUtils.isBlank(relationCode) || StringUtils.endsWith(relationCode, RELATION_CODE_DELIMITER)) {
            return relationCode;
        }
        return relationCode.concat(RELATION_CODE_DELIMITER);
    }

    List<ActivityCategoryEntity> queryChildren(Long parentId);

    boolean exists(Long id);

    ActivityCategoryEntity findById(Long id);

    List<ActivityCategoryEntity> findByIds(List<Long> ids);

    boolean deleteById(Long id);

    boolean updateById(ActivityCategoryEntity entity);

    boolean save(ActivityCategoryEntity entity);

    List<ActivityCategoryEntity> list();

    void updateRelationPath(Long categoryId, String relationPath);

    List<Long> startWithRelationPath(String relationPath);
}
