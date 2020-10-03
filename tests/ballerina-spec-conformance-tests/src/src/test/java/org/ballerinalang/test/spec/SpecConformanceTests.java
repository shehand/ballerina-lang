/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.test.spec;

import org.ballerinalang.test.runtime.util.BTestUtil;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;

/**
 * Test class to run the Ballerina Specification Conformance Test Suite.
 *
 * @since 0.990.4
 */
public class SpecConformanceTests {

    @Test
    public void testSpecConformance() {
        assertFalse(BTestUtil.runTestsInProject("", true).isFailure());
    }
}
