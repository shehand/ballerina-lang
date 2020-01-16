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

import org.ballerinalang.config.ConfigRegistry;
import org.ballerinalang.model.elements.PackageID;
import org.ballerinalang.packerina.buildcontext.BuildContext;
import org.ballerinalang.packerina.buildcontext.BuildContextField;
import org.ballerinalang.util.BootstrapRunner;
import org.wso2.ballerinalang.compiler.PackageCache;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BPackageSymbol;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;
import org.wso2.ballerinalang.compiler.util.CompilerContext;
import org.wso2.ballerinalang.compiler.util.ProjectDirs;
import org.wso2.ballerinalang.util.RepoUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

import static org.wso2.ballerinalang.compiler.util.ProjectDirConstants.BLANG_COMPILED_JAR_EXT;

/**
 * Task for creating jar file.
 */
public class CreateJarTask implements Task {

    private boolean dumpBir;
    private boolean buildNative;
    private boolean dumpLlvmIr;
    private boolean noOptimizeLlvm;

    private boolean skipCopyLibsFromDist = false;

    public CreateJarTask(boolean dumpBir, boolean buildNative, boolean dumpLlvmIr, boolean noOptimizeLlvm) {
        this.dumpBir = dumpBir;
        this.buildNative = buildNative;
        this.dumpLlvmIr = dumpLlvmIr;
        this.noOptimizeLlvm = noOptimizeLlvm;
    }

    public CreateJarTask(boolean dumpBir, boolean skipCopyLibsFromDist, boolean buildNative, boolean dumpLlvmIr,
            boolean noOptimizeLlvm) {
        this.dumpBir = dumpBir;
        this.skipCopyLibsFromDist = skipCopyLibsFromDist;
        this.buildNative = buildNative;
        this.dumpLlvmIr = dumpLlvmIr;
        this.noOptimizeLlvm = noOptimizeLlvm;
    }

    @Override
    public void execute(BuildContext buildContext) {
        // This will avoid initializing Config registry during jar creation.
        ConfigRegistry.getInstance().setInitialized(true);
        Path sourceRoot = buildContext.get(BuildContextField.SOURCE_ROOT);
        Path projectBIRCache = buildContext.get(BuildContextField.BIR_CACHE_DIR);
        Path homeBIRCache = buildContext.getBirCacheFromHome();
        Path systemBIRCache = buildContext.getSystemRepoBirCache();
        Path runtimeJar = getRuntimeAllJar(buildContext);
        Path targetDir = buildContext.get(BuildContextField.TARGET_DIR);

        CompilerContext context = buildContext.get(BuildContextField.COMPILER_CONTEXT);
        PackageCache packageCache = PackageCache.getInstance(context);

        List<BLangPackage> moduleBirMap = buildContext.getModules();
        for (BLangPackage module : moduleBirMap) {

            BLangPackage bLangPackage = packageCache.get(module.packageID);
            if (bLangPackage == null) {
                continue;
            }

            PackageID packageID = bLangPackage.packageID;

            HashSet<Path> moduleDependencySet = buildContext.moduleDependencyPathMap.get(packageID).platformLibs;
            if (!skipCopyLibsFromDist) {
                moduleDependencySet.add(runtimeJar);
            }
            // write module child imports jars
            writeImportJar(bLangPackage.symbol.imports, sourceRoot, buildContext, runtimeJar,
                    projectBIRCache.toString(), homeBIRCache.toString(), systemBIRCache.toString());

            // get the bir path of the module
            Path entryBir = buildContext.getBirPathFromTargetCache(module.packageID);

            // get the jar path of the module.
            Path jarOutput = buildContext.getJarPathFromTargetCache(module.packageID);

            if (!Files.exists(jarOutput)) {
                if (buildNative) {
                    BootstrapRunner.genNativeCode(entryBir.toString(), targetDir, this.dumpLlvmIr, this.noOptimizeLlvm);
                } else {
                    BootstrapRunner.loadTargetAndGenerateJarBinary(entryBir.toString(), jarOutput.toString(),
                            this.dumpBir, moduleDependencySet, projectBIRCache.toString(), homeBIRCache.toString(),
                            systemBIRCache.toString());
                }
            }

            // If there is a testable package we will create testable jar.
            if (!buildContext.skipTests() && bLangPackage.hasTestablePackage()) {
                for (BLangPackage testPkg : bLangPackage.getTestablePkgs()) {
                    // write its child imports jar file to cache
                    writeImportJar(testPkg.symbol.imports, sourceRoot, buildContext, runtimeJar,
                            projectBIRCache.toString(), homeBIRCache.toString(), systemBIRCache.toString());

                    // get the bir path of the module
                    Path testBir = buildContext.getTestBirPathFromTargetCache(testPkg.packageID);

                    // get the jar path of the module.
                    Path testJarOutput = buildContext.getTestJarPathFromTargetCache(testPkg.packageID);
                    if (!Files.exists(testJarOutput)) {
                        BootstrapRunner.loadTargetAndGenerateJarBinary(testBir.toString(), testJarOutput.toString(),
                                this.dumpBir, moduleDependencySet, projectBIRCache.toString(), homeBIRCache.toString(),
                                systemBIRCache.toString());
                    }
                }
            }
        }
        ConfigRegistry.getInstance().setInitialized(false);
    }

    private void writeImportJar(List<BPackageSymbol> imports, Path sourceRoot, BuildContext buildContext,
                                Path runtimeJar, String... reps) {
        for (BPackageSymbol bimport : imports) {
            PackageID id = bimport.pkgID;
            if (id.orgName.value.equals("ballerina") || id.orgName.value.equals("ballerinax")) {
                continue;
            }
            Path jarFilePath;
            Path birFilePath;
            // If the module is part of the project write it to project jar cache check if file exist
            // If not write it to home jar cache
            // skip ballerina and ballerinax
            if (ProjectDirs.isModuleExist(sourceRoot, id.name.value) ||
                    buildContext.getImportPathDependency(id).isPresent()) {
                jarFilePath = buildContext.getJarPathFromTargetCache(id);
                birFilePath = buildContext.getBirPathFromTargetCache(id);
            } else {
                jarFilePath = buildContext.getJarPathFromHomeCache(id);
                birFilePath = buildContext.getBirPathFromHomeCache(id);
            }
            if (!Files.exists(jarFilePath) && buildContext.moduleDependencyPathMap.containsKey(id)) {
                HashSet<Path> moduleDependencySet = buildContext.moduleDependencyPathMap.get(id).platformLibs;
                if (!skipCopyLibsFromDist) {
                    moduleDependencySet.add(runtimeJar);
                }
                BootstrapRunner.loadTargetAndGenerateJarBinary(birFilePath.toString(), jarFilePath.toString(),
                        this.dumpBir, moduleDependencySet, reps);
            }
            writeImportJar(bimport.imports, sourceRoot, buildContext, runtimeJar, reps);
        }
    }

    private Path getRuntimeAllJar(BuildContext buildContext) {

        if (skipCopyLibsFromDist) {
            return null;
        }
        String balHomePath = buildContext.get(BuildContextField.HOME_REPO).toString();
        String ballerinaVersion = RepoUtils.getBallerinaVersion();
        String runtimeJarName = "ballerina-rt-" + ballerinaVersion + BLANG_COMPILED_JAR_EXT;
        return Paths.get(balHomePath, "bre", "lib", runtimeJarName);
    }
}
