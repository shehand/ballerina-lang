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
package org.ballerinalang.langserver.completions.providers.context;

import io.ballerinalang.compiler.syntax.tree.NonTerminalNode;
import io.ballerinalang.compiler.syntax.tree.QualifiedNameReferenceNode;
import io.ballerinalang.compiler.syntax.tree.UnionTypeDescriptorNode;
import org.ballerinalang.annotation.JavaSPIService;
import org.ballerinalang.langserver.common.utils.QNameReferenceUtil;
import org.ballerinalang.langserver.commons.LSContext;
import org.ballerinalang.langserver.commons.completion.CompletionKeys;
import org.ballerinalang.langserver.commons.completion.LSCompletionException;
import org.ballerinalang.langserver.commons.completion.LSCompletionItem;
import org.ballerinalang.langserver.completions.providers.AbstractCompletionProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Completion Provider for {@link UnionTypeDescriptorNode} context.
 *
 * @since 2.0.0
 */
@JavaSPIService("org.ballerinalang.langserver.commons.completion.spi.CompletionProvider")
public class UnionTypeDescriptorNodeContext extends AbstractCompletionProvider<UnionTypeDescriptorNode> {

    public UnionTypeDescriptorNodeContext() {
        super(UnionTypeDescriptorNode.class);
    }

    @Override
    public List<LSCompletionItem> getCompletions(LSContext context, UnionTypeDescriptorNode node)
            throws LSCompletionException {
        NonTerminalNode nodeAtCursor = context.get(CompletionKeys.NODE_AT_CURSOR_KEY);

        if (this.onQualifiedNameIdentifier(context, nodeAtCursor)) {
            QualifiedNameReferenceNode refNode = ((QualifiedNameReferenceNode) nodeAtCursor);
            return this.getCompletionItemList(QNameReferenceUtil.getTypesInModule(context, refNode), context);
        }

        List<LSCompletionItem> completionItems = new ArrayList<>(this.getModuleCompletionItems(context));
        completionItems.addAll(this.getTypeItems(context));

        return completionItems;
    }
}
