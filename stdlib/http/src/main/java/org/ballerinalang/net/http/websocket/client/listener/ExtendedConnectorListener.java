/*
 * Copyright (c) 2020, WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http:www.apache.orglicensesLICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specif ic language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.net.http.websocket.client.listener;

import org.ballerinalang.net.http.websocket.server.WebSocketConnectionInfo;
import org.wso2.transport.http.netty.contract.websocket.WebSocketConnectorListener;

/**
 * Interface for the extended client connector listener.
 *
 * @since 1.2.0
 */
public interface ExtendedConnectorListener extends WebSocketConnectorListener {

    void setConnectionInfo(WebSocketConnectionInfo connectionInfo);
}
