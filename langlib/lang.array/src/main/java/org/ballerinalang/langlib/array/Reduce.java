/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.ballerinalang.langlib.array;

import org.ballerinalang.jvm.runtime.AsyncUtils;
import org.ballerinalang.jvm.scheduling.Scheduler;
import org.ballerinalang.jvm.scheduling.Strand;
import org.ballerinalang.jvm.scheduling.StrandMetadata;
import org.ballerinalang.jvm.types.BType;
import org.ballerinalang.jvm.values.ArrayValue;
import org.ballerinalang.jvm.values.FPValue;
import org.ballerinalang.jvm.values.utils.GetFunction;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.ballerinalang.jvm.util.BLangConstants.ARRAY_LANG_LIB;
import static org.ballerinalang.jvm.util.BLangConstants.BALLERINA_BUILTIN_PKG_PREFIX;
import static org.ballerinalang.jvm.values.utils.ArrayUtils.getElementAccessFunction;
import static org.ballerinalang.util.BLangCompilerConstants.ARRAY_VERSION;

/**
 * Native implementation of lang.array:reduce(Type[], function).
 *
 * @since 1.0
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "lang.array", version = ARRAY_VERSION, functionName = "reduce",
        args = {@Argument(name = "arr", type = TypeKind.ARRAY), @Argument(name = "func", type = TypeKind.FUNCTION),
                @Argument(name = "initial", type = TypeKind.ANY)},
        returnType = {@ReturnType(type = TypeKind.ARRAY)},
        isPublic = true
)
public class Reduce {

    private static final StrandMetadata METADATA = new StrandMetadata(BALLERINA_BUILTIN_PKG_PREFIX, ARRAY_LANG_LIB,
                                                                      ARRAY_VERSION, "reduce");

    public static Object reduce(Strand strand, ArrayValue arr, FPValue<Object, Boolean> func, Object initial) {
        BType arrType = arr.getType();
        int size = arr.size();
        GetFunction getFn = getElementAccessFunction(arrType, "reduce()");
        AtomicReference<Object> accum = new AtomicReference<>(initial);
        AtomicInteger index = new AtomicInteger(-1);
        AsyncUtils
                .invokeFunctionPointerAsyncIteratively(func, null, METADATA, size,
                                                       () -> new Object[]{strand, accum.get(), true,
                                                               getFn.get(arr, index.incrementAndGet()), true},
                                                       accum::set, accum::get, Scheduler.getStrand().scheduler);
        return accum.get();

        
    }
}
