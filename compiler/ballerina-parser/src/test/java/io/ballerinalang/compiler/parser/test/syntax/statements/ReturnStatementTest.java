/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerinalang.compiler.parser.test.syntax.statements;

import org.testng.annotations.Test;

/**
 * Test parsing return statements.
 */
public class ReturnStatementTest extends AbstractStatementTest {

    // Valid source tests

    @Test
    public void testSimplestReturnStatement() {
        test("return;", "return-stmt/return_stmt_assert_1.json");
    }

    @Test
    public void testReturnStatementWithInt() {
        test("return 6;", "return-stmt/return_stmt_assert_2.json");
    }

    @Test
    public void testReturnStatementWithExpr() {
        test("return 6+7;", "return-stmt/return_stmt_assert_3.json");
    }

    // Recovery tests

    @Test
    public void testSimplestReturnStatementWithMissingSemicolon() {
        test("return", "return-stmt/return_stmt_assert_4.json");
    }

    @Test
    public void testReturnStatementWithMissingSemicolon() {
        test("return 6", "return-stmt/return_stmt_assert_5.json");
    }
}
