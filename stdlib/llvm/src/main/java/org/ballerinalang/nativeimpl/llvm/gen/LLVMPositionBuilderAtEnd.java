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
import org.ballerinalang.jvm.values.MapValue;
import org.ballerinalang.nativeimpl.llvm.FFIUtil;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef;
import org.bytedeco.llvm.LLVM.LLVMBuilderRef;

import static org.ballerinalang.model.types.TypeKind.RECORD;
import static org.bytedeco.llvm.global.LLVM.LLVMPositionBuilderAtEnd;

/**
 * Auto generated class.
 *
 * @since 1.0.3
 */
@BallerinaFunction(
        orgName = "ballerina", packageName = "llvm",
        functionName = "llvmPositionBuilderAtEnd",
        args = {
                @Argument(name = "builder", type = RECORD, structType = "LLVMBuilderRef"),
                @Argument(name = "block", type = RECORD, structType = "LLVMBasicBlockRef"),
        })
public class LLVMPositionBuilderAtEnd {

    public static void llvmPositionBuilderAtEnd(Strand strand, MapValue<String, Object> builder,
                                                MapValue<String, Object> block) {

        LLVMBuilderRef builderRef = (LLVMBuilderRef) FFIUtil.getRecodeArgumentNative(builder);
        LLVMBasicBlockRef blockRef = (LLVMBasicBlockRef) FFIUtil.getRecodeArgumentNative(block);
        LLVMPositionBuilderAtEnd(builderRef, blockRef);
    }
}
