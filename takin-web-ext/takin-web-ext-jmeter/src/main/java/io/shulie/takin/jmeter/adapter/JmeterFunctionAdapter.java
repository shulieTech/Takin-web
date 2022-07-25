/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.takin.jmeter.adapter;

import io.shulie.takin.jmeter.JmeterFunctionFactory;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.threads.AbstractThreadGroup;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

import static io.shulie.takin.jmeter.FunctionHelper.getFormatter;
import static io.shulie.takin.jmeter.FunctionHelper.makeParams;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/7/19 5:51 PM
 */
public class JmeterFunctionAdapter {
    private static JmeterFunctionAdapter jmeterFunctionAdapter = new JmeterFunctionAdapter();

    private static boolean loadSuccess = true;

    static {
        try {
            JMeterUtils.loadJMeterProperties(JmeterFunctionAdapter.class.getResource("/jmeter.properties").getPath());
        } catch (Throwable e) {
            loadSuccess = false;
            e.printStackTrace();
        }
    }

    public static JmeterFunctionAdapter getInstance() {
        return jmeterFunctionAdapter;
    }

    /**
     * 解析整个jmeter函数内容,获取结果
     *
     * @param funStr
     * @return
     * @throws InvalidVariableException
     */
    public String execute(String funStr) throws InvalidVariableException {
        // 读取function函数
        String functionName = "";
        if (funStr.contains("(")) {
            functionName = funStr.substring(funStr.indexOf("__"), funStr.indexOf("("));
        } else {
            functionName = funStr.substring(funStr.indexOf("__"), funStr.indexOf("}"));
        }
        AbstractFunction function = JmeterFunctionFactory.getAbstractFunction(functionName);
        // 获取参数
        Collection<CompoundVariable> params = new ArrayList<>();
        if (funStr.contains("(")) {
            // 截取参数
            funStr = funStr.substring(funStr.indexOf("(") + 1, funStr.indexOf(")"));
            // 按逗号分割出参数
            String[] paramStr = funStr.split(",", -1);
            for (int i = 0; i < paramStr.length; i++) {
                String tmp = paramStr[i];
                Collection<CompoundVariable> tmpVar = makeParams(tmp);
                params.addAll(tmpVar);
            }
            function.setParameters(params);
        }
        JMeterContext context = JMeterContextService.getContext();
        context.setVariables(new JMeterVariables());
        AbstractThreadGroup takinThreadGroup = new TakinThreadGroup();
        takinThreadGroup.setName("Takin_Group");
        context.setThreadGroup(takinThreadGroup);

        // 初始化Jmeter变量
        if (loadSuccess) {
            Instant now = Instant.now();
            JMeterUtils.setProperty("START.MS", Long.toString(now.toEpochMilli()));// $NON-NLS-1$
            JMeterUtils.setProperty("START.YMD", getFormatter("yyyyMMdd").format(now));// $NON-NLS-1$ $NON-NLS-2$
            JMeterUtils.setProperty("START.HMS", getFormatter("HHmmss").format(now));// $NON-NLS-1$ $NON-NLS-2$
            JMeterUtils.setProperty("TESTSTART.MS", Long.toString(System.currentTimeMillis()));
        }
        return function.execute();
    }

    /**
     * 是否支持此类函数
     *
     * @param funStr
     * @return
     */
    public boolean supportFunction(String funStr) {
        return JmeterFunctionFactory.functionMap.containsKey(funStr);
    }
}
