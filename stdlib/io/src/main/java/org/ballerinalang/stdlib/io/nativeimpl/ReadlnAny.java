/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.stdlib.io.nativeimpl;

import org.ballerinalang.jvm.api.BStringUtils;
import org.ballerinalang.jvm.api.values.BString;

import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Extern function ballerina/io:readln.
 *
 * @since 0.97
 */
public class ReadlnAny {

    private static Scanner sc = new Scanner(System.in, Charset.defaultCharset().displayName());

    public static BString readln(Object result) {
        if (result != null) {
            System.out.print(result.toString());
        }
        return BStringUtils.fromString(sc.nextLine());
    }
}
