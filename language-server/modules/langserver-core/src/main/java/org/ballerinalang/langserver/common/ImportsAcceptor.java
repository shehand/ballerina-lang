/*
 * Copyright (c) 2020, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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
package org.ballerinalang.langserver.common;

import org.ballerinalang.langserver.common.utils.CommonUtil;
import org.ballerinalang.langserver.commons.LSContext;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.wso2.ballerinalang.compiler.tree.BLangImportPackage;
import org.wso2.ballerinalang.compiler.util.diagnotic.DiagnosticPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * This class provides imports acceptor and its functionalities.
 *
 * @since 1.3.0
 */
public class ImportsAcceptor {
    private final Set<String> newImports;
    private final List<BLangImportPackage> currentModuleImports;
    private final BiConsumer<String, String> onExistCallback;

    public ImportsAcceptor(LSContext context) {
        this(context, null);
    }

    public ImportsAcceptor(LSContext context, BiConsumer<String, String> onExistCallback) {
        this.newImports = new HashSet<>();
        this.currentModuleImports = CommonUtil.getCurrentModuleImports(context);
        this.onExistCallback = onExistCallback;
    }

    /**
     * Return imports acceptor.
     *
     * @return Returns imports acceptor
     */
    public BiConsumer<String, String> getAcceptor() {
        return (orgName, alias) -> {
            boolean notFound = currentModuleImports.stream().noneMatch(
                    pkg -> {
                        String importAlias = pkg.symbol.pkgID.name.value;
                        String escapedName = importAlias.replace(".", ".'");
                        boolean aliasMatched = importAlias.equals(alias) || escapedName.equals(alias);
                        return pkg.symbol.pkgID.orgName.value.equals(orgName) && aliasMatched;
                    }
            );
            if (notFound) {
                String pkgName = orgName + "/" + alias;
                newImports.add(pkgName);
                if (onExistCallback != null) {
                    onExistCallback.accept(orgName, alias);
                }
            }
        };
    }

    /**
     * Return new import text edits.
     *
     * @return a list of {@link TextEdit}
     */
    public List<TextEdit> getNewImportTextEdits() {
        List<TextEdit> edits = new ArrayList<>();
        newImports.forEach(i -> {
            edits.add(createImportTextEdit(i));
        });
        return edits;
    }

    /**
     * Return set of new imports.
     *
     * @return a set of  {@link String}
     */
    public Set<String> getNewImports() {
        return newImports;
    }

    private TextEdit createImportTextEdit(String pkgName) {
        DiagnosticPos pos = null;

        if (!currentModuleImports.isEmpty()) {
            BLangImportPackage lastImport = CommonUtil.getLastItem(currentModuleImports);
            pos = lastImport.getPosition();
        }

        int endCol = 0;
        int endLine = pos == null ? 0 : pos.getEndLine();

        String editText = "import " + pkgName + ";\n";
        Range range = new Range(new Position(endLine, endCol), new Position(endLine, endCol));
        return new TextEdit(range, editText);
    }
}
