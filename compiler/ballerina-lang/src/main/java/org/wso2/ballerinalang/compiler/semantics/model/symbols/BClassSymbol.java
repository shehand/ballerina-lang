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
package org.wso2.ballerinalang.compiler.semantics.model.symbols;

import org.ballerinalang.model.elements.PackageID;
import org.ballerinalang.model.symbols.SymbolOrigin;
import org.wso2.ballerinalang.compiler.semantics.model.types.BType;
import org.wso2.ballerinalang.compiler.util.Name;
import org.wso2.ballerinalang.compiler.util.Names;
import org.wso2.ballerinalang.compiler.util.diagnotic.DiagnosticPos;

import java.util.ArrayList;

/**
 * {@code BClassSymbol} represents a class symbol in a scope.
 *
 * @since 2.0
 */
public class BClassSymbol extends BObjectTypeSymbol {

    public BClassSymbol(int symTag, int flags, Name name, PackageID pkgID, BType type,
                        BSymbol owner, DiagnosticPos pos, SymbolOrigin origin) {
        super(symTag, flags, name, pkgID, type, owner, pos, origin);
        this.referencedFunctions = new ArrayList<>();
    }

    @Override
    public BClassSymbol createLabelSymbol() {
        BClassSymbol copy = Symbols.createClassSymbol(flags, Names.EMPTY, pkgID, type, owner, pos, origin);
        copy.attachedFuncs = attachedFuncs;
        copy.initializerFunc = initializerFunc;
        copy.isLabel = true;
        return copy;
    }
}
