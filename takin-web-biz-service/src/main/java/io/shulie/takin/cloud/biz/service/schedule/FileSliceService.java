package io.shulie.takin.cloud.biz.service.schedule;

import com.pamirs.takin.cloud.entity.domain.vo.file.FileSliceRequest;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneContactFileOutput;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.data.model.mysql.SceneBigFileSliceEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneScriptRefEntity;
import io.shulie.takin.cloud.data.param.scenemanage.SceneBigFileSliceParam;

/**
 * 文件切片 service
 *
 * @author moriarty
 */
public interface FileSliceService {

    String IS_SPLIT = "isSplit";

    String IS_ORDER_SPLIT = "isOrderSplit";

    /**
     * 大文件分片
     * 文件分片分两种情况：1. 根据场景要启动的pod数量分片，这种情况不保证文件中数据的顺序
     * 2. 根据文件中指定列的顺序拆分，这种情况耗时较长，需要逐行处理，而且要求文件顺序正确，不能存在内容的穿插
     *
     * @param request 请求参数
     * @return -
     */
    boolean fileSlice(FileSliceRequest request) throws TakinCloudException;

    /**
     * 查询文件分片信息
     *
     * @param request -
     * @return -
     */
    SceneBigFileSliceEntity getOneByParam(FileSliceRequest request);

    /**
     * 查询文件分片状态
     *
     * @param request -
     * @return -
     */
    Integer isFileSliced(FileSliceRequest request);

    /**
     * 更新SceneScriptRef
     *
     * @param request -
     * @param param   -
     * @return -
     */
    Boolean updateFileRefExtend(FileSliceRequest request, SceneBigFileSliceParam param);

    /**
     * 更新 {@link SceneScriptRefEntity} 对应文件的md5值
     *
     * @param param -
     */
    void updateFileMd5(SceneBigFileSliceParam param);

    /**
     * 关联文件与脚本、场景，并对顺序分片的文件进行预分片
     *
     * @param param -
     * @return -
     * @throws TakinCloudException -
     */
    SceneContactFileOutput contactScene(SceneBigFileSliceParam param) throws TakinCloudException;
}
