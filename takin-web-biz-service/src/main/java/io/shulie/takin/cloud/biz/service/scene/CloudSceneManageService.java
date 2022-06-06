package io.shulie.takin.cloud.biz.service.scene;

import java.math.BigDecimal;
import java.util.List;

import com.github.pagehelper.PageInfo;
import com.pamirs.takin.cloud.entity.domain.vo.scenemanage.SceneManageStartRecordVO;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageQueryInput;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneManageWrapperInput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageListOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput.SceneBusinessActivityRefOutput;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOptions;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.ext.content.asset.AssetBillExt;
import io.shulie.takin.cloud.ext.content.script.ScriptVerityRespExt;
import io.shulie.takin.adapter.api.model.request.scenemanage.CloudUpdateSceneFileRequest;

/**
 * @author qianshui
 * @date 2020/4/17 下午3:31
 */
public interface CloudSceneManageService {

    /**
     * 新增场景
     *
     * @param wrapperVO 包装参数
     * @return -
     */
    Long addSceneManage(SceneManageWrapperInput wrapperVO);

    /**
     * 分页查询列表
     *
     * @param queryVO 查询条件
     * @return 查询结果
     */
    PageInfo<SceneManageListOutput> queryPageList(SceneManageQueryInput queryVO);

    /**
     * 更新场景
     *
     * @param wrapperVO 入参
     */
    void updateSceneManage(SceneManageWrapperInput wrapperVO);

    /**
     * 更新场景状态
     *
     * @param statusVO 入参
     */
    void updateSceneManageStatus(UpdateStatusBean statusVO);

    /**
     * 删除
     *
     * @param id 场景主键
     */
    void delete(Long id);

    /**
     * 获取场景
     *
     * @param id      主键
     * @param options 查询选项
     * @return 场景信息
     */
    SceneManageWrapperOutput getSceneManage(Long id, SceneManageQueryOptions options);

    /**
     * 根据场景ID获取业务活动配置
     *
     * @param sceneId 场景主键
     * @return -
     */
    List<SceneBusinessActivityRefOutput> getBusinessActivityBySceneId(Long sceneId);

    /**
     * 预估流量计算
     *
     * @param bills 入参
     * @return 预估流量消耗值
     */
    BigDecimal calcEstimateFlow(List<AssetBillExt> bills);

    /**
     * 获取压测场景目标路径,当前以/结尾
     *
     * @param sceneId 场景主键
     * @return -
     */
    String getDestPath(Long sceneId);

    /**
     * 严格更新 压测场景生命周期
     *
     * @param statusVO 状态参数
     * @return 操作结果
     */
    Boolean updateSceneLifeCycle(UpdateStatusBean statusVO);

    /**
     * 记录场景启动过程  比如job 是否创建成功，压力节点 是否创建成功，
     *
     * @param recordVO 记录参数
     */
    void reportRecord(SceneManageStartRecordVO recordVO);

    /**
     * 不分页查询所有场景信息，带脚本信息
     *
     * @return -
     */
    List<SceneManageListOutput> querySceneManageList();

    /**
     * 更新 脚本id 关联的场景下的文件
     *
     * @param request 请求所需的参数
     */
    void updateFileByScriptId(CloudUpdateSceneFileRequest request);

    /**
     * 根据主键批量获取
     *
     * @param sceneIds 主键
     * @return 批量信息
     */
    List<SceneManageWrapperOutput> getByIds(List<Long> sceneIds);

    /**
     * 设置未上报日志信息
     */
    void saveUnUploadLogInfo();

    /**
     * 检查并更新
     * <p>
     *     TODO
     * </p>
     *
     * @param request        请求
     * @param uploadPath     上传地址
     * @param isAbsolutePath 是否绝对路径
     * @param update         是否更新
     * @param version        版本{@link io.shulie.takin.adapter.api.model.request.scenemanage.ScriptCheckAndUpdateReq#getVersion}
     * @return 操作结果
     */
    ScriptVerityRespExt checkAndUpdate(List<String> request, String uploadPath, boolean isAbsolutePath, boolean update, Integer version);

    /**
     * 恢复
     * @param id
     */
    void recovery(Long id);
	
	void archive(Long id);
	
    List<SceneManageListOutput> getSceneByStatus(Integer status);
}
