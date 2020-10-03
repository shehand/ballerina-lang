/*
 * Copyright (c) 2018, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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
package org.wso2.ballerinalang.compiler;

import org.ballerinalang.compiler.CompilerOptionName;
import org.ballerinalang.model.elements.PackageID;
import org.ballerinalang.project.InvalidModuleException;
import org.ballerinalang.project.Project;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ballerinalang.compiler.util.CompilerContext;
import org.wso2.ballerinalang.compiler.util.CompilerOptions;
import org.wso2.ballerinalang.compiler.util.Names;
import org.wso2.ballerinalang.programfile.ProgramFileConstants;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Tests for the {@link SourceDirectoryManager} class.
 *
 * @since 0.982.0
 */
public class SourceDirectoryManagerTest {

    private SourceDirectoryManager directoryManager;
    private CompilerContext context = new CompilerContext();

    @BeforeClass
    public void init() {
        context = new CompilerContext();
        CompilerOptions options = CompilerOptions.getInstance(context);
        options.put(CompilerOptionName.PROJECT_DIR, "src/test/resources/src/ballerina/multi-package");
        directoryManager = SourceDirectoryManager.getInstance(context);
    }

    @Test(description = "Return modules list should be equivalence to the expected module list")
    public void testListSourceFilesAndPackages() {
        Names names = Names.getInstance(context);
        List<PackageID> expectedPackageIds = new ArrayList<>();
        expectedPackageIds.add(new PackageID(names.fromString("abc"),
                                             names.fromString("fruits"),
                                             names.fromString("0.0.1"))
        );

        Stream<PackageID> packageIDStream = directoryManager.listSourceFilesAndPackages();
        Assert.assertTrue(packageIDStream.allMatch(expectedPackageIds::remove) && expectedPackageIds.isEmpty());
    }

    @Test(description = "Return module should be equivalence to the expected module")
    public void testGetPackageID() {
        PackageID fruits = directoryManager.getPackageID("fruits");
        Names names = Names.getInstance(context);
        PackageID expectedPackageId = new PackageID(names.fromString("abc"),
                                                    names.fromString("fruits"),
                                                    names.fromString("0.0.1"));
        Assert.assertEquals(fruits, expectedPackageId);
    }

    @Test(description = "Test if module exist in the project")
    public void testIsModuleExists() {
        Project project = context.get(Project.PROJECT_KEY);
        Names names = Names.getInstance(context);
        PackageID expectedPackageId = new PackageID(names.fromString("abc"),
                names.fromString("fruits"),
                names.fromString("0.0.1"));
        Assert.assertTrue(project.isModuleExists(expectedPackageId));
        PackageID invalidName = new PackageID(names.fromString("abc"),
                names.fromString("invalid"),
                names.fromString("0.0.1"));
        Assert.assertFalse(project.isModuleExists(invalidName));
        PackageID invalidOrg = new PackageID(names.fromString("invalid"),
                names.fromString("fruits"),
                names.fromString("0.0.1"));
        Assert.assertFalse(project.isModuleExists(invalidOrg));
        PackageID invalidVersion = new PackageID(names.fromString("abc"),
                names.fromString("fruits"),
                names.fromString("0.1.1"));
        Assert.assertFalse(project.isModuleExists(invalidVersion));
    }

    @Test(description = "Test if get balo path")
    public void getBaloPath() throws InvalidModuleException {
        Project project = context.get(Project.PROJECT_KEY);
        Names names = Names.getInstance(context);
        PackageID moduleId = new PackageID(names.fromString("abc"),
                names.fromString("fruits"),
                names.fromString("0.0.1"));
        // valid module
        String baloName =  "fruits-"
                + ProgramFileConstants.IMPLEMENTATION_VERSION + "-any-0.0.1.balo";
        Path baloPath = directoryManager.getSourceDirectory().getPath()
                .resolve("target").resolve("balo").resolve(baloName);
        Assert.assertEquals(project.getBaloPath(moduleId), baloPath);
        // valid module with a diffrent platform

        // invalid module
        try {
            PackageID invalidModuleId = new PackageID(names.fromString("invalid"),
                    names.fromString("fruits"),
                    names.fromString("0.0.1"));
            // invalid valid module
            project.getBaloPath(invalidModuleId);
            throw new AssertionError("Failed to identify invalid module");
        } catch (InvalidModuleException e) {
            // catch works
        }
    }
}
