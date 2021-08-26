package io.shulie.takin.web.biz.service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.pagehelper.PageHelper;
import com.google.common.base.Splitter;
import com.pamirs.takin.common.constant.TakinErrorEnum;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.common.util.PageInfo;
import com.pamirs.takin.entity.dao.assist.mqproducer.TEbmProducerDao;
import com.pamirs.takin.entity.dao.assist.mqproducer.TRocketmqProducerDao;
import com.pamirs.takin.entity.domain.entity.TEbmProducer;
import com.pamirs.takin.entity.domain.entity.TRocketmqProducer;
import io.shulie.takin.web.biz.common.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 说明: mq虚拟生产消息service
 *
 * @author shulie
 * @version v1.0
 * @date Create in 2018/9/13 12:50
 */
@Service
public class MqProducerService extends CommonService {

    @Autowired
    public TRocketmqProducerDao tRocketmqProducerDao;
    @Autowired
    private TEbmProducerDao tEbmProducerDao;

    /**
     * 新增rocketmq信息
     *
     * @param tRocketmqProducer -
     *
     * @throws TakinModuleException -
     */

    public void addRocketmq(TRocketmqProducer tRocketmqProducer) throws TakinModuleException {

        int rocketmqExist = tRocketmqProducerDao.isRocketmqExist(tRocketmqProducer);
        if (rocketmqExist > 0) {
            throw new TakinModuleException(TakinErrorEnum.ASSIST_MQPRODUCER_ROCKETMQ_SAVE_DUPICATE);
        }
        tRocketmqProducer.setTrpId(snowflake.next());
        tRocketmqProducer.setProduceStatus("0");

        String regex = "^[1-9]+[0-9]*$";
        Pattern p = Pattern.compile(regex);
        Matcher m1 = p.matcher(tRocketmqProducer.getSleepTime());
        Matcher m2 = p.matcher(tRocketmqProducer.getMsgCount());

        //        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.
        //        (\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        //        Pattern p1 = Pattern.compile(rexp);
        //        Matcher m3 = p1.matcher(takincketmqProducer.getMsgIp());

        if (m1.find() && m2.find()) {
            tRocketmqProducerDao.insert(tRocketmqProducer);

        } else {

            throw new TakinModuleException(TakinErrorEnum.ASSIST_MQPRODUCER_ROCKETMQ_SAVE_EXCEPTION);
        }
    }

    /**
     * 更新rocketMq信息
     *
     * @param tRocketmqProducer -
     *
     * @throws TakinModuleException -
     */
    public void updateRocketmq(TRocketmqProducer tRocketmqProducer) throws TakinModuleException {
        Long rocketmqExist = tRocketmqProducer.getTrpId();
        TRocketmqProducer rocketmqExistById = tRocketmqProducerDao.isRocketmqExistById(rocketmqExist);
        if (rocketmqExistById != null) {

            String regex = "^[1-9]+[0-9]*$";
            Pattern p = Pattern.compile(regex);
            Matcher m1 = p.matcher(tRocketmqProducer.getSleepTime());
            Matcher m2 = p.matcher(tRocketmqProducer.getMsgCount());

            //            String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.
            //            (\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
            //            Pattern p1 = Pattern.compile(rexp);
            //            Matcher m3 = p1.matcher(takincketmqProducer.getMsgIp());

            if (m1.find() && m2.find()) {
                tRocketmqProducerDao.update(tRocketmqProducer);

            }
        } else {
            throw new TakinModuleException(TakinErrorEnum.ASSIST_MQPRODUCER_ROCKETMQ_UPDATE_EXCEPTION);
        }
    }

    /**
     * 说明: API.05.03.004 新增ESB/IBM虚拟发送消息
     *
     * @param tEbmProducer 消息封装对象
     *
     * @author shulie
     * @date 2018/9/13 16:45
     */
    public void saveEbm(TEbmProducer tEbmProducer) throws TakinModuleException {
        int exist = tEbmProducerDao.selectEbmExist(tEbmProducer);
        if (exist > 0) {
            throw new TakinModuleException(TakinErrorEnum.ASSIST_MQPRODUCER_EBM_SAVE_DUPICATE);
        }
        tEbmProducer.setTepId(snowflake.next());
        tEbmProducer.setProduceStatus("0");
        tEbmProducer.setEsbcode(filterIllegalCharacters(tEbmProducer.getEsbcode()));
        tEbmProducerDao.saveEbm(tEbmProducer);

    }

    /**
     * 说明: API.05.03.006 修改ESB/IBM虚拟发送消息
     *
     * @param tEbmProducer 待修改的消息对象信息
     *
     * @author shulie
     * @date 2018/9/13 16:45
     */
    public void updateEbm(TEbmProducer tEbmProducer) throws TakinModuleException {
        TEbmProducer ebmProducer = tEbmProducerDao.selectEbmMsgById(tEbmProducer.getTepId());
        if (ebmProducer == null) {
            throw new TakinModuleException(TakinErrorEnum.ASSIST_MQPRODUCER_EBM_NOT_FOUNT_EXCEPTION);
        }
        tEbmProducerDao.updateEbm(tEbmProducer);

    }

    /**
     * 查询rocketmq生产消息列表
     *
     * @param paramMap {pageNum, pageSize, topic, groupName}
     *
     * @return
     */
    public PageInfo<TRocketmqProducer> queryRocketmqList(Map<String, Object> paramMap) {
        PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        List<TRocketmqProducer> rocketmqProducerList = tRocketmqProducerDao.queryRocketmqList(paramMap);

        return new PageInfo<>(rocketmqProducerList);
    }

    /**
     * 批量删除rocketmq消息
     *
     * @param trpIds rocketmq生产消息id列表
     */
    public void deleteRocketMq(String trpIds) {
        List<String> trpIdList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(trpIds);
        tRocketmqProducerDao.batchDeleteRocketMq(trpIdList);
        List<TRocketmqProducer> tRocketmqProducers = tRocketmqProducerDao.queryRocketMqListByIds(trpIdList);

    }

    /**
     * 查询rocketmq消息详情
     *
     * @param trpId rocketmq生产消息id
     *
     * @return
     */
    public TRocketmqProducer queryRocketMqDetail(String trpId) {
        return tRocketmqProducerDao.queryRocketmqDetail(trpId);
    }

    /**
     * 查询esb/ibm消息详情
     *
     * @param tepId ESB/IBM消息id
     *
     * @return
     */
    public TEbmProducer queryEsbOrIbmDetail(String tepId) {
        TEbmProducer tEbmProducer = tEbmProducerDao.selectEbmMsgById(Long.parseLong(tepId));

        return tEbmProducer;
    }

    /**
     * 查询ESB/IBM消息列表
     *
     * @param paramMap { pageNum, pageSize, esb, produceStartTime, produceEndTime }
     *
     * @return
     */
    public PageInfo<TEbmProducer> queryEsbAndIbmList(Map<String, Object> paramMap) {
        PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        List<TEbmProducer> ebmProducerList = tEbmProducerDao.queryEsbAndIbmList(paramMap);

        return new PageInfo<>(ebmProducerList);
    }

    /**
     * 批量删除esb/ibm消息
     *
     * @param tepIds
     */
    public void deteletEsbOrIbm(String tepIds) {
        List<String> tepIdList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(tepIds);
        tEbmProducerDao.batchDeleteEsbAndIbm(tepIdList);
        List<TEbmProducer> tEbmProducers = tEbmProducerDao.queryEBMProduceListByIds(tepIdList);

    }
}
