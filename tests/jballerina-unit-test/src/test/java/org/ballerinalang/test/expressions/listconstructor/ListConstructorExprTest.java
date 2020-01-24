/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.test.expressions.listconstructor;

import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.test.util.BAssertUtil;
import org.ballerinalang.test.util.BCompileUtil;
import org.ballerinalang.test.util.BRunUtil;
import org.ballerinalang.test.util.CompileResult;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test cases for list constructor expressions.
 */
public class ListConstructorExprTest {

    private CompileResult result, resultNegative;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile("test-src/expressions/listconstructor/list_constructor.bal");
        resultNegative = BCompileUtil.compile(
                "test-src/expressions/listconstructor/list_constructor_negative.bal");
    }

    @Test
    public void testListConstructorExpr() {
        BValue[] returns = BRunUtil.invoke(result, "testListConstructorExpr");
        Assert.assertEquals(returns.length, 1);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    @Test
    public void diagnosticsTest() {
        int i = 0;
        BAssertUtil.validateError(resultNegative, i++, "invalid list constructor expression: " +
                "types cannot be inferred for '[v1, v2, v3]'", 18, 24);
        BAssertUtil.validateError(resultNegative, i++, "tuple and expression size does not match",
                22, 20);
        BAssertUtil.validateError(resultNegative, i++, "tuple and expression size does not match",
                23, 34);
        Assert.assertEquals(resultNegative.getErrorCount(), i);
    }
}
