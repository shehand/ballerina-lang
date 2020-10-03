/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ballerinalang.openapi.model;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import org.ballerinalang.openapi.exception.BallerinaOpenApiException;
import org.ballerinalang.openapi.utils.CodegenUtils;

import java.io.PrintStream;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Wraps the {@link PathItem} from openapi models to provide an iterable object model
 * for operations.
 *
 * @since 0.967.0
 */
public class BallerinaPath implements BallerinaOpenApiObject<BallerinaPath, PathItem> {
    private String ref;
    private String summary;
    private String resourceName;
    private boolean noOperationsForPath;
    private boolean sameResourceOperationExists;
    private String description;
    private Set<Map.Entry<String, BallerinaOperation>> operations;
    private Set<Map.Entry<String, OperationCategory>> sameResourceOperations;
    private static final PrintStream outStream = System.err;

    public BallerinaPath() {
        this.operations = new LinkedHashSet<>();
        this.sameResourceOperations = new LinkedHashSet<>();
        this.sameResourceOperationExists = false;
    }

    @Override
    public BallerinaPath buildContext(PathItem item, OpenAPI openAPI) throws BallerinaOpenApiException {
        this.ref = item.get$ref();
        this.summary = item.getSummary();
        this.description = item.getDescription();

        Map<String, OperationCategory> categorizedOperations = new HashMap<>();
        Map.Entry<String, BallerinaOperation> entry;
        BallerinaOperation operation;

        // Iterate through the operation map and add operations belong to same ballerina resource.
        Map<PathItem.HttpMethod, Operation> operationMap = item.readOperationsMap();
        for (Map.Entry<PathItem.HttpMethod, Operation> operationI : operationMap.entrySet()) {
            String operationIId = operationI.getValue().getOperationId();
            boolean idMatched = false;
            if (operationIId != null) {
                for (Map.Entry<PathItem.HttpMethod, Operation> operationJ : operationMap.entrySet()) {
                    String operationJId = operationJ.getValue().getOperationId();
                    if (!operationIId.equals(operationJId) && CodegenUtils.normalizeForBIdentifier(operationIId)
                            .equals(CodegenUtils.normalizeForBIdentifier(operationJId))) {
                        idMatched = true;
                    }
                }
            }

            // Add operation ID if there is no operationID
            if (operationI.getValue().getOperationId() == null) {
                String resName = CodegenUtils.generateOperationId(openAPI);
                String pathName = BallerinaOpenApi.getPathName();
                operationI.getValue().setOperationId(resName);
                outStream.println("warning : `" + resName + "` is used as the resource name since the " +
                        "operation id is missing for " + pathName + " " + operationI.getKey());
            }

            operation = new BallerinaOperation().buildContext(operationI.getValue(), openAPI);
            if (idMatched) {
                entry = new AbstractMap.SimpleEntry<>(operationI.getKey().name(), operation);
                if (categorizedOperations.get(CodegenUtils.normalizeForBIdentifier(operationIId)) != null) {
                    categorizedOperations.get(CodegenUtils.normalizeForBIdentifier(operationIId)).addOperation(entry);
                    categorizedOperations.get(CodegenUtils.normalizeForBIdentifier(operationIId))
                            .addMethod(operationI.getKey().name());
                } else {
                    categorizedOperations.put(CodegenUtils.normalizeForBIdentifier(operationIId),
                            new OperationCategory(CodegenUtils.normalizeForBIdentifier(operationIId)));
                    categorizedOperations.get(CodegenUtils.normalizeForBIdentifier(operationIId)).addOperation(entry);
                    categorizedOperations.get(CodegenUtils.normalizeForBIdentifier(operationIId))
                            .addMethod(operationI.getKey().name());
                }
            } else {
                entry = new AbstractMap.SimpleEntry<>(operationI.getKey().name(), operation);
                operations.add(entry);
            }
        }
        sameResourceOperations = categorizedOperations.entrySet();
        this.setSameResourceOperationExists(!sameResourceOperations.isEmpty());
        this.setNoOperationsForPath(operationMap.isEmpty());
        return this;
    }

    @Override
    public BallerinaPath buildContext(PathItem item) throws BallerinaOpenApiException {
        return buildContext(item, null);
    }

    @Override
    public BallerinaPath getDefaultValue() {
        return null;
    }

    public String getRef() {
        return ref;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public Set<Map.Entry<String, BallerinaOperation>> getOperations() {
        return operations;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = CodegenUtils.normalizeForBIdentifier(resourceName);
    }

    public boolean isNoOperationsForPath() {
        return noOperationsForPath;
    }

    private void setNoOperationsForPath(boolean noOperationsForPath) {
        this.noOperationsForPath = noOperationsForPath;
    }

    private void setSameResourceOperationExists(boolean sameResourceOperationExists) {
        this.sameResourceOperationExists = sameResourceOperationExists;
    }

    public boolean isSameResourceOperationExists() {
        return sameResourceOperationExists;
    }
}
