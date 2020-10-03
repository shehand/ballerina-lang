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
package org.ballerinalang.langserver.command.executors.openapi;

import org.ballerinalang.langserver.commons.LSContext;
import org.ballerinalang.langserver.commons.workspace.LSDocumentIdentifier;
import org.ballerinalang.langserver.commons.workspace.WorkspaceDocumentException;
import org.ballerinalang.langserver.compiler.DocumentServiceKeys;
import org.ballerinalang.langserver.compiler.exception.CompilationFailedException;
import org.ballerinalang.langserver.util.TokensUtil;
import org.ballerinalang.langserver.util.references.ReferencesUtil;
import org.ballerinalang.langserver.util.references.TokenOrSymbolNotFoundException;
import org.ballerinalang.model.tree.TopLevelNode;
import org.ballerinalang.model.tree.expressions.RecordLiteralNode;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentPositionParams;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotationAttachment;
import org.wso2.ballerinalang.compiler.tree.BLangFunction;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;
import org.wso2.ballerinalang.compiler.tree.BLangService;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangRecordLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangSimpleVarRef;
import org.wso2.ballerinalang.compiler.util.diagnotic.DiagnosticPos;

import java.util.List;

/**
 * Util class for open api code action executors.
 *
 * @since 1.2.0
 */
public class OpenApiCodeActionUtil {

    /**
     * Get the symbol at the Cursor.
     *
     * @param context  LS Operation Context
     * @param document LS Document
     * @param position Cursor Position
     * @return Symbol reference at cursor
     * @throws WorkspaceDocumentException when couldn't find file for uri
     * @throws CompilationFailedException when compilation failed
     */
    public static List<BLangPackage> getBLangPkg(LSContext context, LSDocumentIdentifier document,
                                                 Position position)
            throws WorkspaceDocumentException, CompilationFailedException, TokenOrSymbolNotFoundException {
        TextDocumentIdentifier textDocIdentifier = new TextDocumentIdentifier(document.getURIString());
        TextDocumentPositionParams pos = new TextDocumentPositionParams(textDocIdentifier, position);
        context.put(DocumentServiceKeys.POSITION_KEY, pos);
        context.put(DocumentServiceKeys.FILE_URI_KEY, document.getURIString());
        context.put(DocumentServiceKeys.COMPILE_FULL_PROJECT, true);
        TokensUtil.findTokenAtPosition(context, position);
        return ReferencesUtil.compileModules(context);
    }

    public static BLangFunction getBLangFunction(List<BLangPackage> packages, Position position) {
        for (BLangPackage aPackage : packages) {
            for (TopLevelNode topLevelNode : aPackage.topLevelNodes) {
                if (topLevelNode instanceof BLangService) {
                    for (BLangFunction resourceFunction : ((BLangService) topLevelNode).resourceFunctions) {
                        for (BLangAnnotationAttachment annAttachment : resourceFunction.annAttachments) {
                            for (RecordLiteralNode.RecordField field :
                                    ((BLangRecordLiteral) annAttachment.expr).fields) {
                                DiagnosticPos fieldPosition =
                                        ((BLangSimpleVarRef) ((BLangRecordLiteral.BLangRecordKeyValueField) field)
                                                .key.expr).pos;
                                if (fieldPosition.sLine == position.getLine() + 1 &&
                                        fieldPosition.sCol == position.getCharacter()) {
                                    return resourceFunction;
                                }

                            }
                        }
                    }
                }
            }
        }
        return null;
    }

}
