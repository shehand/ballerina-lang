/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.ballerinalang.plugins.idea.run.configuration.file.service;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import org.ballerinalang.plugins.idea.run.configuration.GoRunningState;
import org.ballerinalang.plugins.idea.run.configuration.file.GoRunFileConfiguration;
import org.ballerinalang.plugins.idea.util.GoExecutor;
import org.jetbrains.annotations.NotNull;

public class GoRunServiceFileRunningState extends GoRunningState<GoRunFileConfiguration> {

    public GoRunServiceFileRunningState(@NotNull ExecutionEnvironment env, @NotNull Module module,
                                        GoRunFileConfiguration configuration) {
        super(env, module, configuration);
    }

    @Override
    protected GoExecutor patchExecutor(@NotNull GoExecutor executor) throws ExecutionException {
        GoRunFileConfiguration.Kind kind = getConfiguration().getRunKind();
        String command = "main";
        if (kind == GoRunFileConfiguration.Kind.SERVICE) {
            command = "service";
        }
        return executor
                .withParameters("run")
                .withParameters(command)
                .withParameterString(myConfiguration.getGoToolParams())
                .withParameters(myConfiguration.getFilePath());
    }
}

