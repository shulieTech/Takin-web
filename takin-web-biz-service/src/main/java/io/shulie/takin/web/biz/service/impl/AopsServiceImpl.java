package io.shulie.takin.web.biz.service.impl;

import java.util.Map;

import io.shulie.takin.web.biz.service.AopsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * aops reader server
 *
 * @author 311183
 */
@Service
public class AopsServiceImpl implements AopsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AopsServiceImpl.class);

    private static final String ZERO = "0";

    @Override
    public Map getAopsData(String ip, String startSecond, String endSecond, String time) {
        return null;
    }

}
