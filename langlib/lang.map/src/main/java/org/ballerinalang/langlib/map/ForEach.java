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

package org.ballerinalang.langlib.map;

import org.ballerinalang.jvm.runtime.AsyncUtils;
import org.ballerinalang.jvm.scheduling.Scheduler;
import org.ballerinalang.jvm.scheduling.Strand;
import org.ballerinalang.jvm.scheduling.StrandMetadata;
import org.ballerinalang.jvm.values.FPValue;
import org.ballerinalang.jvm.values.MapValue;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;

import java.util.concurrent.atomic.AtomicInteger;

import static org.ballerinalang.jvm.util.BLangConstants.BALLERINA_BUILTIN_PKG_PREFIX;
import static org.ballerinalang.jvm.util.BLangConstants.MAP_LANG_LIB;
import static org.ballerinalang.util.BLangCompilerConstants.MAP_VERSION;

/**
 * Native implementation of lang.map:forEach(map&lt;Type&gt;, function).
 *
 * @since 1.0
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "lang.map", version = MAP_VERSION, functionName = "forEach",
        args = {@Argument(name = "m", type = TypeKind.MAP), @Argument(name = "func", type = TypeKind.FUNCTION)},
        isPublic = true
)
public class ForEach {

    private static final StrandMetadata METADATA = new StrandMetadata(BALLERINA_BUILTIN_PKG_PREFIX, MAP_LANG_LIB,
                                                                      MAP_VERSION, "forEach");

    public static void forEach(Strand strand, MapValue<?, ?> m, FPValue<Object, Object> func) {
        int size = m.size();
        AtomicInteger index = new AtomicInteger(-1);
        AsyncUtils
                .invokeFunctionPointerAsyncIteratively(func, null, METADATA, size,
                                                       () -> new Object[]{strand,
                                                               m.get(m.getKeys()[index.incrementAndGet()]), true},
                                                       result -> {
                                                       }, () -> null, Scheduler.getStrand().scheduler);
    }
}
