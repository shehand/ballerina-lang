/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerina.compiler.api.symbols;

import java.util.Map;
import java.util.Optional;

/**
 * Represents the markdown documentation attachment.
 *
 * @since 2.0.0
 */
public interface Documentation {

    /**
     * Get the description of the documentation.
     *
     * @return {@link Optional} description
     */
    public Optional<String> description();

    /**
     * Get the parameter list in the documentation as a map where parameter name being the key.
     *
     * @return {@link Map} of parameter names and descriptions
     */
    public Map<String, String> parameterMap();

    /**
     * Get the return value description.
     *
     * @return {@link Optional} return description
     */
    public Optional<String> returnDescription();
}
