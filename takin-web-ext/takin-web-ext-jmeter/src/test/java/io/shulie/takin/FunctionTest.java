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

package io.shulie.takin;

import io.shulie.takin.jmeter.adapter.JmeterFunctionAdapter;
import org.apache.jmeter.functions.InvalidVariableException;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class FunctionTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void FunctionTest() throws InvalidVariableException {
        // UUID
        System.out.println(new JmeterFunctionAdapter().execute("${__time(,)}"));
        System.out.println(new JmeterFunctionAdapter().execute("${__UUID}"));

        // 随机数
        System.out.println(new JmeterFunctionAdapter().execute("${__dateTimeConvert(09102020011034,MMddyyyyHHmmss,yyyyMMdd HH:mm:ss,)}"));
        System.out.println(new JmeterFunctionAdapter().execute("${__timeShift(yyyy/MM/dd,,P-1D,,)}"));

        System.out.println(new JmeterFunctionAdapter().execute("${__RandomDate(,,2023-05-09,en_EN,)}"));
        System.out.println(new JmeterFunctionAdapter().execute("${__Random(1,6,)}"));
        System.out.println(new JmeterFunctionAdapter().execute("${__RandomString(2,121234,)}"));

        // char,把数字转化成Unicode字符。
        System.out.println(new JmeterFunctionAdapter().execute("${__char(0x65)}"));
        // 当前线程的编号
        System.out.println(new JmeterFunctionAdapter().execute("${__threadNum}"));
        System.out.println(new JmeterFunctionAdapter().execute("${__threadGroupName}"));

        // time
        System.out.println(new JmeterFunctionAdapter().execute("${__time(,)}"));
        // timeShift
        System.out.println(new JmeterFunctionAdapter().execute("${__timeShift(,,,,)}"));
        // 正数求和
        System.out.println(new JmeterFunctionAdapter().execute("${__intSum(1,2,)}"));
        System.out.println(new JmeterFunctionAdapter().execute("${__longSum(12,21,)}"));

        // bshell
        System.out.println(new JmeterFunctionAdapter().execute("${__BeanShell(3+1,)}"));

        //System.out.println(new JmeterFunctionAdapter().execute("${__split(333|2|a,ab,|)}"));
        System.out.println(new JmeterFunctionAdapter().execute("${__counter(true,)}"));

        //System.out.println(new JmeterFunctionAdapter().execute("${__javaScript(5+1,)}"));
        //System.out.println(new JmeterFunctionAdapter().execute("${__groovy(4+5,)}"));

        //System.out.println(new JmeterFunctionAdapter().execute("${__property(user.dir)}"));

        System.out.println(new JmeterFunctionAdapter().execute("${__eval(1+1)}"));
        // base64
    }
}
