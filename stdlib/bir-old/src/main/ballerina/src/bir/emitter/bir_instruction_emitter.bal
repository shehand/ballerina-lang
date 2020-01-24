// Copyright (c) 2019 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

function emitInstructions(Instruction?[] instructions, int tabs) returns string {
    string insStr = "";
    foreach Instruction? ins in instructions {
    	if ins is Instruction {
            insStr += emitInstruction(ins, tabs);
            insStr += emitLBreaks(1);
	}
    }
    return insStr;
}

function emitInstruction(Instruction ins, int tabs) returns string {
    if ins is ConstantLoad {
        return emitInsConstantLoad(ins, tabs); 
    } else if ins is NewMap {
        return emitInsNewMap(ins, tabs); 
    } else if ins is NewTable {
        return emitInsNewTable(ins, tabs); 
    } else if ins is NewInstance {
        return emitInsNewInstance(ins, tabs); 
    } else if ins is NewArray {
        return emitInsNewArray(ins, tabs); 
    } else if ins is NewError {
        return emitInsNewError(ins, tabs); 
    } else if ins is FPLoad {
        return emitInsFPLoad(ins, tabs); 
    } else if ins is FieldAccess {
        return emitInsFieldAccess(ins, tabs); 
    } else if ins is TypeCast {
        return emitInsTypeCast(ins, tabs); 
    } else if ins is IsLike {
        return emitInsIsLike(ins, tabs); 
    } else if ins is TypeTest {
        return emitInsTypeTest(ins, tabs); 
    } else if ins is Move {
        return emitInsMove(ins, tabs); 
    } else if ins is BinaryOp {
        return emitInsBinaryOp(ins, tabs); 
    } else if ins is NewXMLElement {
        return emitInsNewXMLElement(ins, tabs); 
    } else if ins is NewXMLQName {
        return emitInsNewXMLQName(ins, tabs); 
    } else if ins is NewStringXMLQName {
        return emitInsNewStringXMLQName(ins, tabs); 
    } else if ins is XMLAccess {
        return emitInsXMLAccess(ins, tabs); 
    } else if ins is NewXMLText {
        return emitInsNewXMLText(ins, tabs); 
    } else if ins is NewXMLComment {
        return emitInsNewXMLComment(ins, tabs); 
    } else if ins is NewXMLPI {
        return emitInsNewXMLPI(ins, tabs); 
    } else if ins is UnaryOp {
        return emitInsUnaryOp(ins, tabs); 
    } else if ins is NewTypeDesc {
        return emitInsNewTypeDesc(ins, tabs); 
    } else {
        return emitPlatformInstruction(ins, tabs);
    }
}

function emitInsConstantLoad(ConstantLoad ins, int tabs) returns string {
    string cnstLStr = "";
    cnstLStr += emitTabs(tabs);
    cnstLStr += emitVarRef(ins.lhsOp); 
    cnstLStr += emitSpaces(1);
    cnstLStr += "=";
    cnstLStr += emitSpaces(1);
    cnstLStr += "ConstLoad";
    cnstLStr += emitSpaces(1);
    cnstLStr += emitValue(ins.value); 
    cnstLStr += ";";
    return cnstLStr;
}

function emitInsNewMap(NewMap ins, int tabs) returns string {
    string nMapStr = "";
    nMapStr += emitTabs(tabs);
    nMapStr += emitVarRef(ins.lhsOp); 
    nMapStr += emitSpaces(1);
    nMapStr += "=";
    nMapStr += emitSpaces(1);
    nMapStr += "NewMap";
    nMapStr += emitSpaces(1);
    nMapStr += emitTypeRef(ins.bType); 
    nMapStr += ";";
    return nMapStr;
}


function emitInsNewTable(NewTable ins, int tabs) returns string {
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(ins.lhsOp); 
    str += emitSpaces(1);
    str += "=";
    str += emitSpaces(1);
    str += "table(";
    str += emitVarRef(ins.columnsOp); 
    str += ",";
    str += emitSpaces(1);
    str += emitVarRef(ins.dataOp); 
    str += ",";
    str += emitSpaces(1);
    str += emitVarRef(ins.keyColOp); 
    str += ")<";
    str += emitTypeRef(ins.typeValue); 
    str += ">;";
    return str;
}
 
function emitInsNewInstance(NewInstance ins, int tabs) returns string {
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(ins.lhsOp);
    str += emitSpaces(1);
    str += "=";
    str += emitSpaces(1);
    str += "new";
    str += emitSpaces(1);
    TypeDef | TypeRef typeDefRef = ins.typeDefRef;
    if typeDefRef is TypeDef {
        str += emitName(typeDefRef.name);
    } else {
        str += emitModuleID(typeDefRef.externalPkg);
        str += ":";
        str += emitName(typeDefRef.name);
    }
    str += ";";
    return str;
}
 
function emitInsNewArray(NewArray ins, int tabs) returns string {
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(ins.lhsOp);
    str += emitSpaces(1);
    str += "=";
    str += emitSpaces(1);
    str += "newArray";
    str += emitSpaces(1);
    str += emitTypeRef(ins.typeValue);
    str += "[";
    str += emitVarRef(ins.sizeOp);
    str += "]";
    str += ";";
    return str;
}
 
function emitInsNewError(NewError ins, int tabs) returns string {
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(ins.lhsOp);
    str += emitSpaces(1);
    str += "=";
    str += emitSpaces(1);
    str += "error";
    str += emitSpaces(1);
    str += emitTypeRef(ins.typeValue);
    str += "(";
    str += emitVarRef(ins.reasonOp);
    str += ",";
    str += emitSpaces(1);
    str += emitVarRef(ins.detailsOp);
    str += ")";
    str += ";";
    return str;
}
 
function emitInsFPLoad(FPLoad ins, int tabs) returns string {
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(ins.lhsOp);
    str += emitSpaces(1);
    str += "=";
    str += emitSpaces(1);
    str += "fp";
    str += emitSpaces(1);
    str += emitName(ins.name);
    // TODO add params and closure maps
    str += ";";
    return str;
}
 
function emitInsFieldAccess(FieldAccess ins, int tabs) returns string {
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(ins.lhsOp);
    if ins.kind is INS_KIND_MAP_LOAD | INS_KIND_ARRAY_LOAD {
        str += emitSpaces(1);
        str += "=";
        str += emitSpaces(1);
        str += emitVarRef(ins.rhsOp);
        str += "[";
        str += emitVarRef(ins.keyOp);
        str += "]";
    } else if ins.kind is INS_KIND_MAP_STORE | INS_KIND_ARRAY_STORE {
        str += "[";
        str += emitVarRef(ins.keyOp);
        str += "]";
        str += emitSpaces(1);
        str += "=";
        str += emitSpaces(1);
        str += emitVarRef(ins.rhsOp);
    }
    str += ";";
    return str;
}
 
function emitInsTypeCast(TypeCast ins, int tabs) returns string {
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(ins.lhsOp);
    str += emitSpaces(1);
    str += "=";
    str += emitSpaces(1);
    str += "<";
    str += emitTypeRef(ins.castType);
    str += ">";
    str += emitSpaces(1);
    str += emitVarRef(ins.rhsOp);
    str += ";";
    return str;
}
 
function emitInsIsLike(IsLike ins, int tabs) returns string {
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(ins.lhsOp);
    str += emitSpaces(1);
    str += "=";
    str += emitSpaces(1);
    str += emitVarRef(ins.rhsOp);
    str += emitSpaces(1);
    str += "isLike";
    str += emitSpaces(1);
    str += emitTypeRef(ins.typeVal);
    str += ";";
    return str;
}
 
function emitInsTypeTest(TypeTest ins, int tabs) returns string {
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(ins.lhsOp);
    str += emitSpaces(1);
    str += "=";
    str += emitSpaces(1);
    str += emitVarRef(ins.rhsOp);
    str += emitSpaces(1);
    str += "is";
    str += emitSpaces(1);
    str += emitTypeRef(ins.typeValue);
    str += ";";
    return str;
}
 
function emitInsMove(Move ins, int tabs) returns string {
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(ins.lhsOp);
    str += emitSpaces(1);
    str += "=";
    str += emitSpaces(1);
    str += emitVarRef(ins.rhsOp);
    str += ";";
    return str;
}
 
function emitInsBinaryOp(BinaryOp ins, int tabs) returns string {
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(ins.lhsOp);
    str += emitSpaces(1);
    str += "=";
    str += emitSpaces(1);
    str += emitVarRef(ins.rhsOp1);
    str += emitSpaces(1);
    str += emitBinaryOpInstructionKind(<BinaryOpInstructionKind> ins.kind);
    str += emitSpaces(1);
    str += emitVarRef(ins.rhsOp2);
    str += ";";
    return str;
}
 
function emitInsNewXMLElement(NewXMLElement ins, int tabs) returns string {
    // TODO fill this 
    return "";
}
 
function emitInsNewXMLQName(NewXMLQName ins, int tabs) returns string {
    // TODO fill this 
    return "";
}
 
function emitInsNewStringXMLQName(NewStringXMLQName ins, int tabs) returns string {
    // TODO fill this 
    return "";
}
 
function emitInsXMLAccess(XMLAccess ins, int tabs) returns string {
    // TODO fill this 
    return "";
}
 
function emitInsNewXMLText(NewXMLText ins, int tabs) returns string {
    // TODO fill this 
    return "";
}
 
function emitInsNewXMLComment(NewXMLComment ins, int tabs) returns string {
    // TODO fill this 
    return "";
}
 
function emitInsNewXMLPI(NewXMLPI ins, int tabs) returns string {
    // TODO fill this 
    return "";
}
 
function emitInsUnaryOp(UnaryOp ins, int tabs) returns string {
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(ins.lhsOp);
    str += emitSpaces(1);
    str += "=";
    str += emitSpaces(1);
    // TODO emit unary op kind
    str += emitVarRef(ins.rhsOp);
    str += ";";
    return str;
}
 
function emitInsNewTypeDesc(NewTypeDesc ins, int tabs) returns string {
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(ins.lhsOp);
    str += emitSpaces(1);
    str += "=";
    str += emitSpaces(1);
    str += "newType";
    str += emitSpaces(1);
    str += emitTypeRef(ins.typeValue);
    str += ";";
    return str;
}
 
/////////////// Emit Terminator instructions ////////////////////
function emitTerminator(Terminator term, int tabs) returns string {
    if term is Wait {
        return emitWait(term, tabs);
    } else if term is Flush {
        return emitFlush(term, tabs);
    } else if term is WorkerReceive {
        return emitWorkerReceive(term, tabs);
    } else if term is WorkerSend {
        return emitWorkerSend(term, tabs);
    } else if term is Call {
        return emitCall(term, tabs);
    } else if term is AsyncCall {
        return emitAsyncCall(term, tabs);
    } else if term is Branch {
        return emitBranch(term, tabs);
    } else if term is GOTO {
        return emitGOTO(term, tabs);
    } else if term is Lock {
        return emitLock(term, tabs);
    } else if term is FieldLock {
        return emitFieldLock(term, tabs);
    } else if term is Unlock {
        return emitUnlock(term, tabs);
    } else if term is Return {
        return emitReturn(term, tabs);
    } else if term is Panic {
        return emitPanic(term, tabs);
    } else if term is FPCall {
        return emitFPCall(term, tabs);
    } else if term is WaitAll {
        return emitWaitAll(term, tabs);
    } else {
        return emitPlatformTerminator(term, tabs);
    }
}

function emitWait(Wait term, int tabs) returns string { 
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(term.lhsOp);
    str += emitSpaces(1);
    str += "=";
    str += emitSpaces(1);
    str += "wait";
    str += emitSpaces(1);
    int i = 0;
    int argLength = term.exprList.length();
    while i < argLength {
        VarRef? ref = term.exprList[i];
        if ref is VarRef {
            str += emitVarRef(ref);
            i += 1;
            if i < argLength {
                str += emitSpaces(1);
                str += "|";
                str += emitSpaces(1);
            }
        }
    }
    str += emitSpaces(1);
    str += "->";
    str += emitSpaces(1);
    str += emitBasicBlockRef(term.thenBB);
    str += ";";
    return str;
}

function emitFlush(Flush term, int tabs) returns string { 
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(term.lhsOp);
    str += emitSpaces(1);
    str += "=";
    str += emitSpaces(1);
    str += "flush";
    str += emitSpaces(1);
    int i = 0;
    int argLength = term.workerChannels.length();
    while i < argLength {
        ChannelDetail ref = term.workerChannels[i];
        str += emitName(ref.name);
        i += 1;
        if i < argLength {
            str += ",";
            str += emitSpaces(1);
        }
    }
    str += emitSpaces(1);
    str += "->";
    str += emitSpaces(1);
    str += emitBasicBlockRef(term.thenBB);
    str += ";";
    return str;
}

function emitWorkerReceive(WorkerReceive term, int tabs) returns string { 
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(term.lhsOp);
    str += emitSpaces(1);
    str += "=";
    str += emitSpaces(1);
    str += "<=";
    str += emitSpaces(1);
    str += emitName(term.channelName);
    str += emitSpaces(1);
    str += "->";
    str += emitSpaces(1);
    str += emitBasicBlockRef(term.thenBB);
    str += ";";
    return str;
}

function emitWorkerSend(WorkerSend term, int tabs) returns string { 
    string str = "";
    str += emitTabs(tabs);
    VarRef? lhsOp = term.lhsOp;
    if lhsOp is VarRef {
        str += emitVarRef(lhsOp);
        str += emitSpaces(1);
        str += "=";
        str += emitSpaces(1);
    }
    str += emitVarRef(term.dataOp);
    str += emitSpaces(1);
    if term.isSync {
        str += "=>";
    } else {
        str += "=>>";
    }
    str += emitSpaces(1);
    str += emitName(term.channelName);
    str += emitSpaces(1);
    str += "->";
    str += emitSpaces(1);
    str += emitBasicBlockRef(term.thenBB);
    str += ";";
    return str;
}

function emitCall(Call term, int tabs) returns string { 
    string callStr = "";
    callStr += emitTabs(tabs);
    VarRef? lhsOp = term.lhsOp;
    if lhsOp is VarRef {
        callStr += emitVarRef(lhsOp);
        callStr += emitSpaces(1);
        callStr += "=";
        callStr += emitSpaces(1);
    }
    callStr += emitName(term.name);
    callStr += "(";
    int i = 0;
    int argLength = term.args.length();
    foreach VarRef? ref in term.args {
        if ref is VarRef {
            callStr += emitVarRef(ref);
            i += 1;
            if i < argLength {
                callStr += ",";
                callStr += emitSpaces(1);
            }
        }
    }
    callStr += ")";
    callStr += emitSpaces(1);
    callStr += "->";
    callStr += emitSpaces(1);
    callStr += emitBasicBlockRef(term.thenBB);
    callStr += ";";
    return callStr;
}

function emitAsyncCall(AsyncCall term, int tabs) returns string { 
    string str = "";
    str += emitTabs(tabs);
    VarRef? lhsOp = term.lhsOp;
    if lhsOp is VarRef {
        str += emitVarRef(lhsOp);
        str += emitSpaces(1);
        str += "=";
        str += emitSpaces(1);
    }
    str += "start";
    str += emitSpaces(1);
    str += emitName(term.name);
    str += "(";
    int i = 0;
    int argLength = term.args.length();
    foreach VarRef? ref in term.args {
        if ref is VarRef {
            str += emitVarRef(ref);
            i += 1;
            if i < argLength {
                str += ",";
                str += emitSpaces(1);
            }
        }
    }
    str += ")";
    str += emitSpaces(1);
    str += "->";
    str += emitSpaces(1);
    str += emitBasicBlockRef(term.thenBB);
    str += ";";
    return str;
}

function emitBranch(Branch term, int tabs) returns string { 
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(term.op);
    str += "?";
    str += emitSpaces(1);
    str += emitBasicBlockRef(term.trueBB);
    str += emitSpaces(1);
    str += ":";
    str += emitSpaces(1);
    str += emitBasicBlockRef(term.falseBB);
    str += ";";
    return str;
}

function emitGOTO(GOTO term, int tabs) returns string { 
    string retStr = "";
    retStr += emitTabs(tabs);
    retStr += "GOTO";
    retStr += emitSpaces(1);
    retStr += emitBasicBlockRef(term.targetBB);
    retStr += ";";
    return retStr;
}

function emitLock(Lock term, int tabs) returns string { 
    string str = "";
    str += emitTabs(tabs);
    str += "lock";
    str += emitSpaces(1);
    str += emitName(term.globleVar.name);
    str += emitSpaces(1);
    str += "->";
    str += emitSpaces(1);
    str += emitBasicBlockRef(term.lockBB);
    str += ";";
    return str;
}

function emitFieldLock(FieldLock term, int tabs) returns string { 
    string str = "";
    str += emitTabs(tabs);
    str += "lock";
    str += emitSpaces(1);
    str += emitName(term.localVar.name);
    str += "[\"";
    str += term.field;
    str += "\"]";
    str += emitSpaces(1);
    str += "->";
    str += emitSpaces(1);
    str += emitBasicBlockRef(term.lockBB);
    str += ";";
    return str;
}

function emitUnlock(Unlock term, int tabs) returns string { 
    string str = "";
    str += emitTabs(tabs);
    str += "unlock";
    str += emitSpaces(1);
    str += "(";
    int i = 0;
    int argLength = term.globleVars.length();
    foreach VariableDcl? ref in term.globleVars {
        if ref is VariableDcl {
            str += emitName(ref.name);
            i += 1;
            if i < argLength {
                str += ",";
                str += emitSpaces(1);
            }
        }
    }
    str += ")";
    str += emitSpaces(1);
    str += "(";
    i = 0;
    argLength = term.localLocks.length();
    foreach LocalLocks? ref in term.localLocks {
        if ref is LocalLocks {
            int j = 0;
            int length = ref.fields.length();
            foreach string f in ref.fields {
                str += emitName(ref.localVar.name);
                str += "[\"";
                str += f;
                str += "\"]";
                j += 1;
                if j < length {
                    str += ",";
                    str += emitSpaces(1);
                }
            }
            
            i += 1;
            if i < argLength {
                str += ",";
                str += emitSpaces(1);
            }
        }
    }
    str += ")";
    str += emitSpaces(1);
    str += "->";
    str += emitSpaces(1);
    str += emitBasicBlockRef(term.unlockBB);
    str += ";";
    return str;
}

function emitReturn(Return term, int tabs) returns string { 
    string retStr = "";
    retStr += emitTabs(tabs);
    retStr += "return";
    retStr += ";";
    return retStr;
}

function emitPanic(Panic term, int tabs) returns string { 
    string retStr = "";
    retStr += emitTabs(tabs);
    retStr += "panic";
    retStr += emitSpaces(1);
    retStr += emitVarRef(term.errorOp);
    retStr += ";";
    return retStr;
}

function emitFPCall(FPCall term, int tabs) returns string { 
    string callStr = "";
    callStr += emitTabs(tabs);
    VarRef? lhsOp = term.lhsOp;
    if lhsOp is VarRef {
        callStr += emitVarRef(lhsOp);
        callStr += emitSpaces(1);
        callStr += "=";
        callStr += emitSpaces(1);
    }
    callStr += "FPCall";
    callStr += emitSpaces(1);
    callStr += emitVarRef(term.fp);
    callStr += "(";
    int i = 0;
    int argLength = term.args.length();
    foreach VarRef? ref in term.args {
        if ref is VarRef {
            callStr += emitVarRef(ref);
            i += 1;
            if i < argLength {
                callStr += ",";
                callStr += emitSpaces(1);
            }
        }
    }
    callStr += ")";
    callStr += emitSpaces(1);
    callStr += "->";
    callStr += emitSpaces(1);
    callStr += emitBasicBlockRef(term.thenBB);
    callStr += ";";
    return callStr;
}

function emitWaitAll(WaitAll term, int tabs) returns string { 
    string str = "";
    str += emitTabs(tabs);
    str += emitVarRef(term.lhsOp);
    str += emitSpaces(1);
    str += "=";
    str += emitSpaces(1);
    str += "waitAll";
    str += emitSpaces(1);
    str += "{";
    int i = 0;
    int argLength = term.futures.length();
    while i < argLength {
        VarRef? ref = term.futures[i];
        string key = term.keys[i];
        if ref is VarRef {
            str += "\"";
            str += key;
            str += "\"";
            str += ":";
            str += emitSpaces(1);
            str += emitVarRef(ref);
            i += 1;
            if i < argLength {
                str += ",";
                str += emitSpaces(1);
            }
        }
    }
    str += "}";
    str += emitSpaces(1);
    str += "->";
    str += emitSpaces(1);
    str += emitBasicBlockRef(term.thenBB);
    str += ";";
    return str;
}


