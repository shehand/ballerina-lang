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

import org.ballerinalang.jvm.BallerinaValues;
import org.ballerinalang.jvm.scheduling.Strand;
import org.ballerinalang.jvm.types.BPackage;
import org.ballerinalang.jvm.values.ArrayValue;
import org.ballerinalang.jvm.values.MapValue;
import org.ballerinalang.nativeimpl.llvm.FFIUtil;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;

import static org.ballerinalang.model.types.TypeKind.ARRAY;
import static org.ballerinalang.model.types.TypeKind.INT;
import static org.ballerinalang.model.types.TypeKind.RECORD;
import static org.bytedeco.llvm.global.LLVM.LLVMFunctionType;

/**
 * Auto generated class.
 *
 * @since 1.0.3
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "llvm",
        functionName = "llvmFunctionType1",
        args = {
                @Argument(name = "returnType", type = RECORD, structType = "LLVMTypeRef"),
                @Argument(name = "paramTypes", type = ARRAY, elementType = RECORD),
                @Argument(name = "paramCount", type = INT),
                @Argument(name = "isVarArg", type = INT),
        },
        returnType = {
                @ReturnType(type = RECORD, structType = "LLVMTypeRef", structPackage = "ballerina/llvm"),
        }
)
public class LLVMFunctionType1 {

    public static MapValue<String, Object> llvmFunctionType1(Strand strand, MapValue<String, Object> returnType,
                                                             ArrayValue paramTypes, long paramCount,
                                                             long isVarArg) {

        LLVMTypeRef returnTypeRef = (LLVMTypeRef) FFIUtil.getRecodeArgumentNative(returnType);
        Pointer[] paramTypesRef = FFIUtil.getRecodeArrayArgumentNative(paramTypes);
        PointerPointer paramTypesWrapped = new PointerPointer(paramTypesRef);
        int paramCountRef = (int) paramCount;
        int isVarArgRef = (int) isVarArg;
        LLVMTypeRef returnValue = LLVMFunctionType(returnTypeRef, paramTypesWrapped, paramCountRef, isVarArgRef);
        MapValue<String, Object> returnWrappedRecord = BallerinaValues.createRecordValue(new BPackage("ballerina",
                "llvm"), "LLVMTypeRef");
        FFIUtil.addNativeToRecode(returnValue, returnWrappedRecord);
        return returnWrappedRecord;
    }
}
