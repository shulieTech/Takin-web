package io.shulie.takin.web.common.agent.impl;

import java.util.ArrayList;
import java.util.List;

import io.shulie.takin.web.common.agent.AgentZipResolverSupport;

/**
 * @Description 中间件探针包解析器
 * @Author ocean_wll
 * @Date 2021/11/10 8:50 下午
 */
public class MiddlewareZipResolver extends AgentZipResolverSupport {

    @Override
    public List<String> checkFile0(String filePath) {
        return new ArrayList<>();
    }

    @Override
    public String getZipBaseDirName() {
        return "modules";
    }
}
