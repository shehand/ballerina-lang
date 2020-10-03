/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.langserver.commons.completion;

import org.ballerinalang.langserver.commons.LSContext;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.InsertTextFormat;

/**
 * Abstract representation of the LSCompletionItem.
 *
 * @since 2.0.0
 */
public abstract class AbstractLSCompletionItem implements LSCompletionItem {

    protected CompletionItem completionItem;

    public AbstractLSCompletionItem(LSContext context, CompletionItem completionItem) {
        this.completionItem = completionItem;
        this.setInsertTextFormat(context);
    }

    @Override
    public CompletionItem getCompletionItem() {
        return completionItem;
    }
    
    /**
     * Convert the Snippet to a plain text snippet by removing the place holders.
     *
     * @param snippet Snippet string to alter
     * @return {@link String}   Converted Snippet
     */
    private String getPlainTextSnippet(String snippet) {
        return snippet
                .replaceAll("\\$\\{\\d+:([^\\{^\\}]*)\\}", "$1")
                .replaceAll("(\\$\\{\\d+\\})", "");
    }

    private void setInsertTextFormat(LSContext context) {
        boolean isSnippetSupported = context.get(CompletionKeys.CLIENT_CAPABILITIES_KEY).getCompletionItem()
                .getSnippetSupport();
        if (!isSnippetSupported) {
            this.completionItem.setInsertText(this.getPlainTextSnippet(this.completionItem.getInsertText()));
            this.completionItem.setInsertTextFormat(InsertTextFormat.PlainText);
        } else {
            this.completionItem.setInsertTextFormat(InsertTextFormat.Snippet);
        }
    }
}
