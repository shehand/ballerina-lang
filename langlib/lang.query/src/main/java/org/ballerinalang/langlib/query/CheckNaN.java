/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.langlib.query;

import org.ballerinalang.jvm.scheduling.Strand;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;

import static org.ballerinalang.util.BLangCompilerConstants.QUERY_VERSION;

/**
 * Implementation of lang.query:checkNaN(float).
 *
 * @since Swan Lake
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "lang.query", version = QUERY_VERSION, functionName = "checkNaN",
        args = {@Argument(name = "x", type = TypeKind.FLOAT)},
        returnType = {@ReturnType(type = TypeKind.BOOLEAN)}
)
public class CheckNaN {

    public static boolean checkNaN(Strand strand, double x) {
        return Double.isNaN(x);
    }
}
