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
package org.ballerinalang.langserver.codeaction.providers.openapi.openapitoballerina;

import org.ballerinalang.annotation.JavaSPIService;
import org.ballerinalang.langserver.codeaction.providers.AbstractCodeActionProvider;
import org.ballerinalang.langserver.command.executors.openapi.openapitoballerina.AddMissingParameterInBallerinaExecutor;
import org.ballerinalang.langserver.common.constants.CommandConstants;
import org.ballerinalang.langserver.commons.LSContext;
import org.ballerinalang.langserver.commons.codeaction.CodeActionNodeType;
import org.ballerinalang.langserver.commons.command.CommandArgument;
import org.ballerinalang.langserver.compiler.DocumentServiceKeys;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import static org.ballerinalang.langserver.common.constants.CommandConstants.ADD_MISSING_PARAMETER_IN_BALLERINA;

/**
 * Code Action provider for add missing parameter in ballerina file for open API contract.
 *
 * @since 1.2.0
 */
@JavaSPIService("org.ballerinalang.langserver.commons.codeaction.spi.LSCodeActionProvider")
public class AddMissingParameterCodeAction extends AbstractCodeActionProvider {

    @Override
    public List<CodeAction> getNodeBasedCodeActions(CodeActionNodeType nodeType, LSContext lsContext,
                                                    List<Diagnostic> allDiagnostics) {
        return null;
    }

    @Override
    public List<CodeAction> getDiagBasedCodeActions(CodeActionNodeType nodeType, LSContext lsContext,
                                                    List<Diagnostic> diagnosticsOfRange,
                                                    List<Diagnostic> allDiagnostics) {
        List<CodeAction> actions = new ArrayList<>();
        for (Diagnostic diagnostic : diagnosticsOfRange) {
            Matcher matcher = CommandConstants.PARAMETER_FOR_THE_METHOD_NOT_FOUND_IN_BALLERINA.matcher(
                    diagnostic.getMessage());
            if (matcher.find()) {
                CodeAction codeAction = getCommand(diagnostic, lsContext);
                if (codeAction != null) {
                    actions.add(codeAction);
                }
            }
        }
        return actions;
    }

    private static CodeAction getCommand(Diagnostic diagnostic,
                                         LSContext lsContext) {
        String diagnosticMessage = diagnostic.getMessage();
        Position position = diagnostic.getRange().getStart();
        int line = position.getLine();
        int column = position.getCharacter();
        String uri = lsContext.get(DocumentServiceKeys.FILE_URI_KEY);
        CommandArgument lineArg = new CommandArgument(CommandConstants.ARG_KEY_NODE_LINE, "" + line);
        CommandArgument colArg = new CommandArgument(CommandConstants.ARG_KEY_NODE_COLUMN, "" + column);
        CommandArgument uriArg = new CommandArgument(CommandConstants.ARG_KEY_DOC_URI, uri);
        List<Diagnostic> diagnostics = new ArrayList<>();

        Matcher matcher = CommandConstants.PARAMETER_FOR_THE_METHOD_NOT_FOUND_IN_BALLERINA.matcher(diagnosticMessage);
        if (matcher.find() && matcher.groupCount() > 1) {
            String parameter = matcher.group(1);
            String method = matcher.group(2);
            String path = matcher.group(3);
            String commandTitle = String.format(CommandConstants.ADD_MISSING_PARAMETER_IN_BALLERINA, parameter, method,
                                                path);
            CommandArgument parameterArg = new CommandArgument(CommandConstants.ARG_KEY_PARAMETER, parameter);
            CommandArgument methodArg = new CommandArgument(CommandConstants.ARG_KEY_METHOD, method);
            CommandArgument pathArg = new CommandArgument(CommandConstants.ARG_KEY_PATH, path);

            List<Object> args = Arrays.asList(lineArg, colArg, uriArg, parameterArg, methodArg, pathArg);
            CodeAction action = new CodeAction(commandTitle);
            action.setKind(CodeActionKind.QuickFix);
            action.setCommand(
                    new Command(ADD_MISSING_PARAMETER_IN_BALLERINA, AddMissingParameterInBallerinaExecutor.COMMAND,
                                args));
            action.setDiagnostics(diagnostics);
            return action;
        }
        return null;
    }

}
