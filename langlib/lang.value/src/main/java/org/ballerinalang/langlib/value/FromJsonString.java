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

import org.ballerinalang.jvm.JSONParser;
import org.ballerinalang.jvm.api.BErrorCreator;
import org.ballerinalang.jvm.api.BStringUtils;
import org.ballerinalang.jvm.api.values.BString;
import org.ballerinalang.jvm.scheduling.Strand;
import org.ballerinalang.jvm.util.exceptions.BallerinaException;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;

import static org.ballerinalang.util.BLangCompilerConstants.VALUE_VERSION;

/**
 * Parse a string in JSON format and return the the value that it represents.
 *
 * @since 1.0
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "lang.value", version = VALUE_VERSION,
        functionName = "fromJsonString",
        args = {@Argument(name = "str", type = TypeKind.STRING)},
        returnType = {@ReturnType(type = TypeKind.JSON), @ReturnType(type = TypeKind.ERROR)},
        isPublic = true
)
public class FromJsonString {

    private static final BString JSON_STRING_ERROR = BStringUtils.fromString("{ballerina}FromJsonStringError");

    public static Object fromJsonString(Strand strand, BString value) {

        String str = value.getValue();
        if (str.equals("null")) {
            return null;
        }
        try {
            return JSONParser.parse(str);
        } catch (BallerinaException e) {
            return BErrorCreator.createError(BStringUtils.fromString("{ballerina/lang.value}FromJsonStringError"),
                                             BStringUtils.fromString(e.getMessage()));
        }
    }
}
