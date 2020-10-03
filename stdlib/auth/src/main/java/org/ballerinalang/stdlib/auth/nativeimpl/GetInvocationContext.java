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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.stdlib.auth.nativeimpl;

import org.ballerinalang.jvm.api.values.BString;
import org.ballerinalang.jvm.scheduling.Scheduler;
import org.ballerinalang.jvm.scheduling.Strand;
import org.ballerinalang.jvm.values.MapValue;
import org.ballerinalang.jvm.values.ValueCreator;

import static org.ballerinalang.jvm.util.BLangConstants.BALLERINA_AUTH_PKG_ID;

/**
 * Extern function to get auth invocation context record.
 *
 */
public class GetInvocationContext {

    public static MapValue<BString, Object> getInvocationContext() {
        return getInvocationContextRecord(Scheduler.getStrand());
    }

    private static final String AUTH_INVOCATION_CONTEXT_PROPERTY = "AuthInvocationContext";
    private static final String RECORD_TYPE_INVOCATION_CONTEXT = "InvocationContext";
    private static final ValueCreator valueCreator = ValueCreator.getValueCreator(BALLERINA_AUTH_PKG_ID.toString());

    private static MapValue<BString, Object> getInvocationContextRecord(Strand strand) {
        MapValue<BString, Object> invocationContext =
                (MapValue<BString, Object>) strand.getProperty(AUTH_INVOCATION_CONTEXT_PROPERTY);
        if (invocationContext == null) {
            invocationContext = valueCreator.createRecordValue(RECORD_TYPE_INVOCATION_CONTEXT);
            strand.setProperty(AUTH_INVOCATION_CONTEXT_PROPERTY, invocationContext);
        }
        return invocationContext;
    }
}
