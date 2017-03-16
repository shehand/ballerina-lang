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
package org.ballerinalang.plugins.idea.run.configuration.application;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.ballerinalang.plugins.idea.run.configuration.GoRunConfigurationProducerBase;
import org.ballerinalang.plugins.idea.run.configuration.GoRunUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoApplicationRunConfigurationProducer extends GoRunConfigurationProducerBase<GoApplicationConfiguration>
        implements Cloneable {

    public GoApplicationRunConfigurationProducer() {
        super(GoApplicationRunConfigurationType.getInstance());
    }

    @Override
    protected boolean setupConfigurationFromContext(@NotNull GoApplicationConfiguration configuration,
                                                    @NotNull ConfigurationContext context,
                                                    Ref<PsiElement> sourceElement) {
        //        PsiElement contextElement = GoRunUtil.getContextElement(context);
        //        if (contextElement != null && GoTestFinder.isTestFile(contextElement.getContainingFile())) {
        //            return false;
        //        }
        //        String importPath = getImportPathFromContext(contextElement);
        //        if (StringUtil.isNotEmpty(importPath)) {
        //            configuration.setModule(context.getModule());
        //            configuration.setRunKind(GoApplicationConfiguration.Kind.PACKAGE);
        //            configuration.setPackage(importPath);
        //            configuration.setName("Build " + importPath + " and run");
        //            return true;
        //        }
        if (super.setupConfigurationFromContext(configuration, context, sourceElement)) {
            configuration.setKind(GoApplicationConfiguration.Kind.FILE);
            return true;
        }
        return false;
    }

    @Nullable
    private static String getImportPathFromContext(@Nullable PsiElement contextElement) {
        //        if (GoRunUtil.isPackageContext(contextElement)) {
        //            PsiFile file = contextElement.getContainingFile();
        //            if (file instanceof BallerinaFile) {
        //                return ((BallerinaFile)file).getImportPath(false);
        //            }
        //        }
        //        else if (contextElement instanceof PsiDirectory) {
        //            return GoSdkUtil.getImportPath((PsiDirectory)contextElement, false);
        //        }
        return null;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull GoApplicationConfiguration configuration,
                                              ConfigurationContext context) {
        PsiElement contextElement = GoRunUtil.getContextElement(context);
        if (contextElement == null) return false;

        Module module = ModuleUtilCore.findModuleForPsiElement(contextElement);
        if (!Comparing.equal(module, configuration.getConfigurationModule().getModule())) return false;

        if (configuration.getKind() == GoApplicationConfiguration.Kind.PACKAGE) {
            return Comparing.equal(getImportPathFromContext(contextElement), configuration.getPackage());
        }

        return super.isConfigurationFromContext(configuration, context);
    }

    @NotNull
    @Override
    protected String getConfigurationName(@NotNull PsiFile file) {
        return "Build " + file.getName() + " and run";
    }
}
