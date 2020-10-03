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
package org.ballerinalang.langserver.extensions.document;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.ballerinalang.langserver.extensions.LSExtensionTestUtil;
import org.ballerinalang.langserver.extensions.ballerina.document.ASTModification;
import org.ballerinalang.langserver.extensions.ballerina.document.BallerinaASTResponse;
import org.ballerinalang.langserver.util.FileUtils;
import org.ballerinalang.langserver.util.TestUtil;
import org.eclipse.lsp4j.jsonrpc.Endpoint;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Test visible endpoint detection.
 */
public class ASTModifyTest {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    private Endpoint serviceEndpoint;

    private Path mainFile = FileUtils.RES_DIR.resolve("extensions")
            .resolve("document")
            .resolve("ast")
            .resolve("modify")
            .resolve("main.bal");

    private Path mainEmptyFile = FileUtils.RES_DIR.resolve("extensions")
            .resolve("document")
            .resolve("ast")
            .resolve("modify")
            .resolve("mainEmpty.bal");

//    private Path mainNatsFile = FileUtils.RES_DIR.resolve("extensions")
//            .resolve("document")
//            .resolve("ast")
//            .resolve("modify")
//            .resolve("mainNats.bal");
//
//    private Path mainNatsFileWithEmptyLine = FileUtils.RES_DIR.resolve("extensions")
//            .resolve("document")
//            .resolve("ast")
//            .resolve("modify")
//            .resolve("mainNatsWithEmptyLine.bal");

    private Path emptyFile = FileUtils.RES_DIR.resolve("extensions")
            .resolve("document")
            .resolve("ast")
            .resolve("modify")
            .resolve("empty.bal");

    private Path mainHttpCallFile = FileUtils.RES_DIR.resolve("extensions")
            .resolve("document")
            .resolve("ast")
            .resolve("modify")
            .resolve("mainHttpCall.bal");

    private Path mainHttpCallWithPrintFile = FileUtils.RES_DIR.resolve("extensions")
            .resolve("document")
            .resolve("ast")
            .resolve("modify")
            .resolve("mainHttpCallWithPrint.bal");
//
//    private Path serviceNatsFile = FileUtils.RES_DIR.resolve("extensions")
//            .resolve("document")
//            .resolve("ast")
//            .resolve("modify")
//            .resolve("serviceNats.bal");
//
//    private Path mainNatsModifiedFile = FileUtils.RES_DIR.resolve("extensions")
//            .resolve("document")
//            .resolve("ast")
//            .resolve("modify")
//            .resolve("mainNatsModified.bal");

    private Path serviceHttpCallModifiedFile = FileUtils.RES_DIR.resolve("extensions")
            .resolve("document")
            .resolve("ast")
            .resolve("modify")
            .resolve("serviceHttpCallModified.bal");

    public static void skipOnWindows() {
        if (OS.contains("win")) {
            throw new SkipException("Skipping the test case on Windows");
        }
    }

    @BeforeClass
    public void startLangServer() throws IOException {
        this.serviceEndpoint = TestUtil.initializeLanguageSever();
    }

    private void assertTree(JsonElement actual, JsonElement expected) {
        if (expected.isJsonObject()) {
            Assert.assertEquals(expected.getAsJsonObject().entrySet().size(),
                    actual.getAsJsonObject().entrySet().size());
            Set<String> expectedKeys = new HashSet<>();
            for (Map.Entry<String, JsonElement> entry : expected.getAsJsonObject().entrySet()) {
                expectedKeys.add(entry.getKey());
            }
            Set<String> actualKeys = new HashSet<>();
            for (Map.Entry<String, JsonElement> entry : actual.getAsJsonObject().entrySet()) {
                actualKeys.add(entry.getKey());
            }
            Assert.assertEquals(expectedKeys, actualKeys);
            for (String key : expectedKeys) {
                if (!key.equals("id") && !key.equals("ws")) {
                    JsonElement expectedElement = expected.getAsJsonObject().get(key);
                    JsonElement actualElement = actual.getAsJsonObject().get(key);
                    assertTree(actualElement, expectedElement);
                }
            }
        } else if (expected.isJsonArray()) {
            Assert.assertEquals(expected.getAsJsonArray().size(), actual.getAsJsonArray().size());
            for (int i = 0; i < expected.getAsJsonArray().size(); i++) {
                assertTree(actual.getAsJsonArray().get(i), expected.getAsJsonArray().get(i));
            }
        } else if (expected.isJsonNull()) {
            Assert.assertTrue(actual.isJsonNull());
        } else if (expected.isJsonPrimitive()) {
            if (!expected.getAsString().contains(".bal")) {
                if (expected.getAsString().lastIndexOf("$") >= 0) {
                    Assert.assertEquals(expected.getAsString().substring(0,
                            expected.getAsString().lastIndexOf("$")),
                            actual.getAsString().substring(0, expected.getAsString().lastIndexOf("$")));
                } else {
                    Assert.assertEquals(expected.getAsString(), actual.getAsString());
                }
            }
        }
    }

    private Path createTempFile(Path filePath) throws IOException {
        Path tempFilePath = FileUtils.BUILD_DIR.resolve("tmp")
                .resolve(UUID.randomUUID() + ".bal");
        Files.copy(filePath, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
        return tempFilePath;
    }

    private void assertSource(String actualSource, String expectedSource) {
        Assert.assertEquals(actualSource.replaceAll("([\\s]*)", " "),
                expectedSource.replaceAll("([\\s]*)", " "));
    }

    @Test(description = "Remove content.")
    public void testDelete() throws IOException {
        skipOnWindows();
        Path tempFile = createTempFile(mainFile);
        TestUtil.openDocument(serviceEndpoint, tempFile);
        ASTModification modification = new ASTModification(4, 5, 4, 33, "delete", null);
        BallerinaASTResponse astModifyResponse = LSExtensionTestUtil
                .modifyAndGetBallerinaAST(tempFile.toString(),
                        new ASTModification[]{modification}, this.serviceEndpoint);
        Assert.assertTrue(astModifyResponse.isParseSuccess());
        BallerinaASTResponse astResponse = LSExtensionTestUtil.getBallerinaDocumentAST(
                mainEmptyFile.toString(), this.serviceEndpoint);
        assertTree(astModifyResponse.getAst(), astResponse.getAst());
        String expectedFileContent = new String(Files.readAllBytes(mainEmptyFile));
        assertSource(astModifyResponse.getSource(), expectedFileContent);
        TestUtil.closeDocument(this.serviceEndpoint, tempFile);
    }

    @Test(description = "Insert content.")
    public void testInsert() throws IOException {
        skipOnWindows();
        Path tempFile = createTempFile(mainFile);
        TestUtil.openDocument(serviceEndpoint, tempFile);

        Gson gson = new Gson();
        //Adding ballerina/io to check how it duplicate import definitions are handled.
        ASTModification modification0 = new ASTModification(1, 1, 1, 1, "IMPORT",
                gson.fromJson("{\"TYPE\":\"ballerina/io\"}", JsonObject.class));
        ASTModification modification1 = new ASTModification(1, 1, 1, 1, "IMPORT",
                gson.fromJson("{\"TYPE\":\"ballerina/http\"}", JsonObject.class));
        ASTModification modification2 = new ASTModification(4, 1, 4, 1, "DECLARATION",
                gson.fromJson("{\"TYPE\":\"http:Client\", \"VARIABLE\":\"clientEndpoint\"," +
                        "\"PARAMS\": [\"\\\"http://postman-echo.com\\\"\"]}", JsonObject.class));
        ASTModification modification3 = new ASTModification(4, 1, 4, 1,
                "REMOTE_SERVICE_CALL_CHECK",
                gson.fromJson("{\"TYPE\":\"http:Response\", \"VARIABLE\":\"response\"," +
                        "\"CALLER\":\"clientEndpoint\", \"FUNCTION\":\"get\"," +
                        "\"PARAMS\": [\"\\\"/get?test=123\\\"\"]}", JsonObject.class));
        BallerinaASTResponse astModifyResponse = LSExtensionTestUtil
                .modifyAndGetBallerinaAST(tempFile.toString(),
                        new ASTModification[]{modification0, modification1, modification2, modification3},
                        this.serviceEndpoint);
        BallerinaASTResponse astResponse = LSExtensionTestUtil.getBallerinaDocumentAST(
                mainHttpCallWithPrintFile.toString(), this.serviceEndpoint);
        String expectedFileContent = new String(Files.readAllBytes(mainHttpCallWithPrintFile));
        assertSource(astModifyResponse.getSource(), expectedFileContent);
        assertTree(astModifyResponse.getAst(), astResponse.getAst());
        TestUtil.closeDocument(this.serviceEndpoint, tempFile);
    }

//    @Test(description = "Update content.")
//    public void testUpdate() throws IOException {
//        skipOnWindows();
//        Path tempFile = createTempFile(mainFile);
//        TestUtil.openDocument(serviceEndpoint, tempFile);
//
//        Gson gson = new Gson();
//        ASTModification modification1 = new ASTModification(1, 1, 3, 1, "IMPORT",
//                gson.fromJson("{\"TYPE\":\"ballerina/nats\"}", JsonObject.class));
//        ASTModification modification2 = new ASTModification(4, 1, 5, 1, "DECLARATION",
//                gson.fromJson("{\"TYPE\":\"nats:Connection\", \"VARIABLE\":\"connection\"," +
//                        "\"PARAMS\": []}", JsonObject.class));
//        ASTModification modification3 = new ASTModification(5, 1, 5, 1, "DECLARATION",
//                gson.fromJson("{\"TYPE\":\"nats:Producer\", \"VARIABLE\":\"producer\"," +
//                        "\"PARAMS\": [connection]}", JsonObject.class));
//        ASTModification modification4 = new ASTModification(5, 1, 5, 1,
//                "REMOTE_SERVICE_CALL",
//                gson.fromJson("{\"TYPE\":\"nats:Error?\", \"VARIABLE\":\"result\"," +
//                                "\"CALLER\":\"producer\", \"FUNCTION\":\"publish\"," +
//                                "\"PARAMS\": [\"\\\"Foo\\\"\", \"\\\"Test Message\\\"\"]}",
//                        JsonObject.class));
//        BallerinaASTResponse astModifyResponse = LSExtensionTestUtil
//                .modifyAndGetBallerinaAST(tempFile.toString(),
//                        new ASTModification[]{modification1, modification2, modification3, modification4},
//                        this.serviceEndpoint);
//        Assert.assertTrue(astModifyResponse.isParseSuccess());
//
//        BallerinaASTResponse astResponse = LSExtensionTestUtil.getBallerinaDocumentAST(
//                mainNatsFile.toString(), this.serviceEndpoint);
//        assertTree(astModifyResponse.getAst(), astResponse.getAst());
//        String expectedFileContent = new String(Files.readAllBytes(mainNatsFile));
//        assertSource(astModifyResponse.getSource(), expectedFileContent);
//        TestUtil.closeDocument(this.serviceEndpoint, tempFile);
//    }
//
//
//    @Test(description = "Main content.")
//    public void testMain() throws IOException {
//        skipOnWindows();
//        Path tempFile = createTempFile(emptyFile);
//        TestUtil.openDocument(serviceEndpoint, tempFile);
//
//        Gson gson = new Gson();
//        ASTModification modification1 = new ASTModification(1, 1, 1, 1, "IMPORT",
//                gson.fromJson("{\"TYPE\":\"ballerina/nats\"}", JsonObject.class));
//        ASTModification modification2 = new ASTModification(1, 1, 1, 1, "MAIN_START",
//                gson.fromJson("{\"COMMENT\":\"\"}", JsonObject.class));
//        ASTModification modification3 = new ASTModification(1, 1, 1, 1, "DECLARATION",
//                gson.fromJson("{\"TYPE\":\"nats:Connection\", \"VARIABLE\":\"connection\"," +
//                        "\"PARAMS\": []}", JsonObject.class));
//        ASTModification modification4 = new ASTModification(1, 1, 1, 1, "DECLARATION",
//                gson.fromJson("{\"TYPE\":\"nats:Producer\", \"VARIABLE\":\"producer\"," +
//                        "\"PARAMS\": [connection]}", JsonObject.class));
//        ASTModification modification5 = new ASTModification(1, 1, 1, 1,
//                "REMOTE_SERVICE_CALL",
//                gson.fromJson("{\"TYPE\":\"nats:Error?\", \"VARIABLE\":\"result\"," +
//                                "\"CALLER\":\"producer\", \"FUNCTION\":\"publish\"," +
//                                "\"PARAMS\": [\"\\\"Foo\\\"\", \"\\\"Test Message\\\"\"]}",
//                        JsonObject.class));
//        ASTModification modification6 = new ASTModification(1, 1, 1, 1, "MAIN_END",
//                gson.fromJson("{}", JsonObject.class));
//
//        BallerinaASTResponse astModifyResponse = LSExtensionTestUtil
//                .modifyAndGetBallerinaAST(tempFile.toString(),
//                        new ASTModification[]{modification1, modification2, modification3, modification4,
//                                modification5, modification6}, this.serviceEndpoint);
//        Assert.assertTrue(astModifyResponse.isParseSuccess());
//
//        BallerinaASTResponse astResponse = LSExtensionTestUtil.getBallerinaDocumentAST(
//                mainNatsFileWithEmptyLine.toString(), this.serviceEndpoint);
//        assertTree(astModifyResponse.getAst(), astResponse.getAst());
//        String expectedFileContent = new String(Files.readAllBytes(mainNatsFile));
//        assertSource(astModifyResponse.getSource(), expectedFileContent);
//        TestUtil.closeDocument(this.serviceEndpoint, tempFile);
//    }
//
//    @Test(description = "Main content insert.")
//    public void testMainInsert() throws IOException {
//        skipOnWindows();
//        Path tempFile = createTempFile(emptyFile);
//        TestUtil.openDocument(serviceEndpoint, tempFile);
//
//        Gson gson = new Gson();
//        BallerinaASTResponse astModifyResponse = LSExtensionTestUtil
//                .modifyTriggerAndGetBallerinaAST(tempFile.toString(),
//                        "main", gson.fromJson("{\"COMMENT\":\"\"}", JsonObject.class), this.serviceEndpoint);
//        Assert.assertTrue(astModifyResponse.isParseSuccess());
//
//        ASTModification modification3 = new ASTModification(1, 1, 1, 1, "IMPORT",
//                gson.fromJson("{\"TYPE\":\"ballerina/http\"}", JsonObject.class));
//        ASTModification modification4 = new ASTModification(2, 1, 2, 1, "DECLARATION",
//                gson.fromJson("{\"TYPE\":\"http:Client\", \"VARIABLE\":\"clientEndpoint\"," +
//                        "\"PARAMS\": [\"\\\"http://postman-echo.com\\\"\"]}", JsonObject.class));
//        ASTModification modification5 = new ASTModification(2, 1, 2, 1,
//                "REMOTE_SERVICE_CALL_CHECK",
//                gson.fromJson("{\"TYPE\":\"http:Response\", \"VARIABLE\":\"response\"," +
//                        "\"CALLER\":\"clientEndpoint\", \"FUNCTION\":\"get\"," +
//                        "\"PARAMS\": [\"\\\"/get?test=123\\\"\"]}", JsonObject.class));
//
//        BallerinaASTResponse astModifyResponse2 = LSExtensionTestUtil
//                .modifyAndGetBallerinaAST(tempFile.toString(),
//                        new ASTModification[]{modification3, modification4, modification5}, this.serviceEndpoint);
//        Assert.assertTrue(astModifyResponse2.isParseSuccess());
//
//        BallerinaASTResponse astResponse = LSExtensionTestUtil.getBallerinaDocumentAST(
//                mainHttpCallFile.toString(), this.serviceEndpoint);
//        String expectedFileContent = new String(Files.readAllBytes(mainHttpCallFile));
//        assertSource(astModifyResponse2.getSource(), expectedFileContent);
//        assertTree(astModifyResponse2.getAst(), astResponse.getAst());
//        TestUtil.closeDocument(this.serviceEndpoint, tempFile);
//    }
//
//    @Test(description = "Service.")
//    public void testService() throws IOException {
//        skipOnWindows();
//        Path tempFile = createTempFile(emptyFile);
//        TestUtil.openDocument(serviceEndpoint, tempFile);
//
//        Gson gson = new Gson();
//        BallerinaASTResponse astModifyResponse = LSExtensionTestUtil
//                .modifyTriggerAndGetBallerinaAST(tempFile.toString(), "service",
//                        gson.fromJson("{\"SERVICE\":\"hello\", \"RESOURCE\":\"sayHello\", \"RES_PATH\":\"sayHello\","
//                                + "\"METHODS\":\"\\\"GET\\\"\", \"PORT\":\"9090\"}", JsonObject.class),
//                                this.serviceEndpoint);
//        Assert.assertTrue(astModifyResponse.isParseSuccess());
//
//        ASTModification modification2 = new ASTModification(2, 1, 2, 1, "IMPORT",
//                gson.fromJson("{\"TYPE\":\"ballerina/nats\"}", JsonObject.class));
//        ASTModification modification3 = new ASTModification(11, 1, 11, 1, "DECLARATION",
//                gson.fromJson("{\"TYPE\":\"nats:Connection\", \"VARIABLE\":\"connection\"," +
//                        "\"PARAMS\": []}", JsonObject.class));
//        ASTModification modification4 = new ASTModification(11, 1, 11, 1, "DECLARATION",
//                gson.fromJson("{\"TYPE\":\"nats:Producer\", \"VARIABLE\":\"producer\"," +
//                        "\"PARAMS\": [\"connection\"]}", JsonObject.class));
//        ASTModification modification5 = new ASTModification(11, 1, 11, 1,
//                "REMOTE_SERVICE_CALL",
//                gson.fromJson("{\"TYPE\":\"nats:Error?\", \"VARIABLE\":\"result\"," +
//                        "\"CALLER\":\"producer\", \"FUNCTION\":\"publish\"," +
//                        "\"PARAMS\": [\"\\\"Foo\\\"\", \"\\\"Test Message\\\"\"]}", JsonObject.class));
//
//        BallerinaASTResponse astModifyResponse2 = LSExtensionTestUtil
//                .modifyAndGetBallerinaAST(tempFile.toString(),
//                        new ASTModification[]{modification2, modification3, modification4, modification5},
//                        this.serviceEndpoint);
//        Assert.assertTrue(astModifyResponse2.isParseSuccess());
//
//        BallerinaASTResponse astResponse = LSExtensionTestUtil.getBallerinaDocumentAST(
//                serviceNatsFile.toString(), this.serviceEndpoint);
//        String expectedFileContent = new String(Files.readAllBytes(serviceNatsFile));
//        assertSource(astModifyResponse2.getSource(), expectedFileContent);
//        assertTree(astModifyResponse2.getAst(), astResponse.getAst());
//        TestUtil.closeDocument(this.serviceEndpoint, tempFile);
//    }

//    @Test(description = "Service to main")
//    public void testMoveServiceToMain() throws IOException {
//        skipOnWindows();
//        Path tempFile = createTempFile(serviceNatsFile);
//        TestUtil.openDocument(serviceEndpoint, tempFile);
//
//        Gson gson = new Gson();
//        BallerinaASTResponse astModifyResponse = LSExtensionTestUtil
//                .modifyTriggerAndGetBallerinaAST(tempFile.toString(), "main",
//                        gson.fromJson("{}", JsonObject.class), this.serviceEndpoint);
//        Assert.assertTrue(astModifyResponse.isParseSuccess());
//        BallerinaASTResponse astResponse = LSExtensionTestUtil.getBallerinaDocumentAST(
//                mainNatsModifiedFile.toString(), this.serviceEndpoint);
//        String expectedFileContent = new String(Files.readAllBytes(mainNatsModifiedFile));
//        assertSource(astModifyResponse.getSource(), expectedFileContent);
//        assertTree(astModifyResponse.getAst(), astResponse.getAst());
//        TestUtil.closeDocument(this.serviceEndpoint, tempFile);
//    }

    //Todo: Bug in compiler need to be fixed
    //@Test(description = "Main to service")
//    public void testMoveMainToService() throws IOException {
//        skipOnWindows();
//        Path tempFile = createTempFile(mainHttpCallFile);
//        TestUtil.openDocument(serviceEndpoint, tempFile);
//
//        Gson gson = new Gson();
//        BallerinaASTResponse astModifyResponse = LSExtensionTestUtil
//                .modifyTriggerAndGetBallerinaAST(tempFile.toString(), "service",
//                        gson.fromJson("{\"SERVICE\":\"hello\", \"RESOURCE\":\"sayHello\"," +
//                                " \"PORT\":\"9090\"}", JsonObject.class), this.serviceEndpoint);
//        Assert.assertTrue(astModifyResponse.isParseSuccess());
//        BallerinaASTResponse astResponse = LSExtensionTestUtil.getBallerinaDocumentAST(
//                serviceHttpCallModifiedFile.toString(), this.serviceEndpoint);
//
//        String expectedFileContent = new String(Files.readAllBytes(serviceHttpCallModifiedFile));
//        assertSource(astModifyResponse.getSource(), expectedFileContent);
//        assertTree(astModifyResponse.getAst(), astResponse.getAst());
//        TestUtil.closeDocument(this.serviceEndpoint, tempFile);
//    }


//        @Test
//    public void test() {
////        String input = "import ballerina/http;\n" +
////                "public function main() {\n" +
////                "}";
//        String input = "import ballerina/http;\n" +
//                "public function main() {\n" +
//                "http:Client clientEndpoint = new (\"http://postman-echo.com\");\n" +
//                "http:Response response = check clientEndpoint->get(\"/get?test=123\");\n" +
//                "}";
//        TextDocument textDocument = TextDocuments.from(input);
//        SyntaxTree oldTree = SyntaxTree.from(textDocument);
////        TextEdit[] edits = new TextEdit[]{
////                TextEdit.from(TextRange.from(0, 22),
////                        ""),
////        };
//        TextEdit[] edits = new TextEdit[]{
//                TextEdit.from(TextRange.from(23, 47 - 23),
//                        "service hello on new http:Listener(9090) {\n" +
//                                "    resource function sayHello(http:Caller caller, http:Request req) {"),
//                TextEdit.from(TextRange.from(79, 1), "    }\n" +
//                        "}")
//        };
//        TextDocumentChange textDocumentChange = TextDocumentChange.from(edits);
//        SyntaxTree newTree = SyntaxTree.from(oldTree, textDocumentChange);
//    }

    @AfterClass
    public void stopLangServer() {
        TestUtil.shutdownLanguageServer(this.serviceEndpoint);
    }
}
