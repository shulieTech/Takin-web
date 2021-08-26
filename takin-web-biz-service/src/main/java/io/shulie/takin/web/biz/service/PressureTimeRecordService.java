package io.shulie.takin.web.biz.service;

import com.pamirs.takin.common.constant.TakinErrorEnum;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.entity.dao.transparentflow.TPressureTimeRecordDao;
import com.pamirs.takin.entity.domain.entity.TPressureTimeRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author shulie
 * @description
 * @create 2019-04-12 09:16:23
 */
@Service
public class PressureTimeRecordService {
    @Autowired
    private TPressureTimeRecordDao tPressureTimeRecordDao;

    public void savePressureTimeRecord(TPressureTimeRecord pressureTimeRecord) throws TakinModuleException {
        String startTime = pressureTimeRecord.getStartTime();
        if (startTime == null) {
            throw new TakinModuleException(TakinErrorEnum.PRESSURE_TIME_RECORD_SAVE_PARAM_EXCEPTION);
        }
        TPressureTimeRecord record = tPressureTimeRecordDao.queryLatestPressureTime();
        //如果此时数据库中存在在压测的时间记录，则不能添加
        if (record != null && StringUtils.isNotEmpty(record.getStartTime())) {
            throw new TakinModuleException(TakinErrorEnum.PRESSURE_TIME_RECORD_EXIST_EXCEPTION);
        }
        tPressureTimeRecordDao.insert(startTime);
    }

    public void updatePressureTimeRecord(TPressureTimeRecord pressureTimeRecord) throws TakinModuleException {
        String recordId = pressureTimeRecord.getRecordId();
        String endTime = pressureTimeRecord.getEndTime();
        if (StringUtils.isEmpty(recordId) || StringUtils.isEmpty(endTime)) {
            throw new TakinModuleException(TakinErrorEnum.PRESSURE_TIME_RECORD_UPDATE_PARAM_EXCEPTION);
        }
        tPressureTimeRecordDao.updateByPrimaryKey(recordId, endTime);
    }

    /**
     * 说明: 查询最新的链路压测时间
     *
     * @return TPressureTimeRecord
     * @author shulie
     * @create 2019/4/12 10:17
     */
    public TPressureTimeRecord queryLatestPressureTime() throws TakinModuleException {
        /**
         * 1,开始时间不为空，结束时间为空
         * 2,最大的开始时间
         */
        return tPressureTimeRecordDao.queryLatestPressureTime();
    }
}
