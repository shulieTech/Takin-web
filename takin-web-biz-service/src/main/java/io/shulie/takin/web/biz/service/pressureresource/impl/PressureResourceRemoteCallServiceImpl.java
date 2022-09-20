package io.shulie.takin.web.biz.service.pressureresource.impl;

import bsh.Interpreter;
import com.alibaba.fastjson.JSON;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.MockInfo;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceCheckVO;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceMockInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelateRemoteCallRequest;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceRemoteCallService;
import io.shulie.takin.web.biz.service.pressureresource.common.PassEnum;
import io.shulie.takin.web.biz.service.pressureresource.common.dy.DynamicCompilerUtil;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceRelateRemoteCallVO;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateRemoteCallDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateRemoteCallMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateRemoteCallEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceRemoteCallQueryParam;
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
 * @author xingchen
 * @description: TODO
 * @date 2022/9/16 12:06 PM
 */
@Service
public class PressureResourceRemoteCallServiceImpl implements PressureResourceRemoteCallService {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceRemoteCallServiceImpl.class);

    @Resource
    private PressureResourceRelateRemoteCallDAO pressureResourceRelateRemoteCallDAO;

    @Resource
    private PressureResourceRelateRemoteCallMapper pressureResourceRelateRemoteCallMapper;

    /**
     * @param request
     * @return
     */
    @Override
    public PagingList<PressureResourceRelateRemoteCallVO> pageList(PressureResourceRelateRemoteCallRequest request) {
        PressureResourceRemoteCallQueryParam param = new PressureResourceRemoteCallQueryParam();
        BeanUtils.copyProperties(request, param);
        PagingList<PressureResourceRelateRemoteCallEntity> pageList = pressureResourceRelateRemoteCallDAO.pageList(param);

        if (pageList.isEmpty()) {
            return PagingList.of(Collections.emptyList(), pageList.getTotal());
        }
        //转换下
        List<PressureResourceRelateRemoteCallEntity> source = pageList.getList();
        List<PressureResourceRelateRemoteCallVO> returnList = source.stream().map(configDto -> {
            PressureResourceRelateRemoteCallVO vo = new PressureResourceRelateRemoteCallVO();
            BeanUtils.copyProperties(configDto, vo);
            vo.setId(String.valueOf(configDto.getId()));
            // 调用依赖
            if (StringUtils.isNotBlank(vo.getAppName()) && StringUtils.isNotBlank(vo.getServerAppName())) {
                vo.setInvoke(vo.getAppName() + "调用" + vo.getServerAppName());
            }
            return vo;
        }).collect(Collectors.toList());
        return PagingList.of(returnList, pageList.getTotal());
    }

    /**
     * 更新mock
     *
     * @param mockInput
     */
    @Override
    public void update(PressureResourceMockInput mockInput) {
        Long id = mockInput.getId();
        if (id == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        PressureResourceRelateRemoteCallEntity update = new PressureResourceRelateRemoteCallEntity();
        update.setId(id);
        MockInfo mockInfo = mockInput.getMockInfo();
        if (mockInfo != null) {
            update.setMockReturnValue(JSON.toJSONString(mockInfo));
            // 设置了mock,默认不放行
            update.setPass(PassEnum.PASS_NO.getCode());
        } else {
            update.setPass(mockInput.getPass());
        }
        update.setGmtModified(new Date());
        pressureResourceRelateRemoteCallMapper.updateById(update);
    }

    /**
     * 获取服务平均响应时间,id为远程调用服务Id
     *
     * @param id
     * @return
     */
    @Override
    public Integer getServiceAvgRt(Long id) {
        return 0;
    }

    /**
     * 校验
     *
     * @param mockInfo
     * @return
     */
    @Override
    public PressureResourceCheckVO check(MockInfo mockInfo) {
        PressureResourceCheckVO checkVO = new PressureResourceCheckVO();
        checkVO.setSuccess(true);

        if (StringUtils.isBlank(mockInfo.getMockValue())) {
            checkVO.setRemark("mock数据为空!");
            checkVO.setSuccess(false);
            return checkVO;
        }
        if (mockInfo.getType().equals("0")) {
            try {
                JSON.parseObject(mockInfo.getMockValue());
            } catch (Throwable e) {
                checkVO.setSuccess(false);
                checkVO.setRemark(e.getMessage());
            }
        } else {
            try {
                String remark = DynamicCompilerUtil.check(mockInfo.getMockValue());
                if (StringUtils.isNotBlank(remark)) {
                    checkVO.setRemark(remark);
                    checkVO.setSuccess(false);
                }
            } catch (Throwable e) {
                checkVO.setRemark(e.getMessage());
                checkVO.setSuccess(false);
            }
        }
        return checkVO;
    }
}
