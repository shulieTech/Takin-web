package io.shulie.takin.web.biz.service.pressureresource.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.MqConsumerFeature;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceMqConsumerCreateInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceMqConsumerQueryRequest;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceMqConsumerService;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceMqComsumerVO;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateMqComsumerDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateMqConsumerMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateMqConsumerEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceMqConsumerQueryParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 压测资源配置
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:53 PM
 */
@Service
public class PressureResourceMqConsumerServiceImpl implements PressureResourceMqConsumerService {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceMqConsumerServiceImpl.class);

    @Resource
    private PressureResourceRelateMqConsumerMapper pressureResourceRelateMqConsumerMapper;
    @Resource
    private PressureResourceRelateMqComsumerDAO pressureResourceRelateMqComsumerDAO;

    /**
     * 分页
     *
     * @param request
     * @return
     */
    @Override
    public PagingList<PressureResourceMqComsumerVO> list(PressureResourceMqConsumerQueryRequest request) {
        PressureResourceMqConsumerQueryParam param = new PressureResourceMqConsumerQueryParam();
        BeanUtils.copyProperties(request, param);
        PagingList<PressureResourceRelateMqConsumerEntity> pageList = pressureResourceRelateMqComsumerDAO.pageList(param);
        if (pageList.isEmpty()) {
            return PagingList.of(Collections.emptyList(), pageList.getTotal());
        }
        //转换下
        List<PressureResourceRelateMqConsumerEntity> source = pageList.getList();
        List<PressureResourceMqComsumerVO> returnList = source.stream().map(configDto -> {
            PressureResourceMqComsumerVO vo = new PressureResourceMqComsumerVO();
            BeanUtils.copyProperties(configDto, vo);
            vo.setId(String.valueOf(configDto.getId()));
            // 转换下feature
            if (StringUtils.isNotBlank(vo.getFeature())) {
                vo.setMqConsumerFeature(JSON.parseObject(vo.getFeature(), MqConsumerFeature.class));
            }
            // kafka的时候,如果是生产者,不需要展示消费组,设置为空
            if ("KAFKA".equals(vo.getMqType()) && vo.getComsumerType() == 0) {
                vo.setGroup("");
            }
            return vo;
        }).collect(Collectors.toList());

        return PagingList.of(returnList, pageList.getTotal());
    }

    /**
     * 创建影子消费者
     *
     * @param request
     */
    @Override
    public void create(PressureResourceMqConsumerCreateInput request) {
        validata(request);
        PressureResourceMqConsumerQueryParam queryParam = new PressureResourceMqConsumerQueryParam();
        queryParam.setResourceId(request.getResourceId());
        queryParam.setTopic(request.getTopic());
        queryParam.setGroup(request.getGroup());
        queryParam.setMqType(request.getMqType());
        List<PressureResourceRelateMqConsumerEntity> exists = pressureResourceRelateMqComsumerDAO.queryList(queryParam);
        if (CollectionUtils.isNotEmpty(exists)) {
            throw new RuntimeException(
                    String.format("类型为[%s]，对应的topic[%s] group[%s]已存在",
                            request.getMqType(),
                            request.getTopic(),
                            request.getGroup()));
        }
        PressureResourceRelateMqConsumerEntity shadowMqConsumerEntity = convertEntity(request);
        shadowMqConsumerEntity.setId(null);
        shadowMqConsumerEntity.setGmtCreate(new Date());
        shadowMqConsumerEntity.setGmtModified(new Date());
        pressureResourceRelateMqComsumerDAO.add(shadowMqConsumerEntity);
    }

    private void validata(PressureResourceMqConsumerCreateInput request) {
    }

    /**
     * 修改
     *
     * @param request
     */
    @Override
    public void update(PressureResourceMqConsumerCreateInput request) {
        if (request.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        // 判断是否存在
        PressureResourceRelateMqConsumerEntity entity = pressureResourceRelateMqConsumerMapper.selectById(request.getId());
        if (entity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_QUERY_ERROR, "未查询到指定数据");
        }
        PressureResourceMqConsumerQueryParam queryParam = new PressureResourceMqConsumerQueryParam();
        queryParam.setResourceId(request.getResourceId());
        queryParam.setTopic(request.getTopic());
        queryParam.setGroup(request.getGroup());
        queryParam.setMqType(request.getMqType());
        List<PressureResourceRelateMqConsumerEntity> exists = pressureResourceRelateMqComsumerDAO.queryList(queryParam);
        if (CollectionUtils.isNotEmpty(exists)) {
            // 判断是否属于同一个Id
            PressureResourceRelateMqConsumerEntity mqConsumer = exists.get(0);
            if (!mqConsumer.getId().equals(request.getId())) {
                throw new RuntimeException(
                        String.format("类型为[%s]，对应的topic[%s] group[%s]已存在",
                                request.getMqType(),
                                request.getTopic(),
                                request.getGroup()));
            }
        }
        // 更新
        PressureResourceRelateMqConsumerEntity updateEntity = convertEntity(request);
        updateEntity.setGmtModified(new Date());
        pressureResourceRelateMqConsumerMapper.updateById(updateEntity);
    }

    /**
     * 处理消费状态
     *
     * @param input
     */
    @Override
    public void processConsumerTag(PressureResourceMqConsumerCreateInput input) {
        if (CollectionUtils.isEmpty(input.getIds()) && input.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数未指定");
        }
        PressureResourceRelateMqConsumerEntity updateEntity = new PressureResourceRelateMqConsumerEntity();
        updateEntity.setConsumerTag(input.getConsumerTag());
        QueryWrapper<PressureResourceRelateMqConsumerEntity> queryWrapper = new QueryWrapper<>();
        if (CollectionUtils.isNotEmpty(input.getIds())) {
            queryWrapper.in("id", input.getIds());
        }
        if (input.getId() != null) {
            queryWrapper.eq("id", input.getId());
        }
        pressureResourceRelateMqConsumerMapper.update(updateEntity, queryWrapper);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        pressureResourceRelateMqConsumerMapper.deleteById(id);
    }

    private PressureResourceRelateMqConsumerEntity convertEntity(PressureResourceMqConsumerCreateInput request) {
        PressureResourceRelateMqConsumerEntity shadowMqConsumerEntity = new PressureResourceRelateMqConsumerEntity();
        shadowMqConsumerEntity.setId(request.getId());
        shadowMqConsumerEntity.setResourceId(request.getResourceId());
        shadowMqConsumerEntity.setTopic(request.getTopic());
        shadowMqConsumerEntity.setGroup(StringUtils.isBlank(request.getGroup()) ? "default" : request.getGroup());
        shadowMqConsumerEntity.setBrokerAddr(request.getBrokerAddr());
        shadowMqConsumerEntity.setTopicTokens(request.getTopicTokens());
        shadowMqConsumerEntity.setSystemIdToken(request.getSystemIdToken());
        shadowMqConsumerEntity.setMqType(request.getMqType());
        // 是否消费
        shadowMqConsumerEntity.setConsumerTag(request.getConsumerTag());
        shadowMqConsumerEntity.setComsumerType(request.getComsumerType());
        shadowMqConsumerEntity.setIsCluster(request.getIsCluster());
        // 设置来源标识
        shadowMqConsumerEntity.setType(request.getType());
        if (request.getMqConsumerFeature() != null) {
            shadowMqConsumerEntity.setFeature(JSON.toJSONString(request.getMqConsumerFeature()));
        }
        return shadowMqConsumerEntity;
    }
}
