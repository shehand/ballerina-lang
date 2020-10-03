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

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.ballerinalang.bindgen.command.BindingsGenerator.setExceptionList;
import static org.ballerinalang.bindgen.utils.BindgenConstants.CONSTRUCTOR_INTEROP_TYPE;
import static org.ballerinalang.bindgen.utils.BindgenUtils.getAlias;

/**
 * Class for storing details pertaining to a specific Java constructor used for Ballerina bridge code generation.
 *
 * @since 1.2.0
 */
public class JConstructor implements Cloneable {

    private String interopType;
    private String exceptionName;
    private String shortClassName;
    private String initObjectName;
    private String constructorName;
    private String exceptionConstName;
    private String externalFunctionName;

    private boolean returnError = false;
    private boolean hasException = false; // identifies if the Ballerina returns should have an error declared
    private boolean handleException = false; // identifies if the Java constructor throws an error
    private boolean javaArraysModule = false;

    private List<JParameter> parameters = new ArrayList<>();
    private StringBuilder paramTypes = new StringBuilder();

    JConstructor(Constructor c) {
        shortClassName = getAlias(c.getDeclaringClass());
        constructorName = c.getName();
        interopType = CONSTRUCTOR_INTEROP_TYPE;
        initObjectName = "_" + Character.toLowerCase(this.shortClassName.charAt(0)) + shortClassName.substring(1);

        // Loop through the parameters of the constructor to populate a list.
        for (Parameter param : c.getParameters()) {
            JParameter parameter = new JParameter(param);
            parameters.add(parameter);
            paramTypes.append(getAlias(param.getType()).toLowerCase(Locale.ENGLISH));
            if (parameter.getIsPrimitiveArray() || param.getType().isArray()) {
                javaArraysModule = true;
                returnError = true;
                hasException = true;
            }
        }

        // Set an identifier for the last parameter in the list.
        if (!parameters.isEmpty()) {
            JParameter lastParam = parameters.get(parameters.size() - 1);
            lastParam.setHasNext(false);
        }

        // Populate fields to identify error return types.
        for (Class<?> exceptionType : c.getExceptionTypes()) {
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
    }

    void setConstructorName(String name) {
        this.constructorName = name;
    }

    void setExternalFunctionName(String name) {
        this.externalFunctionName = name;
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    String getConstructorName() {
        return constructorName;
    }

    String getParamTypes() {
        return paramTypes.toString();
    }

    void setShortClassName(String shortClassName) {
        this.shortClassName = shortClassName;
    }

    boolean requireJavaArrays() {
        return javaArraysModule;
    }
}
