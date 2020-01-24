// Copyright (c) 2019 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.ballerinalang.nativeimpl.llvm.gen;

import org.ballerinalang.jvm.scheduling.Strand;
import org.ballerinalang.jvm.types.BPackage;
import org.ballerinalang.jvm.values.MapValue;
import org.ballerinalang.nativeimpl.llvm.FFIUtil;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;
import org.bytedeco.javacpp.BytePointer;

import static org.ballerinalang.model.types.TypeKind.RECORD;
import static org.bytedeco.llvm.global.LLVM.LLVMGetDefaultTargetTriple;

/**
 * Auto generated class.
 *
 * @since 1.0.3
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "llvm",
        functionName = "llvmGetDefaultTargetTriple",
        returnType = {
                @ReturnType(type = RECORD, structType = "BytePointer", structPackage = "ballerina/llvm"),
        }
)
public class LLVMGetDefaultTargetTriple {

    public static MapValue<String, Object> llvmGetDefaultTargetTriple(Strand strand) {

        BytePointer returnValue = LLVMGetDefaultTargetTriple();
        MapValue<String, Object> rerunWrapperRecode = FFIUtil.newRecord(new BPackage("ballerina",
                "llvm"), "BytePointer");
        FFIUtil.addNativeToRecode(returnValue, rerunWrapperRecode);
        return rerunWrapperRecode;
    }
}
