/*
 *   Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
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

package org.ballerinalang.langlib.value;

import org.ballerinalang.jvm.JSONUtils;
import org.ballerinalang.jvm.scheduling.Strand;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;

import static org.ballerinalang.util.BLangCompilerConstants.VALUE_VERSION;


/**
 * Merge two JSON values.
 *
 * @since 1.0
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "lang.value", version = VALUE_VERSION,
        functionName = "mergeJson",
        args = {@Argument(name = "j1", type = TypeKind.JSON), @Argument(name = "j2", type = TypeKind.JSON)},
        returnType = {@ReturnType(type = TypeKind.JSON), @ReturnType(type = TypeKind.ERROR)},
        isPublic = true
)
public class MergeJson {

    public static Object mergeJson(Strand strand, Object j1, Object j2) {
        return JSONUtils.mergeJson(j1, j2, true);
    }

}
