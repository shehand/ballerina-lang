/*
 * Copyright (c) 2019, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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
package org.ballerinalang.langserver.codeaction.builder.impl;

import org.ballerinalang.langserver.codeaction.builder.DiagBasedCodeAction;
import org.ballerinalang.langserver.command.executors.CreateFunctionExecutor;
import org.ballerinalang.langserver.common.constants.CommandConstants;
import org.ballerinalang.langserver.common.utils.CommonUtil;
import org.ballerinalang.langserver.commons.LSContext;
import org.ballerinalang.langserver.commons.codeaction.LSCodeActionProviderException;
import org.ballerinalang.langserver.commons.command.CommandArgument;
import org.ballerinalang.langserver.commons.workspace.LSDocumentIdentifier;
import org.ballerinalang.langserver.commons.workspace.WorkspaceDocumentException;
import org.ballerinalang.langserver.commons.workspace.WorkspaceDocumentManager;
import org.ballerinalang.langserver.compiler.DocumentServiceKeys;
import org.ballerinalang.langserver.compiler.common.LSDocumentIdentifierImpl;
import org.ballerinalang.langserver.compiler.exception.CompilationFailedException;
import org.ballerinalang.langserver.util.references.ReferencesKeys;
import org.ballerinalang.langserver.util.references.SymbolReferencesModel;
import org.ballerinalang.langserver.util.references.TokenOrSymbolNotFoundException;
import org.ballerinalang.model.elements.PackageID;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.wso2.ballerinalang.compiler.tree.BLangNode;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangInvocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import static org.ballerinalang.langserver.codeaction.providers.AbstractCodeActionProvider.getDiagnosedContent;
import static org.ballerinalang.langserver.codeaction.providers.AbstractCodeActionProvider.offsetPositionToInvocation;
import static org.ballerinalang.langserver.util.references.ReferencesUtil.getReferenceAtCursor;

/**
 * Code Action for creating undefined function.
 *
 * @since 1.2.0
 */
public class CreateFunctionCodeAction implements DiagBasedCodeAction {
    @Override
    public List<CodeAction> get(Diagnostic diagnostic, List<Diagnostic> allDiagnostics, LSContext context)
            throws LSCodeActionProviderException {
        String diagnosticMessage = diagnostic.getMessage();
        Position position = diagnostic.getRange().getStart();
        int line = position.getLine();
        int column = position.getCharacter();
        String uri = context.get(DocumentServiceKeys.FILE_URI_KEY);
        LSDocumentIdentifier document = new LSDocumentIdentifierImpl(uri);
        CommandArgument lineArg = new CommandArgument(CommandConstants.ARG_KEY_NODE_LINE, "" + line);
        CommandArgument colArg = new CommandArgument(CommandConstants.ARG_KEY_NODE_COLUMN, "" + column);
        CommandArgument uriArg = new CommandArgument(CommandConstants.ARG_KEY_DOC_URI, uri);
        List<Diagnostic> diagnostics = new ArrayList<>();

        List<Object> args = Arrays.asList(lineArg, colArg, uriArg);
        Matcher matcher = CommandConstants.UNDEFINED_FUNCTION_PATTERN.matcher(diagnosticMessage);
        String functionName = (matcher.find() && matcher.groupCount() > 0) ? matcher.group(1) + "(...)" : "";
        WorkspaceDocumentManager docManager = context.get(DocumentServiceKeys.DOC_MANAGER_KEY);
        String diagnosedContent = getDiagnosedContent(diagnostic, context, document);
        try {
            LSDocumentIdentifier lsDocument = docManager.getLSDocument(CommonUtil.getPathFromURI(uri).get());
            context.put(ReferencesKeys.OFFSET_CURSOR_N_TRY_NEXT_BEST, true);
            context.put(ReferencesKeys.DO_NOT_SKIP_NULL_SYMBOLS, true);
            Position afterAliasPos = offsetPositionToInvocation(diagnosedContent, position);
            // TODO: Try to use referenceAtCursor
            SymbolReferencesModel.Reference refAtCursor = getReferenceAtCursor(context, lsDocument, afterAliasPos);
            BLangNode bLangNode = refAtCursor.getbLangNode();
            BLangInvocation node = null;
            if (bLangNode instanceof BLangInvocation) {
                node = (BLangInvocation) bLangNode;
            }
            if (node != null && node.pkgAlias.value.isEmpty()) {
                boolean isWithinProject = (node.expr == null);
                if (node.expr != null) {
                    BLangPackage bLangPackage = context.get(DocumentServiceKeys.CURRENT_BLANG_PACKAGE_CONTEXT_KEY);
                    List<String> currentModules = document.getProjectModules();
                    PackageID nodePkgId = node.expr.type.tsymbol.pkgID;
                    isWithinProject = bLangPackage.packageID.orgName.equals(nodePkgId.orgName) &&
                            currentModules.contains(nodePkgId.name.value);
                }
                if (isWithinProject) {
                    String commandTitle = CommandConstants.CREATE_FUNCTION_TITLE + functionName;
                    CodeAction action = new CodeAction(commandTitle);
                    action.setKind(CodeActionKind.QuickFix);
                    action.setCommand(new Command(commandTitle, CreateFunctionExecutor.COMMAND, args));
                    action.setDiagnostics(diagnostics);
                    return Collections.singletonList(action);
                }
            }
        } catch (CompilationFailedException | WorkspaceDocumentException | TokenOrSymbolNotFoundException e) {
            // ignore
        }
        return new ArrayList<>();
    }
}
