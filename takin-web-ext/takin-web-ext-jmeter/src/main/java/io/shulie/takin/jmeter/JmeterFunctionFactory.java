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

package io.shulie.takin.jmeter;

import io.shulie.takin.jmeter.functions.*;
import org.apache.jmeter.functions.AbstractFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/7/19 6:00 PM
 */
public class JmeterFunctionFactory {
    public static Map<String, AbstractFunction> functionMap = new HashMap<>();


    /**
     * 按照keys初始化Jmeter函数
     *
     * @param keys
     */
    public static void init(List<String> keys) {
        if (keys == null && keys.size() == 0) {
            return;
        }
        for (int i = 0; i < keys.size(); i++) {
            functionMap.put(keys.get(i), new RandomString());
        }
    }

    /**
     * 将Jmeter函数对应的key初始化
     * key function key
     * value funcation
     *
     * 有变量的，暂时没处理，业务需求不一定能用到
     */
    static {
        // 随机数
        functionMap.put("__RandomString", new RandomString());
        functionMap.put("__Random", new Random());
        functionMap.put("__RandomDate", new RandomDate());

        functionMap.put("__machineIP", new MachineIP());
        functionMap.put("__machineName", new MachineName());

        functionMap.put("__time", new TimeFunction());
        functionMap.put("__timeShift", new TimeShift());
        functionMap.put("__dateTimeConvert", new DateTimeConvertFunction());

        functionMap.put("__UUID", new Uuid());

        //functionMap.put("__groovy", new Groovy());
        //functionMap.put("__javaScript", new JavaScript());
        functionMap.put("__BeanShell", new BeanShell());

        // 返回当前线程号,从1开始递增。
        functionMap.put("__threadNum", new ThreadNumber());
        functionMap.put("__threadGroupName", new ThreadGroupName());
        functionMap.put("__samplerName", new SamplerName());
        functionMap.put("__TestPlanName", new TestPlanName());

        // 统计线程的迭代次数
        functionMap.put("__counter", new IterationCounter());

        // 动态设置Jmeter属性
        //functionMap.put("__setProperty", new SetProperty());
        // 获取jmeter.properties文件中设置的JMeter属性。
        //functionMap.put("__property", new Property());
        // 获取命令行中定义的属性
        //functionMap.put("__P", new Property2());
        // 执行变量表达式并返回结果，如果需要嵌套的使用变量时
        //functionMap.put("__V", new Variable());
        // 把数字转化成Unicode字符。
        functionMap.put("__char", new CharFunction());

        //int型求和
        functionMap.put("__intSum", new IntSum());
        functionMap.put("__longSum", new LongSum());

        //计算表达式。
        functionMap.put("__eval", new EvalFunction());
        // 把表达式的结果存入变量。
        //functionMap.put("__evalVar", new EvalVarFunction());
    }

    public static AbstractFunction getAbstractFunction(String key) {
        return functionMap.get(key);
    }
}
