/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.wso2.ballerinalang.compiler.bir.codegen.interop;

import org.ballerinalang.compiler.BLangCompilerException;
import org.objectweb.asm.ClassWriter;
import org.wso2.ballerinalang.compiler.bir.codegen.JvmCodeGenUtil;
import org.wso2.ballerinalang.compiler.bir.codegen.JvmMethodGen;
import org.wso2.ballerinalang.compiler.bir.codegen.JvmPackageGen;
import org.wso2.ballerinalang.compiler.bir.codegen.internal.AsyncDataCollector;
import org.wso2.ballerinalang.compiler.bir.model.BIRNode;
import org.wso2.ballerinalang.compiler.bir.model.BIRNode.BIRBasicBlock;
import org.wso2.ballerinalang.compiler.bir.model.BIRNode.BIRFunction;
import org.wso2.ballerinalang.compiler.bir.model.BIRNode.BIRPackage;
import org.wso2.ballerinalang.compiler.bir.model.BIRNode.BIRVariableDcl;
import org.wso2.ballerinalang.compiler.bir.model.BIROperand;
import org.wso2.ballerinalang.compiler.bir.model.BIRTerminator;
import org.wso2.ballerinalang.compiler.bir.model.InstructionKind;
import org.wso2.ballerinalang.compiler.semantics.model.SymbolTable;
import org.wso2.ballerinalang.compiler.semantics.model.types.BInvokableType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BType;
import org.wso2.ballerinalang.compiler.util.TypeTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.wso2.ballerinalang.compiler.bir.codegen.JvmCodeGenUtil.toNameString;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.WRAPPER_GEN_BB_ID_NAME;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmDesugarPhase.addDefaultableBooleanVarsToSignature;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmDesugarPhase.enrichWithDefaultableParamInits;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmDesugarPhase.insertAndGetNextBasicBlock;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmPackageGen.cleanupPackageName;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmPackageGen.getFunctionWrapper;
import static org.wso2.ballerinalang.compiler.bir.codegen.interop.AnnotationProc.getInteropAnnotValue;
import static org.wso2.ballerinalang.compiler.bir.codegen.interop.InteropMethodGen.createJInteropFunctionWrapper;
import static org.wso2.ballerinalang.compiler.bir.codegen.interop.InteropMethodGen.desugarInteropFuncs;
import static org.wso2.ballerinalang.compiler.bir.codegen.interop.InteropMethodGen.genJFieldForInteropField;

/**
 * Extern method generation class for JVM byte code generation.
 *
 * @since 1.2.0
 */
public class ExternalMethodGen {

    public static void genJMethodForBExternalFunc(BIRFunction birFunc,
                                                  ClassWriter cw,
                                                  BIRPackage birModule,
                                                  BType attachedType,
                                                  JvmMethodGen jvmMethodGen,
                                                  JvmPackageGen jvmPackageGen,
                                                  String moduleClassName,
                                                  AsyncDataCollector lambdaGenMetadata) {

        ExternalFunctionWrapper extFuncWrapper = getExternalFunctionWrapper(birModule, birFunc, attachedType,
                jvmPackageGen);

        if (extFuncWrapper instanceof JFieldFunctionWrapper) {
            genJFieldForInteropField((JFieldFunctionWrapper) extFuncWrapper, cw, birModule, jvmPackageGen,
                                     jvmMethodGen, moduleClassName, lambdaGenMetadata);
        } else {
            jvmMethodGen.genJMethodForBFunc(birFunc, cw, birModule, moduleClassName,
                                            attachedType, lambdaGenMetadata);
        }
    }

    public static void injectDefaultParamInits(BIRPackage module, JvmMethodGen jvmMethodGen,
                                               JvmPackageGen jvmPackageGen) {

        // filter out functions.
        List<BIRFunction> functions = module.functions;
        if (!functions.isEmpty()) {
            int funcSize = functions.size();
            int count = 3;

            // Generate classes for other functions.
            while (count < funcSize) {
                BIRFunction birFunc = functions.get(count);
                count = count + 1;
                BIRFunctionWrapper extFuncWrapper = lookupBIRFunctionWrapper(module, birFunc, null, jvmPackageGen);
                if (extFuncWrapper instanceof OldStyleExternalFunctionWrapper) {
                    desugarOldExternFuncs((OldStyleExternalFunctionWrapper) extFuncWrapper, birFunc, jvmMethodGen);
                    enrichWithDefaultableParamInits(birFunc, jvmMethodGen);
                } else if (extFuncWrapper instanceof JMethodFunctionWrapper) {
                    desugarInteropFuncs((JMethodFunctionWrapper) extFuncWrapper, birFunc, jvmMethodGen);
                    enrichWithDefaultableParamInits(birFunc, jvmMethodGen);
                } else if (!(extFuncWrapper instanceof JFieldFunctionWrapper)) {
                    enrichWithDefaultableParamInits(birFunc, jvmMethodGen);
                }
            }
        }

    }

    public static void desugarOldExternFuncs(OldStyleExternalFunctionWrapper extFuncWrapper, BIRFunction birFunc,
                                             JvmMethodGen jvmMethodGen) {

        BType retType = birFunc.type.retType;

        BIROperand retRef = null;
        if (retType.tag != TypeTags.NIL) {
            BIRVariableDcl localVar = birFunc.localVars.get(0);
            BIRVariableDcl variableDcl = new BIRVariableDcl(retType, localVar.name, localVar.scope, localVar.kind);
            retRef = new BIROperand(variableDcl);
        }

        jvmMethodGen.resetIds();

        BIRBasicBlock beginBB = insertAndGetNextBasicBlock(birFunc.basicBlocks, WRAPPER_GEN_BB_ID_NAME, jvmMethodGen);
        BIRBasicBlock retBB = insertAndGetNextBasicBlock(birFunc.basicBlocks, WRAPPER_GEN_BB_ID_NAME, jvmMethodGen);

        List<BIROperand> args = new ArrayList<>();

        BIRVariableDcl receiver = birFunc.receiver;
        if (!(receiver == null)) {

            BIROperand argRef = new BIROperand(receiver);
            args.add(argRef);
        }

        Set<BIRNode.BIRFunctionParameter> birFuncParams = birFunc.parameters.keySet();

        for (BIRNode.BIRFunctionParameter birFuncParam : birFuncParams) {
            BIROperand argRef = new BIROperand(birFuncParam);
            args.add(argRef);
        }

        String jMethodName = birFunc.name.value;
        beginBB.terminator = new JavaMethodCall(birFunc.pos, InstructionKind.PLATFORM, args, retRef,
                                                extFuncWrapper.jClassName, extFuncWrapper.jMethodVMSig, jMethodName,
                                                retBB);

        retBB.terminator = new BIRTerminator.Return(birFunc.pos);
    }

    private static ExternalFunctionWrapper getExternalFunctionWrapper(BIRPackage birModule, BIRFunction birFunc,
                                                                      BType attachedType, JvmPackageGen jvmPackageGen) {

        BIRFunctionWrapper birFuncWrapper = lookupBIRFunctionWrapper(birModule, birFunc, attachedType, jvmPackageGen);
        if (birFuncWrapper instanceof ExternalFunctionWrapper) {
            return (ExternalFunctionWrapper) birFuncWrapper;
        } else {
            throw new BLangCompilerException("cannot find function definition for : " + birFunc.name.value);
            // TODO improve
        }
    }

    public static BIRFunctionWrapper lookupBIRFunctionWrapper(BIRPackage birModule, BIRFunction birFunc,
                                                              BType attachedType, JvmPackageGen jvmPackageGen) {

        String lookupKey;
        String currentPackageName = JvmCodeGenUtil.getPackageName(birModule);

        String birFuncName = birFunc.name.value;

        if (attachedType == null) {
            lookupKey = currentPackageName + birFuncName;
        } else if (attachedType.tag == TypeTags.OBJECT) {
            lookupKey = currentPackageName + toNameString(attachedType) + "." + birFuncName;
        } else {
            throw new BLangCompilerException(String.format("Java method generation for the receiver type %s " +
                    "is not supported: ", attachedType));
        }

        BIRFunctionWrapper birFuncWrapper = jvmPackageGen.lookupBIRFunctionWrapper(lookupKey);
        if (birFuncWrapper != null) {
            return birFuncWrapper;
        } else {
            throw new BLangCompilerException("cannot find function definition for : " + lookupKey);
        }
    }

    public static OldStyleExternalFunctionWrapper createOldStyleExternalFunctionWrapper(BIRFunction birFunc,
                                                                                        String orgName,
                                                                                        String moduleName,
                                                                                        String version,
                                                                                        String birModuleClassName,
                                                                                        String jClassName,
                                                                                        boolean isEntryModule,
                                                                                        SymbolTable symbolTable) {

        List<BType> jMethodPramTypes = new ArrayList<>(birFunc.type.paramTypes);
        if (isEntryModule) {
            addDefaultableBooleanVarsToSignature(birFunc, symbolTable.booleanType);
        }
        BInvokableType functionTypeDesc = birFunc.type;

        BType restType = functionTypeDesc.restType;

        if (restType != null) {
            jMethodPramTypes.add(restType);
        }

        BIRVariableDcl receiver = birFunc.receiver;

        String jvmMethodDescription;
        String jMethodVMSig;
        if (receiver == null) {
            jvmMethodDescription = JvmCodeGenUtil.getMethodDesc(functionTypeDesc.paramTypes,
                                                                functionTypeDesc.retType);
            jMethodVMSig = getExternMethodDesc(jMethodPramTypes, functionTypeDesc.retType);
        } else {
            jvmMethodDescription = JvmCodeGenUtil.getMethodDesc(functionTypeDesc.paramTypes,
                                                                functionTypeDesc.retType, receiver.type);
            jMethodVMSig = getExternMethodDesc(jMethodPramTypes, functionTypeDesc.retType, receiver.type);
        }
        return new OldStyleExternalFunctionWrapper(orgName, moduleName, version, birFunc, birModuleClassName,
                                                   jvmMethodDescription, jClassName, jMethodPramTypes, jMethodVMSig);
    }

    public static String getExternMethodDesc(List<BType> paramTypes, BType retType) {
        return JvmCodeGenUtil.INITIAL_MEHOD_DESC + JvmCodeGenUtil.populateMethodDesc(paramTypes) +
                generateExternReturnType(retType);
    }

    public static String getExternMethodDesc(List<BType> paramTypes, BType retType, BType attachedType) {
        return JvmCodeGenUtil.INITIAL_MEHOD_DESC + JvmCodeGenUtil.getArgTypeSignature(attachedType) +
                JvmCodeGenUtil.populateMethodDesc(paramTypes) + generateExternReturnType(retType);
    }

    static String generateExternReturnType(BType bType) {
        bType = JvmCodeGenUtil.TYPE_BUILDER.build(bType);
        if (bType == null || bType.tag == TypeTags.NIL || bType.tag == TypeTags.NEVER) {
            return ")V";
        }
        return JvmCodeGenUtil.generateReturnType(bType);
    }

    public static BIRFunctionWrapper createExternalFunctionWrapper(InteropValidator interopValidator,
                                                                   BIRFunction birFunc, String orgName,
                                                                   String moduleName, String version,
                                                                   String birModuleClassName,
                                                                   String lookupKey,
                                                                   JvmPackageGen jvmPackageGen) {

        BIRFunctionWrapper birFuncWrapper;
        InteropValidationRequest jInteropValidationReq = getInteropAnnotValue(birFunc);
        if (jInteropValidationReq == null) {
            // This is a old-style external Java interop function
            String pkgName = JvmCodeGenUtil.getPackageName(orgName, moduleName, version);
            String jClassName = jvmPackageGen.lookupExternClassName(cleanupPackageName(pkgName), lookupKey);
            if (jClassName != null) {
                if (JvmCodeGenUtil.isBallerinaBuiltinModule(orgName, moduleName)) {
                    birFuncWrapper = getFunctionWrapper(birFunc, orgName, moduleName, version, jClassName);
                } else {
                    birFuncWrapper = createOldStyleExternalFunctionWrapper(birFunc, orgName, moduleName, version,
                                                                           birModuleClassName, jClassName,
                                                                           interopValidator.isEntryModuleValidation(),
                                                                           jvmPackageGen.symbolTable);
                }
            } else {
                throw new BLangCompilerException("cannot find full qualified class name for extern function : " +
                                                         pkgName + birFunc.name.value);
            }
        } else {
            birFuncWrapper = createJInteropFunctionWrapper(interopValidator, jInteropValidationReq, birFunc, orgName,
                                                           moduleName, version, birModuleClassName,
                                                           jvmPackageGen.symbolTable);
        }

        return birFuncWrapper;
    }

    private ExternalMethodGen() {
    }
}
