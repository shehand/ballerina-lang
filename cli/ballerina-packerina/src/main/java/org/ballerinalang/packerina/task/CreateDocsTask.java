/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.ballerinalang.packerina.task;

import org.ballerinalang.docgen.docs.BallerinaDocGenerator;
import org.ballerinalang.docgen.model.ModuleDoc;
import org.ballerinalang.packerina.buildcontext.BuildContext;
import org.ballerinalang.packerina.buildcontext.BuildContextField;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.ballerinalang.tool.LauncherUtils.createLauncherException;
import static org.wso2.ballerinalang.compiler.util.ProjectDirConstants.TARGET_API_DOC_DIRECTORY;

/**
 * Task for creating API docs for modules.
 */
public class CreateDocsTask implements Task {

    private boolean combine;
    private boolean excludeIndex;
    private boolean toJson;
    private Path jsonPath;

    public CreateDocsTask(boolean toJson, Path jsonPath, boolean excludeIndex, boolean combine) {
        this.combine = combine;
        this.excludeIndex = excludeIndex;
        this.toJson = toJson;
        this.jsonPath = jsonPath;
    }

    @Override
    public void execute(BuildContext buildContext) {
        Path sourceRootPath = buildContext.get(BuildContextField.SOURCE_ROOT);
        Path targetDir = buildContext.get(BuildContextField.TARGET_DIR);
        Path outputPath = targetDir.resolve(TARGET_API_DOC_DIRECTORY);
        buildContext.out().println();
        if (jsonPath != null) {
            buildContext.out().println("Generating API Documentation using data in JSON");
            BallerinaDocGenerator.writeAPIDocsForModulesFromJson(jsonPath, outputPath.toString(), excludeIndex);
            buildContext.out().println("\t" + sourceRootPath.relativize(outputPath).toString());
        } else if (combine) {
            buildContext.out().println("Combining Docs");
            BallerinaDocGenerator.mergeApiDocs(sourceRootPath.toString());
        } else {
            buildContext.out().println("Generating API Documentation");
            List<BLangPackage> modules = buildContext.getModules();
            try {
                // disable deprecated verbose logs from docerina
                BallerinaDocGenerator.setPrintStream(new EmptyPrintStream());
                Map<String, ModuleDoc> moduleDocMap = BallerinaDocGenerator
                        .generateModuleDocs(sourceRootPath.toString(), modules);
                Files.createDirectories(outputPath);
                if (toJson) {
                    BallerinaDocGenerator.writeAPIDocsToJSON(moduleDocMap, outputPath.toString());
                    buildContext.out().println("\t" + "data saved as a JSON in: " +
                            sourceRootPath.relativize(outputPath).toString());
                } else {
                    BallerinaDocGenerator.writeAPIDocsForModules(moduleDocMap, outputPath.toString(), excludeIndex);
                    buildContext.out().println("\t" + sourceRootPath.relativize(outputPath).toString());
                }
            } catch (IOException e) {
                throw createLauncherException("Unable to generate API Documentation.");
            }
        }
    }

    static class EmptyPrintStream extends PrintStream {
        EmptyPrintStream() throws UnsupportedEncodingException {
            super(new OutputStream() {
                @Override
                public void write(int b) {
                }
            }, true, "UTF-8");
        }
    }
}
