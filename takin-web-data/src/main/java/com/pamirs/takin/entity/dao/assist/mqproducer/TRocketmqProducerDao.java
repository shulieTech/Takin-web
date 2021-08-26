package com.pamirs.takin.entity.dao.assist.mqproducer;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pamirs.takin.entity.domain.entity.TRocketmqProducer;
import org.apache.ibatis.annotations.Param;

/**
 * @author lk
 * @description ESB/IBM生产消息接口
 * @create 2018/9/13 13:42
 */
public interface TRocketmqProducerDao {

    /**
     * 说明: 校验该是否存在
     *
     * @param TRocketmqProducer 消息实体
     * @return >0表示存在，否则不存在可添加
     */
    int isRocketmqExist(TRocketmqProducer TRocketmqProducer);

    /**
     * @param TRocketmqProducer 消息对象
     * @return int
     * @description 向数据库中插入一条消息
     */
    int insert(TRocketmqProducer TRocketmqProducer);

    /**
     * 查询rocketmq消息列表
     *
     * @param paramMap
     * @return
     */
    List<TRocketmqProducer> queryRocketmqList(Map<String, Object> paramMap);

    /**
     * 根据id查询，返回对象
     *
     * @param trpId
     * @return
     */

    TRocketmqProducer selectakincketMqMsgById(long trpId);

    /**
     * 批量删除rocketmq生产消息
     *
     * @param trpIdList
     */
    void batchDeleteRocketMq(@Param("trpIdList") List<String> trpIdList);

    /**
     * 查询rocketmq消息详情
     *
     * @param trpId rocketmq生产消息id
     * @return
     */
    TRocketmqProducer queryRocketmqDetail(String trpId);

    /**
     * 说明: 根据id列表批量查询rocketmq生产信息
     *
     * @param rocketMqProduceListIds id列表
     * @return rocketmq生产信息列表
     * @author shulie
     * @date 2018/11/6 14:24
     */
    List<TRocketmqProducer> queryRocketMqListByIds(
        @Param("rocketMqProduceListIds") List<String> rocketMqProduceListIds);

    /**
     * 更新生产消息状态
     *
     * @param TRocketmqProducer
     */
    void updateRocketMqStatus(TRocketmqProducer TRocketmqProducer);

    /**
     * 更新生产状态、成功数、结束时间
     *
     * @param produceStatus
     * @param msgSuccessCount
     * @param produceEndTime
     * @param trpId
     */
    void updateRocketMqProduceStatus(@Param("produceStatus") String produceStatus,
        @Param("msgSuccessCount") String msgSuccessCount,
        @Param("produceEndTime") Date produceEndTime,
        @Param("trpId") String trpId);

    /**
     * 根据id查询，判断是否存在。
     *
     * @param rocketmqExist
     * @return
     */
    TRocketmqProducer isRocketmqExistById(Long rocketmqExist);

    /**
     * 更新操作
     *
     * @param TRocketmqProducer
     */

    void update(TRocketmqProducer TRocketmqProducer);
}
