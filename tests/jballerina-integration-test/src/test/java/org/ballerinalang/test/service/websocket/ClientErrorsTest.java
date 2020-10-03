/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.test.service.websocket;

import org.ballerinalang.test.context.BallerinaTestException;
import org.ballerinalang.test.util.websocket.client.WebSocketTestClient;
import org.ballerinalang.test.util.websocket.server.WebSocketRemoteServer;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tests some of the errors that could occur in WebSocket. Basically tests if the error design for WebSocket is correct.
 */
@Test(groups = {"websocket-test"})
public class ClientErrorsTest extends WebSocketTestCommons {

    private WebSocketTestClient client;
    private static final String URL = "ws://localhost:21027/client/errors";
    private WebSocketRemoteServer remoteServer;
    private volatile boolean setupped = false;

    @BeforeMethod(description = "Related file 27_client_exceptions.bal")
    public void setup() throws InterruptedException, URISyntaxException, BallerinaTestException {
        if (!setupped) {
            remoteServer = new WebSocketRemoteServer(15000);
            remoteServer.run();
            client = new WebSocketTestClient(URL);
            client.handshake();
            setupped = true;
        }
    }

    @Test(description = "Connection refused IO error")
    public void testConnectionError() throws InterruptedException {
        String expectedError1 = "error ConnectionError: IO Error";

        String expectedError2 = "error ConnectionError: IO Error";
        sendTextAndMatchAnyResponse("invalid-connection", expectedError1, expectedError2);
    }

    @Test(description = "SSL/TLS error")
    public void testSslError() throws InterruptedException {
        sendTextAndAssertResponse(
                "ssl",
                "error GenericError: SSL/TLS Error");
    }

    @Test(description = "The frame exceeds the max frame length")
    public void testLongFrameError() throws InterruptedException {
        sendTextAndAssertResponse(
                "long-frame",
                "error ProtocolError: io.netty.handler.codec.TooLongFrameException: invalid " +
                        "payload for PING (payload length must be <= 125, was 148");

    }

    @Test(description = "Close the connection and push text")
    public void testConnectionClosedError() throws InterruptedException {
        sendTextAndAssertResponse(
                "connection-closed",
                "error ConnectionClosureError: Close frame already sent. Cannot push text data!");

    }

    @Test(description = "Handshake failing because of missing subprotocol")
    public void testHandshakeError() throws InterruptedException {
        sendTextAndAssertResponse(
                "handshake",
                "error InvalidHandshakeError: Invalid subprotocol. Actual: null. Expected one of: abc");

    }

    @Test(description = "Tests the ready function using the WebSocket client. When `readyOnConnect` is true," +
            " calls the `ready()` function.")
    public void testReadyOnConnect() throws InterruptedException {
        sendTextAndAssertResponse(
                "ready",
                "error GenericError: Already started reading frames");

    }

    private void sendTextAndAssertResponse(String msg, String expected) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        client.setCountDownLatch(countDownLatch);
        client.sendText(msg);
        countDownLatch.await(20, TimeUnit.SECONDS);
        String textReceived = client.getTextReceived();
        Assert.assertEquals(textReceived, expected);
    }

    private void sendTextAndMatchAnyResponse(String msg, String expected1, String expected2)
            throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        client.setCountDownLatch(countDownLatch);
        client.sendText(msg);
        countDownLatch.await(20, TimeUnit.SECONDS);
        String textReceived = client.getTextReceived();
        boolean matched = textReceived.equals(expected1) || textReceived.equals(expected2);
        Assert.assertTrue(matched);
    }

    @AfterClass(description = "Stops the Ballerina server")
    public void cleanup() throws InterruptedException {
        remoteServer.stop();
        client.shutDown();
    }
}
