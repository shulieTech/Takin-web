package io.shulie.takin.web.biz.service.impl;

import java.text.MessageFormat;
import java.util.List;

import com.alibaba.fastjson.JSON;

import com.pamirs.takin.common.constant.TakinErrorEnum;
import com.pamirs.takin.entity.domain.entity.TAlarm;
import com.pamirs.takin.entity.domain.query.Result;
import com.pamirs.takin.entity.domain.query.ResultList;
import com.pamirs.takin.entity.domain.query.TAlarmQuery;
import io.shulie.takin.web.biz.common.CommonService;
import io.shulie.takin.web.biz.service.TAlarmService;
import org.springframework.stereotype.Service;

/**
 * 说明: 告警相关服务实现类
 */
@Service
public class TAlarmServiceImpl extends CommonService implements TAlarmService {

    /**
     * 添加告警
     *
     * @return
     */
    @Override
    public Result<Void> add(TAlarm tAlarm) {
        Result<Void> result = new Result<>();
        try {
            int count = tAlarmDao.insert(tAlarm);
            if (count != 1) {
                result.setSuccess(Boolean.FALSE);
                result.setErrorMessage(TakinErrorEnum.MONITOR_DB_ADD_EXCEPTION.getErrorMessage());
            }
        } catch (Exception e) {
            LOGGER.error(MessageFormat.format("error:{0},model:{1}",
                TakinErrorEnum.MONITOR_DB_ADD_EXCEPTION.getErrorMessage(),
                JSON.toJSONString(tAlarm)), e);
            result.setSuccess(Boolean.FALSE);
            result.setErrorMessage(TakinErrorEnum.MONITOR_DB_ADD_EXCEPTION.getErrorMessage());
        }
        return result;
    }

    /**
     * 修改告警
     *
     * @return
     */
    @Override
    public Result<Void> modify(TAlarm tAlarm) {
        Result<Void> result = new Result<>();
        if (null == tAlarm || null == tAlarm.getId()) {
            result.setSuccess(Boolean.FALSE);
            return result;
        }
        try {
            int count = tAlarmDao.update(tAlarm);
            if (count != 1) {
                result.setSuccess(Boolean.FALSE);
                result.setErrorMessage(TakinErrorEnum.MONITOR_DB_UPDATE_EXCEPTION.getErrorMessage());
            }
        } catch (Exception e) {
            LOGGER.error(MessageFormat.format("error:{0},model:{1}",
                TakinErrorEnum.MONITOR_DB_UPDATE_EXCEPTION.getErrorMessage(),
                JSON.toJSONString(tAlarm)), e);
            result.setSuccess(Boolean.FALSE);
            result.setErrorMessage(TakinErrorEnum.MONITOR_DB_UPDATE_EXCEPTION.getErrorMessage());

        }
        return result;
    }

    /**
     * 逻辑删除告警
     *
     * @return
     */
    @Override
    public Result<Void> deleteById(Long id) {
        Result<Void> result = new Result<>();
        if (null == id) {
            result.setSuccess(Boolean.FALSE);
            return result;
        }
        try {
            int count = tAlarmDao.delete(id);
            if (count != 1) {
                result.setSuccess(Boolean.FALSE);
                result.setErrorMessage(TakinErrorEnum.MONITOR_DB_DELETE_EXCEPTION.getErrorMessage());
            }
        } catch (Exception e) {
            LOGGER.error(MessageFormat.format("error:{0},id:{1}",
                TakinErrorEnum.MONITOR_DB_DELETE_EXCEPTION.getErrorMessage(), id), e);
            result.setSuccess(Boolean.FALSE);
            result.setErrorMessage(TakinErrorEnum.MONITOR_DB_DELETE_EXCEPTION.getErrorMessage());
        }
        return result;
    }

    /**
     * 查询告警列表
     *
     * @return
     */
    @Override
    public ResultList<TAlarm> queryListByQuery(TAlarmQuery query) {
        ResultList<TAlarm> resultList = new ResultList<>();
        try {
            query.setOrderBy("create_time desc");
            List<TAlarm> goodsDOList = tAlarmDao.selectList(query);
            long count = tAlarmDao.selectListCount(query);
            resultList.setDatalist(goodsDOList);
            resultList.setTotal(count);

        } catch (Exception e) {
            LOGGER.error(MessageFormat.format("error:{0},query:{1}",
                TakinErrorEnum.MONITOR_DB_QUERYLIST_EXCEPTION.getErrorMessage(), JSON.toJSONString(query)), e);
            resultList.setSuccess(Boolean.FALSE);
            resultList.setErrorMessage(TakinErrorEnum.MONITOR_DB_QUERYLIST_EXCEPTION.getErrorMessage());

        }
        return resultList;
    }

    /**
     * 查询告警
     *
     * @return
     */
    @Override
    public Result<TAlarm> queryOneById(Long id) {
        Result<TAlarm> result = new Result<>();
        if (null == id) {
            result.setSuccess(Boolean.FALSE);
            return result;
        }
        try {
            TAlarm tAlarm = tAlarmDao.selectOneById(id);
            result.setData(tAlarm);
        } catch (Exception e) {
            LOGGER.error(MessageFormat.format("error:{0},id:{1}",
                TakinErrorEnum.MONITOR_DB_QUERY_EXCEPTION.getErrorMessage(), id), e);
            result.setSuccess(Boolean.FALSE);
            result.setErrorMessage(TakinErrorEnum.MONITOR_DB_QUERY_EXCEPTION.getErrorMessage());
        }
        return result;
    }
}
