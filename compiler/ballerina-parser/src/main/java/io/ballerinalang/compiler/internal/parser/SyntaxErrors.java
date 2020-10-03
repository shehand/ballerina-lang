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
package io.ballerinalang.compiler.internal.parser;

import io.ballerinalang.compiler.internal.diagnostics.DiagnosticCode;
import io.ballerinalang.compiler.internal.diagnostics.DiagnosticErrorCode;
import io.ballerinalang.compiler.internal.parser.tree.STNode;
import io.ballerinalang.compiler.internal.parser.tree.STNodeDiagnostic;
import io.ballerinalang.compiler.internal.parser.tree.STNodeFactory;
import io.ballerinalang.compiler.internal.parser.tree.STNodeList;
import io.ballerinalang.compiler.internal.parser.tree.STToken;
import io.ballerinalang.compiler.internal.syntax.NodeListUtils;
import io.ballerinalang.compiler.internal.syntax.SyntaxUtils;
import io.ballerinalang.compiler.syntax.tree.SyntaxKind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class contains utilities to handle syntax errors.
 *
 * @since 2.0.0
 */
public class SyntaxErrors {

    private SyntaxErrors() {
    }

    public static STNodeDiagnostic createDiagnostic(DiagnosticCode diagnosticCode, Object... args) {
        return STNodeDiagnostic.from(diagnosticCode, args);
    }

    public static <T extends STNode> T addDiagnostic(T node, DiagnosticCode diagnosticCode, Object... args) {
        return addSyntaxDiagnostic(node, createDiagnostic(diagnosticCode, args));
    }

    public static <T extends STNode> T addSyntaxDiagnostic(T node, STNodeDiagnostic diagnostic) {
        return addSyntaxDiagnostics(node, Collections.singletonList(diagnostic));
    }

    @SuppressWarnings("unchecked")
    public static <T extends STNode> T addSyntaxDiagnostics(T node, Collection<STNodeDiagnostic> diagnostics) {
        if (diagnostics.isEmpty()) {
            return node;
        }

        Collection<STNodeDiagnostic> newDiagnostics;
        Collection<STNodeDiagnostic> oldDiagnostics = node.diagnostics();
        if (oldDiagnostics.isEmpty()) {
            newDiagnostics = new ArrayList<>(diagnostics);
        } else {
            // Merge all diagnostics
            newDiagnostics = new ArrayList<>(oldDiagnostics);
            newDiagnostics.addAll(diagnostics);
        }
        return (T) node.modifyWith(newDiagnostics);
    }

    public static STToken createMissingToken(SyntaxKind expectedKind) {
        return STNodeFactory.createMissingToken(expectedKind);
    }
    // TODO check for possibility of removing this method
    public static STToken createMissingTokenWithDiagnostics(SyntaxKind expectedKind) {
        return createMissingTokenWithDiagnostics(expectedKind, getErrorCode(expectedKind));
    }

    public static STToken createMissingTokenWithDiagnostics(SyntaxKind expectedKind, ParserRuleContext currentCtx) {
        return createMissingTokenWithDiagnostics(expectedKind, getErrorCode(currentCtx));
    }

    public static STToken createMissingTokenWithDiagnostics(SyntaxKind expectedKind,
                                                            DiagnosticCode diagnosticCode) {
        List<STNodeDiagnostic> diagnosticList = new ArrayList<>();
        diagnosticList.add(createDiagnostic(diagnosticCode));
        return STNodeFactory.createMissingToken(expectedKind, diagnosticList);
    }

    // TODO check for possibility of removing this method
    private static DiagnosticCode getErrorCode(SyntaxKind expectedKind) {
        switch (expectedKind) {
            // Keywords
            case PUBLIC_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_PUBLIC_KEYWORD;
            case PRIVATE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_PRIVATE_KEYWORD;
            case REMOTE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_REMOTE_KEYWORD;
            case ABSTRACT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_ABSTRACT_KEYWORD;
            case CLIENT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_CLIENT_KEYWORD;
            case IMPORT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_IMPORT_KEYWORD;
            case FUNCTION_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_FUNCTION_KEYWORD;
            case CONST_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_CONST_KEYWORD;
            case LISTENER_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_LISTENER_KEYWORD;
            case SERVICE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_SERVICE_KEYWORD;
            case XMLNS_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_XMLNS_KEYWORD;
            case ANNOTATION_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_ANNOTATION_KEYWORD;
            case TYPE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_TYPE_KEYWORD;
            case RECORD_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_RECORD_KEYWORD;
            case OBJECT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_OBJECT_KEYWORD;
            case VERSION_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_VERSION_KEYWORD;
            case AS_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_AS_KEYWORD;
            case ON_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_ON_KEYWORD;
            case RESOURCE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_RESOURCE_KEYWORD;
            case FINAL_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_FINAL_KEYWORD;
            case SOURCE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_SOURCE_KEYWORD;
            case WORKER_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_WORKER_KEYWORD;
            case PARAMETER_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_PARAMETER_KEYWORD;
            case FIELD_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_FIELD_KEYWORD;

            case RETURNS_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_RETURNS_KEYWORD;
            case RETURN_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_RETURN_KEYWORD;
            case EXTERNAL_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_EXTERNAL_KEYWORD;
            case TRUE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_TRUE_KEYWORD;
            case FALSE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_FALSE_KEYWORD;
            case IF_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_IF_KEYWORD;
            case ELSE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_ELSE_KEYWORD;
            case WHILE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_WHILE_KEYWORD;
            case CHECK_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_CHECK_KEYWORD;
            case CHECKPANIC_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_CHECKPANIC_KEYWORD;
            case PANIC_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_PANIC_KEYWORD;
            case CONTINUE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_CONTINUE_KEYWORD;
            case BREAK_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_BREAK_KEYWORD;
            case TYPEOF_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_TYPEOF_KEYWORD;
            case IS_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_IS_KEYWORD;
            case NULL_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_NULL_KEYWORD;
            case LOCK_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_LOCK_KEYWORD;
            case FORK_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_FORK_KEYWORD;
            case TRAP_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_TRAP_KEYWORD;
            case IN_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_IN_KEYWORD;
            case FOREACH_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_FOREACH_KEYWORD;
            case TABLE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_TABLE_KEYWORD;
            case KEY_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_KEY_KEYWORD;
            case LET_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_LET_KEYWORD;
            case NEW_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_NEW_KEYWORD;
            case FROM_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_FROM_KEYWORD;
            case WHERE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_WHERE_KEYWORD;
            case SELECT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_SELECT_KEYWORD;
            case START_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_START_KEYWORD;
            case FLUSH_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_FLUSH_KEYWORD;
            case DEFAULT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_DEFAULT_KEYWORD;
            case WAIT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_WAIT_KEYWORD;
            case DO_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_DO_KEYWORD;
            case TRANSACTION_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_TRANSACTION_KEYWORD;
            case TRANSACTIONAL_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_TRANSACTIONAL_KEYWORD;
            case COMMIT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_COMMIT_KEYWORD;
            case ROLLBACK_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_ROLLBACK_KEYWORD;
            case RETRY_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_RETRY_KEYWORD;
            case ENUM_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_ENUM_KEYWORD;
            case BASE16_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_BASE16_KEYWORD;
            case BASE64_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_BASE64_KEYWORD;
            case MATCH_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_MATCH_KEYWORD;
            case CONFLICT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_CONFLICT_KEYWORD;
            case LIMIT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_LIMIT_KEYWORD;
            case ORDER_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_ORDER_KEYWORD;
            case BY_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_BY_KEYWORD;
            case ASCENDING_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_ASCENDING_KEYWORD;
            case DESCENDING_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_DESCENDING_KEYWORD;
            case JOIN_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_JOIN_KEYWORD;
            case OUTER_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_OUTER_KEYWORD;
            case CLASS_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_CLASS_KEYWORD;
            case EQUALS_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_EQUALS_KEYWORD;

            // Type keywords
            case INT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_INT_KEYWORD;
            case BYTE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_BYTE_KEYWORD;
            case FLOAT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_FLOAT_KEYWORD;
            case DECIMAL_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_DECIMAL_KEYWORD;
            case STRING_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_STRING_KEYWORD;
            case BOOLEAN_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_BOOLEAN_KEYWORD;
            case XML_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_XML_KEYWORD;
            case JSON_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_JSON_KEYWORD;
            case HANDLE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_HANDLE_KEYWORD;
            case ANY_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_ANY_KEYWORD;
            case ANYDATA_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_ANYDATA_KEYWORD;
            case NEVER_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_NEVER_KEYWORD;
            case VAR_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_VAR_KEYWORD;
            case MAP_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_MAP_KEYWORD;
            case FUTURE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_FUTURE_KEYWORD;
            case TYPEDESC_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_TYPEDESC_KEYWORD;
            case ERROR_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_ERROR_KEYWORD;
            case STREAM_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_STREAM_KEYWORD;
            case READONLY_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_READONLY_KEYWORD;
            case DISTINCT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_DISTINCT_KEYWORD;

            // Separators
            case OPEN_BRACE_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_OPEN_BRACE_TOKEN;
            case CLOSE_BRACE_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_CLOSE_BRACE_TOKEN;
            case OPEN_PAREN_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_OPEN_PAREN_TOKEN;
            case CLOSE_PAREN_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_CLOSE_PAREN_TOKEN;
            case OPEN_BRACKET_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_OPEN_BRACKET_TOKEN;
            case CLOSE_BRACKET_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_CLOSE_BRACKET_TOKEN;
            case SEMICOLON_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_SEMICOLON_TOKEN;
            case DOT_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_DOT_TOKEN;
            case COLON_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_COLON_TOKEN;
            case COMMA_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_COMMA_TOKEN;
            case ELLIPSIS_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_ELLIPSIS_TOKEN;
            case OPEN_BRACE_PIPE_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_OPEN_BRACE_PIPE_TOKEN;
            case CLOSE_BRACE_PIPE_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_CLOSE_BRACE_PIPE_TOKEN;
            case AT_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_AT_TOKEN;
            case HASH_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_HASH_TOKEN;
            case BACKTICK_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_BACKTICK_TOKEN;
            case DOUBLE_QUOTE_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_DOUBLE_QUOTE_TOKEN;
            case SINGLE_QUOTE_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_SINGLE_QUOTE_TOKEN;

            // Operators
            case EQUAL_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_EQUAL_TOKEN;
            case DOUBLE_EQUAL_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_DOUBLE_EQUAL_TOKEN;
            case TRIPPLE_EQUAL_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_TRIPPLE_EQUAL_TOKEN;
            case PLUS_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_PLUS_TOKEN;
            case MINUS_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_MINUS_TOKEN;
            case SLASH_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_SLASH_TOKEN;
            case PERCENT_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_PERCENT_TOKEN;
            case ASTERISK_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_ASTERISK_TOKEN;
            case LT_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_LT_TOKEN;
            case LT_EQUAL_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_LT_EQUAL_TOKEN;
            case GT_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_GT_TOKEN;
            case RIGHT_DOUBLE_ARROW_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_RIGHT_DOUBLE_ARROW_TOKEN;
            case QUESTION_MARK_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_QUESTION_MARK_TOKEN;
            case PIPE_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_PIPE_TOKEN;
            case GT_EQUAL_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_GT_EQUAL_TOKEN;
            case EXCLAMATION_MARK_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_EXCLAMATION_MARK_TOKEN;
            case NOT_EQUAL_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_NOT_EQUAL_TOKEN;
            case NOT_DOUBLE_EQUAL_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_NOT_DOUBLE_EQUAL_TOKEN;
            case BITWISE_AND_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_BITWISE_AND_TOKEN;
            case BITWISE_XOR_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_BITWISE_XOR_TOKEN;
            case LOGICAL_AND_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_LOGICAL_AND_TOKEN;
            case LOGICAL_OR_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_LOGICAL_OR_TOKEN;
            case NEGATION_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_NEGATION_TOKEN;
            case RIGHT_ARROW_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_RIGHT_ARROW_TOKEN;
            case INTERPOLATION_START_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_INTERPOLATION_START_TOKEN;
            case XML_PI_START_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_XML_PI_START_TOKEN;
            case XML_PI_END_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_XML_PI_END_TOKEN;
            case XML_COMMENT_START_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_XML_COMMENT_START_TOKEN;
            case XML_COMMENT_END_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_XML_COMMENT_END_TOKEN;
            case SYNC_SEND_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_SYNC_SEND_TOKEN;
            case LEFT_ARROW_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_LEFT_ARROW_TOKEN;
            case DOUBLE_DOT_LT_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_DOUBLE_DOT_LT_TOKEN;
            case DOUBLE_LT_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_DOUBLE_LT_TOKEN;
            case ANNOT_CHAINING_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_ANNOT_CHAINING_TOKEN;
            case OPTIONAL_CHAINING_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_OPTIONAL_CHAINING_TOKEN;
            case ELVIS_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_ELVIS_TOKEN;
            case DOT_LT_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_DOT_LT_TOKEN;
            case SLASH_LT_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_SLASH_LT_TOKEN;
            case DOUBLE_SLASH_DOUBLE_ASTERISK_LT_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_DOUBLE_SLASH_DOUBLE_ASTERISK_LT_TOKEN;
            case SLASH_ASTERISK_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_SLASH_ASTERISK_TOKEN;
            case DOUBLE_GT_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_DOUBLE_GT_TOKEN;
            case TRIPPLE_GT_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_TRIPPLE_GT_TOKEN;

            case IDENTIFIER_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_IDENTIFIER;
            case STRING_LITERAL:
                return DiagnosticErrorCode.ERROR_MISSING_STRING_LITERAL;
            case DECIMAL_INTEGER_LITERAL_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_DECIMAL_INTEGER_LITERAL;
            case HEX_INTEGER_LITERAL_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_HEX_INTEGER_LITERAL;
            case DECIMAL_FLOATING_POINT_LITERAL_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_DECIMAL_FLOATING_POINT_LITERAL;
            case HEX_FLOATING_POINT_LITERAL_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_HEX_FLOATING_POINT_LITERAL;
            case XML_TEXT_CONTENT:
                return DiagnosticErrorCode.ERROR_MISSING_XML_TEXT_CONTENT;
            case TEMPLATE_STRING:
                return DiagnosticErrorCode.ERROR_MISSING_TEMPLATE_STRING;
            default:
                return DiagnosticErrorCode.ERROR_SYNTAX_ERROR;
        }
    }

    private static DiagnosticCode getErrorCode(ParserRuleContext currentCtx) {
        switch (currentCtx) {
            case EXTERNAL_FUNC_BODY:
            case BINDING_PATTERN_OR_EXPR_RHS:
                return DiagnosticErrorCode.ERROR_MISSING_EQUAL_TOKEN;
            case FUNC_BODY_OR_TYPE_DESC_RHS:
            case FUNC_BODY_BLOCK:
            case FUNC_BODY:
                return DiagnosticErrorCode.ERROR_MISSING_OPEN_BRACE_TOKEN;
            case FUNC_DEF:
            case FUNC_DEF_OR_FUNC_TYPE:
            case FUNC_TYPE_DESC:
            case FUNC_TYPE_DESC_OR_ANON_FUNC:
            case IDENT_AFTER_OBJECT_IDENT:
                return DiagnosticErrorCode.ERROR_MISSING_FUNCTION_KEYWORD;
            case VAR_DECL_STMT_RHS:
            case IMPORT_DECL_RHS:
            case IMPORT_SUB_VERSION:
                return DiagnosticErrorCode.ERROR_MISSING_SEMICOLON_TOKEN;
            case SIMPLE_TYPE_DESCRIPTOR:
            case REQUIRED_PARAM:
            case VAR_DECL_STMT:
            case ASSIGNMENT_OR_VAR_DECL_STMT:
            case DEFAULTABLE_PARAM:
            case REST_PARAM:
            case CLASS_MEMBER_WITHOUT_METADATA:
            case OBJECT_MEMBER_WITHOUT_METADATA:
            case RECORD_FIELD_WITHOUT_METADATA:
            case TYPE_DESCRIPTOR:
            case OPTIONAL_TYPE_DESCRIPTOR:
            case ARRAY_TYPE_DESCRIPTOR:
                return DiagnosticErrorCode.ERROR_MISSING_TYPE_DESC;
            case TYPE_NAME:
            case TYPE_REFERENCE:
            case FIELD_ACCESS_IDENTIFIER:
            case CLASS_NAME:
            case FUNC_NAME:
            case FUNCTION_KEYWORD_RHS:
            case VARIABLE_NAME:
            case TYPE_NAME_OR_VAR_NAME:
            case IMPORT_MODULE_NAME:
            case IMPORT_ORG_OR_MODULE_NAME:
            case IMPORT_PREFIX:
            case VARIABLE_REF:
            case BASIC_LITERAL: // return var-ref for any kind of terminal expression
            case SERVICE_NAME:
            case IDENTIFIER:
            case QUALIFIED_IDENTIFIER:
            case NAMESPACE_PREFIX:
            case IMPLICIT_ANON_FUNC_PARAM:
            case WORKER_NAME_OR_METHOD_NAME:
            case PEER_WORKER_NAME:
            case RECEIVE_FIELD_NAME:
            case WAIT_FIELD_NAME:
            case FIELD_BINDING_PATTERN_NAME:
            case XML_ATOMIC_NAME_IDENTIFIER:
            case MAPPING_FIELD_NAME:
            case MAPPING_FIELD:
            case ANNOT_DECL_OPTIONAL_TYPE:
            case WORKER_NAME:
            case NAMED_WORKERS:
            case ANNOTATION_TAG:
            case CONST_DECL_TYPE:
            case AFTER_PARAMETER_TYPE:
            case MODULE_ENUM_NAME:
            case ENUM_MEMBER_NAME:
            case TYPED_BINDING_PATTERN_TYPE_RHS:
            case ASSIGNMENT_STMT:
            case EXPRESSION:
            case TERMINAL_EXPRESSION:
            case XML_NAME:
            case ACCESS_EXPRESSION:
            case BINDING_PATTERN_STARTING_IDENTIFIER:
            case COMPUTED_FIELD_NAME:
            case SIMPLE_BINDING_PATTERN:
            case ERROR_FIELD_BINDING_PATTERN:
            case ERROR_CAUSE_SIMPLE_BINDING_PATTERN:
                return DiagnosticErrorCode.ERROR_MISSING_IDENTIFIER;
            case VERSION_NUMBER:
            case MAJOR_VERSION:
            case MINOR_VERSION:
            case PATCH_VERSION:
                return DiagnosticErrorCode.ERROR_MISSING_DECIMAL_INTEGER_LITERAL;
            case STRING_LITERAL_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_STRING_LITERAL;
            case ARRAY_LENGTH:
            case DECIMAL_INTEGER_LITERAL_TOKEN:
            case SIGNED_INT_OR_FLOAT_RHS:
                return DiagnosticErrorCode.ERROR_MISSING_DECIMAL_INTEGER_LITERAL;
            case HEX_INTEGER_LITERAL_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_HEX_INTEGER_LITERAL;
            case CONSTANT_EXPRESSION:
                return DiagnosticErrorCode.ERROR_MISSING_STRING_LITERAL;
            case CONSTANT_EXPRESSION_START:
            case XML_NAMESPACE_PREFIX_DECL:
            case OBJECT_FIELD_RHS:
            case OPTIONAL_FIELD_INITIALIZER:
                return DiagnosticErrorCode.ERROR_MISSING_SEMICOLON_TOKEN;
            case NIL_LITERAL:
            case ERROR_MATCH_PATTERN:
                return DiagnosticErrorCode.ERROR_MISSING_ERROR_KEYWORD;
            case DECIMAL_FLOATING_POINT_LITERAL_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_DECIMAL_FLOATING_POINT_LITERAL;
            case HEX_FLOATING_POINT_LITERAL_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_HEX_FLOATING_POINT_LITERAL;
            case STATEMENT:
            case STATEMENT_WITHOUT_ANNOTS:
            case ENUM_MEMBER_RHS:
            case ENUM_MEMBER_END:
                return DiagnosticErrorCode.ERROR_MISSING_CLOSE_BRACE_TOKEN;
            case MATCH_PATTERN_RHS:
            case OPTIONAL_MATCH_GUARD:
                return DiagnosticErrorCode.ERROR_MISSING_RIGHT_DOUBLE_ARROW_TOKEN;
            case XML_COMMENT_CONTENT:
            case XML_PI_DATA:
                return DiagnosticErrorCode.ERROR_MISSING_XML_TEXT_CONTENT;
            case CLASS_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_CLASS_KEYWORD;
            default:
                return getSeperatorTokenErrorCode(currentCtx);
        }
    }

    private static DiagnosticCode getSeperatorTokenErrorCode(ParserRuleContext ctx) {
        switch (ctx) {
            case BITWISE_AND_OPERATOR:
                return DiagnosticErrorCode.ERROR_MISSING_BITWISE_AND_TOKEN;
            case EQUAL_OR_RIGHT_ARROW:
            case ASSIGN_OP:
            case STMT_START_WITH_EXPR_RHS:
            case CONST_DECL_RHS:
                return DiagnosticErrorCode.ERROR_MISSING_EQUAL_TOKEN;
            case BINARY_OPERATOR:
            case UNARY_OPERATOR:
            case COMPOUND_BINARY_OPERATOR:
            case UNARY_EXPRESSION:
            case EXPRESSION_RHS:
            case PLUS_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_PLUS_TOKEN;
            case CLOSE_BRACE:
            case RECORD_BODY_END:
            case RECORD_FIELD_OR_RECORD_END:
                return DiagnosticErrorCode.ERROR_MISSING_CLOSE_BRACE_TOKEN;
            case CLOSE_PARENTHESIS:
            case ARG_LIST_END:
                return DiagnosticErrorCode.ERROR_MISSING_CLOSE_PAREN_TOKEN;
            case COMMA:
            case ERROR_MESSAGE_BINDING_PATTERN_END_COMMA:
                return DiagnosticErrorCode.ERROR_MISSING_COMMA_TOKEN;
            case OPEN_BRACE:
                return DiagnosticErrorCode.ERROR_MISSING_OPEN_BRACE_TOKEN;
            case OPEN_PARENTHESIS:
            case ARG_LIST_START:
            case PARENTHESISED_TYPE_DESC_START:
                return DiagnosticErrorCode.ERROR_MISSING_OPEN_PAREN_TOKEN;
            case SEMICOLON:
            case OBJECT_FIELD_RHS:
            case EXPR_STMT_RHS:
            case ATTACH_POINT_END:
            case FIELD_DESCRIPTOR_RHS:
                return DiagnosticErrorCode.ERROR_MISSING_SEMICOLON_TOKEN;
            case ASTERISK:
            case INFERRED_TYPE_DESC:
                return DiagnosticErrorCode.ERROR_MISSING_ASTERISK_TOKEN;
            case CLOSED_RECORD_BODY_END:
                return DiagnosticErrorCode.ERROR_MISSING_CLOSE_BRACE_PIPE_TOKEN;
            case CLOSED_RECORD_BODY_START:
            case RECORD_BODY_START:
                return DiagnosticErrorCode.ERROR_MISSING_OPEN_BRACE_PIPE_TOKEN;
            case ELLIPSIS:
                return DiagnosticErrorCode.ERROR_MISSING_ELLIPSIS_TOKEN;
            case QUESTION_MARK:
                return DiagnosticErrorCode.ERROR_MISSING_QUESTION_MARK_TOKEN;
            case CLOSE_BRACKET:
            case MEMBER_ACCESS_KEY_EXPR_END:
                return DiagnosticErrorCode.ERROR_MISSING_CLOSE_BRACKET_TOKEN;
            case DOT:
                return DiagnosticErrorCode.ERROR_MISSING_DOT_TOKEN;
            case OPEN_BRACKET:
            case TUPLE_TYPE_DESC_START:
                return DiagnosticErrorCode.ERROR_MISSING_OPEN_BRACKET_TOKEN;
            case SLASH:
                return DiagnosticErrorCode.ERROR_MISSING_SLASH_TOKEN;
            case COLON:
                return DiagnosticErrorCode.ERROR_MISSING_COLON_TOKEN;
            case AT:
                return DiagnosticErrorCode.ERROR_MISSING_AT_TOKEN;
            case RIGHT_ARROW:
                return DiagnosticErrorCode.ERROR_MISSING_RIGHT_ARROW_TOKEN;
            case GT:
            case GT_TOKEN:
            case XML_START_OR_EMPTY_TAG_END:
            case XML_ATTRIBUTES:
                return DiagnosticErrorCode.ERROR_MISSING_GT_TOKEN;
            case LT:
            case LT_TOKEN:
            case XML_START_OR_EMPTY_TAG:
            case XML_END_TAG:
                return DiagnosticErrorCode.ERROR_MISSING_LT_TOKEN;
            case SYNC_SEND_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_SYNC_SEND_TOKEN;
            case ANNOT_CHAINING_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_ANNOT_CHAINING_TOKEN;
            case OPTIONAL_CHAINING_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_OPTIONAL_CHAINING_TOKEN;
            case DOT_LT_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_DOT_LT_TOKEN;
            case SLASH_LT_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_SLASH_LT_TOKEN;
            case DOUBLE_SLASH_DOUBLE_ASTERISK_LT_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_DOUBLE_SLASH_DOUBLE_ASTERISK_LT_TOKEN;
            case SLASH_ASTERISK_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_SLASH_ASTERISK_TOKEN;
            case MINUS_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_MINUS_TOKEN;
            case LEFT_ARROW_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_LEFT_ARROW_TOKEN;
            case TEMPLATE_END:
            case TEMPLATE_START:
            case XML_CONTENT:
            case XML_TEXT:
                return DiagnosticErrorCode.ERROR_MISSING_BACKTICK_TOKEN;
            case XML_COMMENT_START:
                return DiagnosticErrorCode.ERROR_MISSING_XML_COMMENT_START_TOKEN;
            case XML_COMMENT_END:
                return DiagnosticErrorCode.ERROR_MISSING_XML_COMMENT_END_TOKEN;
            case XML_PI:
            case XML_PI_START:
                return DiagnosticErrorCode.ERROR_MISSING_XML_PI_START_TOKEN;
            case XML_PI_END:
                return DiagnosticErrorCode.ERROR_MISSING_XML_PI_END_TOKEN;
            case XML_QUOTE_END:
            case XML_QUOTE_START:
                return DiagnosticErrorCode.ERROR_MISSING_DOUBLE_QUOTE_TOKEN;
            case INTERPOLATION_START_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_INTERPOLATION_START_TOKEN;
            case EXPR_FUNC_BODY_START:
            case RIGHT_DOUBLE_ARROW:
                return DiagnosticErrorCode.ERROR_MISSING_RIGHT_DOUBLE_ARROW_TOKEN;
            default:
                return getKeywordErrorCode(ctx);
        }
    }

    private static DiagnosticCode getKeywordErrorCode(ParserRuleContext ctx) {
        switch (ctx) {
            case PUBLIC_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_PUBLIC_KEYWORD;
            case PRIVATE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_PRIVATE_KEYWORD;
            case REMOTE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_REMOTE_KEYWORD;
            case ABSTRACT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_ABSTRACT_KEYWORD;
            case CLIENT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_CLIENT_KEYWORD;
            case IMPORT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_IMPORT_KEYWORD;
            case FUNCTION_KEYWORD:
            case FUNCTION_IDENT:
                return DiagnosticErrorCode.ERROR_MISSING_FUNCTION_KEYWORD;
            case CONST_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_CONST_KEYWORD;
            case LISTENER_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_LISTENER_KEYWORD;
            case SERVICE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_SERVICE_KEYWORD;
            case XMLNS_KEYWORD:
            case XML_NAMESPACE_DECLARATION:
                return DiagnosticErrorCode.ERROR_MISSING_XMLNS_KEYWORD;
            case ANNOTATION_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_ANNOTATION_KEYWORD;
            case TYPE_KEYWORD:
            case ATTACH_POINT_IDENT:
            case SINGLE_KEYWORD_ATTACH_POINT_IDENT:
                return DiagnosticErrorCode.ERROR_MISSING_TYPE_KEYWORD;
            case RECORD_KEYWORD:
            case RECORD_FIELD:
            case RECORD_IDENT:
                return DiagnosticErrorCode.ERROR_MISSING_RECORD_KEYWORD;
            case OBJECT_KEYWORD:
            case OBJECT_TYPE_QUALIFIER:
            case OBJECT_CONSTRUCTOR_QUALIFIER:
            case OBJECT_IDENT:
                return DiagnosticErrorCode.ERROR_MISSING_OBJECT_KEYWORD;
            case VERSION_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_VERSION_KEYWORD;
            case AS_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_AS_KEYWORD;
            case ON_KEYWORD:
            case ANNOT_DECL_RHS:
                return DiagnosticErrorCode.ERROR_MISSING_ON_KEYWORD;
            case RESOURCE_KEYWORD:
            case RESOURCE_IDENT:
                return DiagnosticErrorCode.ERROR_MISSING_RESOURCE_KEYWORD;
            case FINAL_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_FINAL_KEYWORD;
            case SOURCE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_SOURCE_KEYWORD;
            case WORKER_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_WORKER_KEYWORD;
            case FIELD_IDENT:
                return DiagnosticErrorCode.ERROR_MISSING_FIELD_KEYWORD;
            case RETURNS_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_RETURNS_KEYWORD;
            case RETURN_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_RETURN_KEYWORD;
            case EXTERNAL_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_EXTERNAL_KEYWORD;
            case BOOLEAN_LITERAL:
                return DiagnosticErrorCode.ERROR_MISSING_TRUE_KEYWORD;
            case IF_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_IF_KEYWORD;
            case ELSE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_ELSE_KEYWORD;
            case WHILE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_WHILE_KEYWORD;
            case CHECKING_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_CHECK_KEYWORD;
            case PANIC_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_PANIC_KEYWORD;
            case CONTINUE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_CONTINUE_KEYWORD;
            case BREAK_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_BREAK_KEYWORD;
            case TYPEOF_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_TYPEOF_KEYWORD;
            case IS_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_IS_KEYWORD;
            case NULL_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_NULL_KEYWORD;
            case LOCK_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_LOCK_KEYWORD;
            case FORK_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_FORK_KEYWORD;
            case TRAP_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_TRAP_KEYWORD;
            case IN_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_IN_KEYWORD;
            case FOREACH_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_FOREACH_KEYWORD;
            case TABLE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_TABLE_KEYWORD;
            case KEY_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_KEY_KEYWORD;
            case LET_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_LET_KEYWORD;
            case NEW_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_NEW_KEYWORD;
            case FROM_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_FROM_KEYWORD;
            case WHERE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_WHERE_KEYWORD;
            case SELECT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_SELECT_KEYWORD;
            case START_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_START_KEYWORD;
            case FLUSH_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_FLUSH_KEYWORD;
            case DEFAULT_KEYWORD:
            case OPTIONAL_PEER_WORKER:
            case DEFAULT_WORKER_NAME_IN_ASYNC_SEND:
                return DiagnosticErrorCode.ERROR_MISSING_DEFAULT_KEYWORD;
            case WAIT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_WAIT_KEYWORD;
            case DO_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_DO_KEYWORD;
            case TRANSACTION_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_TRANSACTION_KEYWORD;
            case TRANSACTIONAL_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_TRANSACTIONAL_KEYWORD;
            case COMMIT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_COMMIT_KEYWORD;
            case ROLLBACK_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_ROLLBACK_KEYWORD;
            case RETRY_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_RETRY_KEYWORD;
            case ENUM_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_ENUM_KEYWORD;
            case BASE16_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_BASE16_KEYWORD;
            case BASE64_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_BASE64_KEYWORD;
            case MATCH_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_MATCH_KEYWORD;
            case CONFLICT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_CONFLICT_KEYWORD;
            case LIMIT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_LIMIT_KEYWORD;
            case ORDER_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_ORDER_KEYWORD;
            case BY_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_BY_KEYWORD;
            case ORDER_DIRECTION:
                return DiagnosticErrorCode.ERROR_MISSING_ASCENDING_KEYWORD;
            case JOIN_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_JOIN_KEYWORD;
            case OUTER_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_OUTER_KEYWORD;
            case FAIL_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_FAIL_KEYWORD;
            case PIPE:
            case UNION_OR_INTERSECTION_TOKEN:
                return DiagnosticErrorCode.ERROR_MISSING_PIPE_TOKEN;
            case EQUALS_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_EQUALS_KEYWORD;

            // Type keywords
            case STRING_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_STRING_KEYWORD;
            case XML_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_XML_KEYWORD;
            case VAR_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_VAR_KEYWORD;
            case MAP_KEYWORD:
            case NAMED_WORKER_DECL:
            case PARAMETERIZED_TYPE:
                return DiagnosticErrorCode.ERROR_MISSING_MAP_KEYWORD;
            case FUTURE_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_FUTURE_KEYWORD;
            case TYPEDESC_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_TYPEDESC_KEYWORD;
            case ERROR_KEYWORD:
            case ERROR_BINDING_PATTERN:
                return DiagnosticErrorCode.ERROR_MISSING_ERROR_KEYWORD;
            case STREAM_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_STREAM_KEYWORD;
            case READONLY_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_READONLY_KEYWORD;
            case DISTINCT_KEYWORD:
                return DiagnosticErrorCode.ERROR_MISSING_DISTINCT_KEYWORD;
            default:
                return DiagnosticErrorCode.ERROR_SYNTAX_ERROR;
        }
    }

    /**
     * Clone the given {@code STNode} with the invalid node as leading minutiae.
     *
     * @param toClone     the node to be cloned
     * @param invalidNode the invalid node
     * @return a cloned node with the given invalidNode minutiae
     */
    public static STNode cloneWithLeadingInvalidNodeMinutiae(STNode toClone, STNode invalidNode) {
        return cloneWithLeadingInvalidNodeMinutiae(toClone, invalidNode, null);
    }

    /**
     * Clone the given {@code STNode} with the invalid node as leading minutiae.
     *
     * @param toClone        the node to be cloned
     * @param invalidNode    the invalid node
     * @param diagnosticCode the {@code DiagnosticCode} to be added
     * @param args           additional arguments required to format the diagnostic message
     * @return a cloned node with the given invalidNode minutiae
     */
    public static STNode cloneWithLeadingInvalidNodeMinutiae(STNode toClone,
                                                             STNode invalidNode,
                                                             DiagnosticCode diagnosticCode,
                                                             Object... args) {
        STToken firstToken = toClone.firstToken();
        STToken firstTokenWithInvalidNodeMinutiae = cloneWithLeadingInvalidNodeMinutiae(firstToken,
                invalidNode, diagnosticCode, args);
        return toClone.replace(firstToken, firstTokenWithInvalidNodeMinutiae);
    }

    /**
     * Clone the given {@code STToken} with the invalid node as leading minutiae.
     *
     * @param toClone        the token to be cloned
     * @param invalidNode    the invalid node
     * @param diagnosticCode the {@code DiagnosticCode} to be added
     * @param args           additional arguments required to format the diagnostic message
     * @return a cloned token with the given invalidNode minutiae
     */
    public static STToken cloneWithLeadingInvalidNodeMinutiae(STToken toClone,
                                                              STNode invalidNode,
                                                              DiagnosticCode diagnosticCode,
                                                              Object... args) {
        List<STNode> minutiaeList = convertInvalidNodeToMinutiae(invalidNode);
        STNodeList leadingMinutiae = (STNodeList) toClone.leadingMinutiae();
        leadingMinutiae = leadingMinutiae.addAll(0, minutiaeList);
        STToken cloned = toClone.modifyWith(leadingMinutiae, toClone.trailingMinutiae());
        return diagnosticCode == null ? cloned : addDiagnostic(cloned, diagnosticCode, args);
    }

    /**
     * Clone the given {@code STNode} with the invalid node as trailing minutiae.
     *
     * @param toClone     the node to be cloned
     * @param invalidNode the invalid node
     * @return a cloned node with the given invalidNode minutiae
     */
    public static STNode cloneWithTrailingInvalidNodeMinutiae(STNode toClone, STNode invalidNode) {
        return cloneWithTrailingInvalidNodeMinutiae(toClone, invalidNode, null);
    }

    /**
     * Clone the given {@code STNode} with the invalid node as trailing minutiae.
     *
     * @param toClone        the node to be cloned
     * @param invalidNode    the invalid node
     * @param diagnosticCode the {@code DiagnosticCode} to be added
     * @param args           additional arguments required to format the diagnostic message
     * @return a cloned node with the given invalidNode minutiae
     */
    public static STNode cloneWithTrailingInvalidNodeMinutiae(STNode toClone,
                                                              STNode invalidNode,
                                                              DiagnosticCode diagnosticCode,
                                                              Object... args) {
        STToken lastToken = toClone.lastToken();
        STToken lastTokenWithInvalidNodeMinutiae = cloneWithTrailingInvalidNodeMinutiae(lastToken,
                invalidNode, diagnosticCode, args);
        return toClone.replace(lastToken, lastTokenWithInvalidNodeMinutiae);
    }

    /**
     * Clone the given {@code STToken} with the invalid node as trailing minutiae.
     *
     * @param toClone        the token to be cloned
     * @param invalidNode    the invalid node
     * @param diagnosticCode the {@code DiagnosticCode} to be added
     * @param args           additional arguments required to format the diagnostic message
     * @return a cloned token with the given invalidNode minutiae
     */
    public static STToken cloneWithTrailingInvalidNodeMinutiae(STToken toClone,
                                                               STNode invalidNode,
                                                               DiagnosticCode diagnosticCode,
                                                               Object... args) {
        List<STNode> minutiaeList = convertInvalidNodeToMinutiae(invalidNode);
        STNodeList trailingMinutiae = (STNodeList) toClone.trailingMinutiae();
        trailingMinutiae = trailingMinutiae.addAll(minutiaeList);
        STToken cloned = toClone.modifyWith(toClone.leadingMinutiae(), trailingMinutiae);
        return diagnosticCode == null ? cloned : addDiagnostic(cloned, diagnosticCode, args);
    }

    /**
     * Converts the invalid node into a list of {@code STMinutiae} nodes.
     * <p>
     * Here are the steps:
     * 1) Iterates through all the tokens in the invalid node. For each token:
     * 2) Add the leading minutiae to the list
     * 3) Create a new token without leading or trailing minutiae and add it to the list
     * 4) Add the trailing minutiae to the list
     *
     * @param invalidNode the invalid node to be converted
     * @return a lit of {@code STMinutiae} nodes
     */
    private static List<STNode> convertInvalidNodeToMinutiae(STNode invalidNode) {
        List<STNode> minutiaeList = new ArrayList<>();
        List<STToken> tokens = invalidNode.tokens();
        for (STToken token : tokens) {
            addMinutiaeToList(minutiaeList, token.leadingMinutiae());
            if (!token.isMissing()) {
                STToken tokenWithNoMinutiae = token.modifyWith(
                        STNodeFactory.createEmptyNodeList(), STNodeFactory.createEmptyNodeList());
                minutiaeList.add(STNodeFactory.createInvalidNodeMinutiae(tokenWithNoMinutiae));
            }
            addMinutiaeToList(minutiaeList, token.trailingMinutiae());
        }
        return minutiaeList;
    }

    private static void addMinutiaeToList(List<STNode> list, STNode minutiae) {
        if (!NodeListUtils.isSTNodeList(minutiae)) {
            list.add(minutiae);
            return;
        }

        STNodeList minutiaeList = (STNodeList) minutiae;
        for (int index = 0; index < minutiaeList.size(); index++) {
            STNode element = minutiaeList.get(index);
            if (SyntaxUtils.isSTNodePresent(element)) {
                list.add(element);
            }
        }
    }
}
