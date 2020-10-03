/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.bindgen.model;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.ballerinalang.bindgen.command.BindingsGenerator.getAllJavaClasses;
import static org.ballerinalang.bindgen.command.BindingsGenerator.setClassListForLooping;
import static org.ballerinalang.bindgen.command.BindingsGenerator.setExceptionList;
import static org.ballerinalang.bindgen.utils.BindgenConstants.ARRAY_BRACKETS;
import static org.ballerinalang.bindgen.utils.BindgenConstants.BALLERINA_RESERVED_WORDS;
import static org.ballerinalang.bindgen.utils.BindgenConstants.JAVA_STRING;
import static org.ballerinalang.bindgen.utils.BindgenConstants.JAVA_STRING_ARRAY;
import static org.ballerinalang.bindgen.utils.BindgenConstants.METHOD_INTEROP_TYPE;
import static org.ballerinalang.bindgen.utils.BindgenUtils.getAlias;
import static org.ballerinalang.bindgen.utils.BindgenUtils.getBallerinaHandleType;
import static org.ballerinalang.bindgen.utils.BindgenUtils.getBallerinaParamType;
import static org.ballerinalang.bindgen.utils.BindgenUtils.getJavaType;
import static org.ballerinalang.bindgen.utils.BindgenUtils.isStaticMethod;

/**
 * Class for storing details pertaining to a specific Java method used for Ballerina bridge code generation.
 *
 * @since 1.2.0
 */
public class JMethod {

    private boolean isStatic;
    private boolean hasParams = true;
    private boolean hasReturn = false;
    private boolean returnError = false;
    private boolean objectReturn = false;
    private boolean reservedWord = false;
    private boolean isArrayReturn = false;
    private boolean hasException = false;
    private boolean handleException = false;
    private boolean isStringReturn = false;
    private boolean javaArraysModule = false;
    private boolean hasPrimitiveParam = false;

    private Method method;
    private String methodName;
    private String returnType;
    private String externalType;
    private String exceptionName;
    private String returnTypeJava;
    private String shortClassName;
    private String javaMethodName;
    private String exceptionConstName;
    private String returnComponentType;
    private String interopType = METHOD_INTEROP_TYPE;

    private List<JParameter> parameters = new ArrayList<>();
    private StringBuilder paramTypes = new StringBuilder();

    JMethod(Method m) {
        method = m;
        javaMethodName = m.getName();
        methodName = m.getName();
        shortClassName = getAlias(m.getDeclaringClass());
        isStatic = isStaticMethod(m);

        // Set the attributes required to identify different return types.
        Class returnTypeClass = m.getReturnType();
        if (!returnTypeClass.equals(Void.TYPE)) {
            setReturnTypeAttributes(returnTypeClass);
        }

        // Set the attributes relevant to error returns.
        for (Class<?> exceptionType : m.getExceptionTypes()) {
            try {
                if (!this.getClass().getClassLoader().loadClass(RuntimeException.class.getCanonicalName())
                        .isAssignableFrom(exceptionType)) {
                    JError jError = new JError(exceptionType);
                    exceptionName = jError.getShortExceptionName();
                    exceptionConstName = jError.getExceptionConstName();
                    setExceptionList(jError);
                    hasException = true;
                    handleException = true;
                    break;
                }
            } catch (ClassNotFoundException ignore) {
            }
        }

        // Set the attributes required to identify different parameters.
        setParameters(m.getParameters());
        if (!parameters.isEmpty()) {
            JParameter lastParam = parameters.get(parameters.size() - 1);
            lastParam.setHasNext(false);
        } else {
            hasParams = false;
        }

        List<String> reservedWords = Arrays.asList(BALLERINA_RESERVED_WORDS);
        if (reservedWords.contains(methodName)) {
            reservedWord = true;
        }
        if (objectReturn && !getAllJavaClasses().contains(returnTypeClass.getName())) {
            if (isArrayReturn) {
                setClassListForLooping(returnTypeClass.getComponentType().getName());
            } else {
                setClassListForLooping(returnTypeClass.getName());
            }
        }
    }

    private void setReturnTypeAttributes(Class returnTypeClass) {
        hasReturn = true;
        returnTypeJava = getJavaType(returnTypeClass);
        externalType = getBallerinaHandleType(returnTypeClass);
        returnType = getBallerinaParamType(returnTypeClass);
        returnType = getExceptionName(returnTypeClass, returnType);
        if (returnTypeClass.isArray()) {
            javaArraysModule = true;
            hasException = true;
            returnError = true;
            isArrayReturn = true;
            if (returnTypeClass.getComponentType().isPrimitive()) {
                objectReturn = false;
            } else if (getAlias(returnTypeClass).equals(JAVA_STRING_ARRAY)) {
                objectReturn = false;
            } else {
                returnComponentType = getAlias(returnTypeClass.getComponentType());
                returnComponentType = getExceptionName(returnTypeClass.getComponentType(), returnComponentType);
                returnType = returnComponentType + ARRAY_BRACKETS;
                objectReturn = true;
            }
        } else if (returnTypeClass.isPrimitive()) {
            objectReturn = false;
        } else if (getAlias(returnTypeClass).equals(JAVA_STRING)) {
            isStringReturn = true;
        } else {
            objectReturn = true;
        }
    }

    private String getExceptionName(Class exception, String name) {
        try {
            // Append the prefix "J" in front of bindings generated for Java exceptions.
            if (this.getClass().getClassLoader().loadClass(Exception.class.getCanonicalName())
                    .isAssignableFrom(exception)) {
                return "J" + name;
            }
        } catch (ClassNotFoundException ignore) {
            // Silently ignore if the exception class cannot be found.
        }
        return name;
    }

    private void setParameters(Parameter[] paramArr) {
        for (Parameter param : paramArr) {
            paramTypes.append(getAlias(param.getType()).toLowerCase(Locale.ENGLISH));
            JParameter parameter = new JParameter(param);
            parameters.add(parameter);
            if (parameter.getIsPrimitiveArray()) {
                javaArraysModule = true;
                returnError = true;
                hasPrimitiveParam = true;
                hasException = true;
            }
            if (parameter.isObjArrayParam() || parameter.getIsStringArray()) {
                javaArraysModule = true;
                returnError = true;
                hasException = true;
            }
        }
    }

    public String getJavaMethodName() {
        return javaMethodName;
    }

    String getParamTypes() {
        return paramTypes.toString();
    }

    public Boolean getHasReturn() {
        return hasReturn;
    }

    public Boolean getHasException() {
        return hasException;
    }

    public Boolean getIsStringReturn() {
        return isStringReturn;
    }

    public Boolean getHasPrimitiveParam() {
        return hasPrimitiveParam;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<JParameter> getParameters() {
        return parameters;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean hasParams() {
        return hasParams;
    }

    public String getExternalType() {
        return externalType;
    }

    public boolean isHandleException() {
        return handleException;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public boolean isReturnError() {
        return returnError;
    }

    void setShortClassName(String shortClassName) {
        this.shortClassName = shortClassName;
    }

    public Method getMethod() {
        return method;
    }

    boolean requireJavaArrays() {
        return javaArraysModule;
    }
}
