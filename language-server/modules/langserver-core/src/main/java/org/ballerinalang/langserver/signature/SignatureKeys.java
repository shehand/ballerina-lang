/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.langserver.signature;

import org.ballerinalang.langserver.commons.LSContext;
import org.eclipse.lsp4j.SignatureHelpCapabilities;

/**
 * Text Document Service context keys for the signature help operation context.
 * @since 0.95.6
 */
public class SignatureKeys {

    private SignatureKeys() {
    }

    public static final LSContext.Key<SignatureHelpCapabilities> SIGNATURE_HELP_CAPABILITIES_KEY
            = new LSContext.Key<>();
    public static final LSContext.Key<Integer> PARAMETER_INDEX = new LSContext.Key<>();
}
