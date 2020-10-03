/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.ballerinalang.test.taintchecking.connectors;

import org.ballerinalang.test.util.BAssertUtil;
import org.ballerinalang.test.util.BCompileUtil;
import org.ballerinalang.test.util.CompileResult;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test HTTP Client related natives and Request/Response objects for taint checking operations.
 */
@Test
public class HttpClientTest {

    @Test
    public void testHttpClient() {
        CompileResult result = BCompileUtil.compile("test-src/taintchecking/connectors/httpclient.bal");
        Assert.assertEquals(result.getDiagnostics().length, 0);
    }

    @Test
    public void testHttpClientNegative() {
        CompileResult result = BCompileUtil.compile("test-src/taintchecking/connectors/httpclient-negative.bal");
        Assert.assertEquals(result.getDiagnostics().length, 2);
        BAssertUtil.validateError(result, 0, "tainted value passed to untainted parameter 'path'", 12, 42);
        BAssertUtil.validateError(result, 1, "tainted value passed to untainted parameter 'secureIn'", 16, 28);
    }
}
