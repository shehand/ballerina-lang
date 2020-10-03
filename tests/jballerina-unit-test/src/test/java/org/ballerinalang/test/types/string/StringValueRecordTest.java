/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.test.types.string;

import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.test.util.BCompileUtil;
import org.ballerinalang.test.util.BRunUtil;
import org.ballerinalang.test.util.CompileResult;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test StringValue impl of ballerina string.
 */
public class StringValueRecordTest {
    private CompileResult result;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile("test-src/types/string/string-value-record-test.bal");
    }

    @Test
    public void testRecordStringValue() {
        testAndAssert("testRecordStringValue", 5);
    }

    @Test
    public void testRecordGetKeys() {
        testAndAssert("testRecordGetKeys", 31);
    }

    @Test
    public void testMapToKeys() {
        testAndAssert("testMapToKeys", 38);
    }

    @Test
    public void testOpenRecord() {
        testAndAssert("testOpenRecord", 18);
    }

    private void testAndAssert(String funcName, int i) {
        BValue[] returns = BRunUtil.invoke(result, funcName);
        Assert.assertEquals(returns[0].getClass(), BInteger.class);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), i);
    }
}
