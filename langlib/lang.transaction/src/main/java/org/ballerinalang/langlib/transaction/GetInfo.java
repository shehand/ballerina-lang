/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.ballerinalang.langlib.transaction;

import org.ballerinalang.jvm.scheduling.Strand;
import org.ballerinalang.jvm.transactions.TransactionResourceManager;
import org.ballerinalang.jvm.values.ArrayValue;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;

import static org.ballerinalang.util.BLangCompilerConstants.TRANSACTION_VERSION;

/**
 * Extern function transaction:getInfo.
 *
 * @since 2.0.0-preview1
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "lang.transaction", version = TRANSACTION_VERSION,
        functionName = "getInfo",
        args = {@Argument(name = "xid", type = TypeKind.ARRAY)},
        returnType = {@ReturnType(type = TypeKind.BOOLEAN)},
        isPublic = true
)
public class GetInfo {

    public static Object getInfo(Strand strand, ArrayValue xid) {
        TransactionResourceManager transactionResourceManager = TransactionResourceManager.getInstance();
        return transactionResourceManager.transactionInfoMap.get(xid);
    }
}
