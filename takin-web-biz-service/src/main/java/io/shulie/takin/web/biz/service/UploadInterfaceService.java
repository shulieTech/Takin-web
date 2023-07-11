package io.shulie.takin.web.biz.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pamirs.takin.common.constant.Constants;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.common.redis.RedisKey;
import com.pamirs.takin.common.redis.UploadInterfaceKeyMaker;
import com.pamirs.takin.entity.dao.confcenter.TUploadInterfaceDataDao;
import com.pamirs.takin.entity.domain.entity.TUploadInterfaceData;
import com.pamirs.takin.entity.domain.vo.TUploadInterfaceDetailVo;
import com.pamirs.takin.entity.domain.vo.TUploadInterfaceVo;
import com.pamirs.takin.entity.domain.vo.TUploadNeedVo;
import io.shulie.takin.web.biz.common.CommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 298403
 * @description 上传接口信息接口
 * @create 2019/01/15
 */
@Service
public class UploadInterfaceService extends CommonService {

    @Autowired
    private TUploadInterfaceDataDao uploadInterfaceDataDao;

    /**
     * 判断是否需要上传接口信息
     *
     * @param uploadNeedVo
     * @return
     */
    public boolean executeNeedUploadInterface(TUploadNeedVo uploadNeedVo) throws TakinModuleException {
        if (uploadNeedVo == null || StringUtils.isEmpty(uploadNeedVo.getAppName())) {
            return false;
        }
        String key = UploadInterfaceKeyMaker.getUploadKey(uploadNeedVo.getAppName());
        Optional<Object> value = redisManager.valueGet(key);
        if (value.isPresent()) {
            if (Integer.parseInt(value.get().toString()) == uploadNeedVo.getSize().intValue()) {
                return false;
            } else {
                redisManager.removeKey(key);
                return true;
            }
        }
        return true;
    }

    /**
     * 保存上传的接口信息
     *
     * @param uploadInterfaceVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int saveUploadInterfaceData(TUploadInterfaceVo uploadInterfaceVo) throws TakinModuleException {
        if (uploadInterfaceVo == null || StringUtils.isEmpty(uploadInterfaceVo.getAppName()) || CollectionUtils.isEmpty(
            uploadInterfaceVo.getAppDetails())) {
            return 0;
        }
        String key = UploadInterfaceKeyMaker.getUploadKey(uploadInterfaceVo.getAppName());
        //超时时间
        RedisKey redisKey = new RedisKey(key, 60 * 30);
        if (!redisManager.acquireLock(redisKey)) {
            return 0;
        }
        //保存前先删除数据
        uploadInterfaceDataDao.deleteByAppName(uploadInterfaceVo.getAppName());
        List<TUploadInterfaceData> saveDataList = new ArrayList<>();
        for (TUploadInterfaceDetailVo appDetail : uploadInterfaceVo.getAppDetails()) {
            TUploadInterfaceData tempDate = new TUploadInterfaceData();
            tempDate.setAppName(uploadInterfaceVo.getAppName());
            tempDate.setId(snowflake.next());
            tempDate.setInterfaceValue(appDetail.getInterfaceName());
            if (Constants.UPLOAD_DATA_TYPE_DUBBO.equalsIgnoreCase(appDetail.getType())) {
                tempDate.setInterfaceType(Constants.UPLOAD_DATA_DBTYPE_DUBBO);
            } else if (Constants.UPLOAD_DATA_TYPE_JOB.equalsIgnoreCase(appDetail.getType())) {
                tempDate.setInterfaceType(Constants.UPLOAD_DATA_DBTYPE_JOB);
            } else {
                continue;
            }
            saveDataList.add(tempDate);
            if (saveDataList.size() > 500) {
                uploadInterfaceDataDao.insertList(saveDataList);
                saveDataList = new ArrayList<>();
            }
        }
        if (CollectionUtils.isNotEmpty(saveDataList)) {
            uploadInterfaceDataDao.insertList(saveDataList);
        }
        //最后放入缓存
        redisManager.valuePut(redisKey, System.currentTimeMillis() + 60 * 3000);
        return uploadInterfaceVo.getAppDetails().size();
    }
}
