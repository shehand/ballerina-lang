/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.langserver.completion.latest;

import org.testng.annotations.DataProvider;

/**
 * Function Body Context tests.
 *
 * @since 2.0.0
 */
public class FunctionBodyTest extends CompletionTestNew {
    @DataProvider(name = "completion-data-provider")
    @Override
    public Object[][] dataProvider() {
        return this.getConfigsList();
    }

    @Override
    public Object[][] testSubset() {
         // Enable the following in order to test a subset of test cases
//          return new Object[][] {
//                  {"config1.json", this.getTestResourceDir()},
//                  {"config2.json", this.getTestResourceDir()},
//          };
        return new Object[0][];
    }

    @Override
    public String getTestResourceDir() {
        return "function_body";
    }
}
