/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.langserver.sourceprune;

import org.ballerinalang.langserver.compiler.LSContextImpl;
import org.ballerinalang.langserver.compiler.LSOperation;

import java.util.HashMap;
import java.util.Map;

/**
 * Source Pruner context.
 *
 * @since 0.995.0
 */
public class SourcePruneContext extends LSContextImpl {
    private final Map<Key<?>, Object> props = new HashMap<>();

    public SourcePruneContext(LSOperation operation) {
        super(operation);
    }

    @Override
    public <V> void put(Key<V> key, V value) {
        props.put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V get(Key<V> key) {
        return (V) props.get(key);
    }
}
