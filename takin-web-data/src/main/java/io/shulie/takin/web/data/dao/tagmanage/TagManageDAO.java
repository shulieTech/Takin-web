package io.shulie.takin.web.data.dao.tagmanage;

import java.util.List;

import io.shulie.takin.web.data.param.tagmanage.TagManageParam;
import io.shulie.takin.web.data.result.tagmanage.TagManageResult;

/**
 * @author zhaoyong
 */
public interface TagManageDAO {
    /**
     * 查询所有脚本标签
     *
     * @return
     */
    List<TagManageResult> selectAllScript();

    /**
     * 新增脚本tag
     *
     * @param tagManageParams
     * @return
     */
    List<Long> addScriptTags(List<TagManageParam> tagManageParams,Integer tagType);

    /**
     * 根据id批量查询脚本标签
     *
     * @param tagIds
     * @return
     */
    List<TagManageResult> selectScriptTagsByIds(List<Long> tagIds);

    /**
     * 查询所有数据源标签
     *
     * @return
     */
    List<TagManageResult> selectDataSourceTags();

    /**
     * 根据id批量查询数据源标签
     *
     * @return
     */
    List<TagManageResult> selectDataSourceTagsByIds(List<Long> tagIds);

    /**
     * 新增数据源tag
     *
     * @param tagManageParams
     * @return
     */
    List<Long> addDatasourceTags(List<TagManageParam> tagManageParams);

    /**
     *获取某类型的所有标签
     * @param type
     * @return
     */
    List<TagManageResult> selectTagByType(Integer type) ;

    /**
     * 新增tag
     *
     * @param tagManageParams
     * @return
     */
    List<TagManageResult> addTags(List<TagManageParam> tagManageParams);

    /**
     * 根据name和类型批量查询数据源标签
     *
     * @return
     */
    List<TagManageResult> selectDataSourceTagsByNamesAndType(List<String> tagNames,Integer type);
}
