/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.net.http.websocket.client;

import java.util.List;

/**
 * Represents a failover client connector config.
 *
 * @since 1.2.0
 */
public class FailoverContext {

    private int currentIndex = 0;
    private int failoverInterval = 0;
    private boolean firstConnectionEstablished = false;
    private List<String> targetUrls = null;
    private int initialIndex = 0;

    /**
     * Gets the index.
     *
     * @return currentIndex
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Assigns the index of the `FailoverContext` to the variable index.
     *
     * @param currentIndex - a current index
     */
    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    /**
     * Gets the target URLs.
     *
     * @return targetUrls
     */
    public List<String> getTargetUrls() {
        return targetUrls;
    }

    /**
     * Assigns the target URLs of the `FailoverContext` to the `targetUrls` variable.
     *
     * @param targetUrls - target URLs
     */
    void setTargetUrls(List<String> targetUrls) {
        this.targetUrls = targetUrls;
    }

    /**
     * Assigns the failover interval of the `FailoverContext` to the `failoverInterval` variable.
     *
     * @param failoverInterval - a failover interval
     */
    void setFailoverInterval(int failoverInterval) {
        this.failoverInterval = failoverInterval;
    }

    /**
     * Assigns the failover interval of the `FailoverContext` to the `failoverInterval` variable.
     * @return failoverInterval
     */
    public int getFailoverInterval() {
        return failoverInterval;
    }

    /**
     * Gets the `firstConnectionEstablished`.
     *
     * @return firstConnectionEstablished
     */
    public boolean isFirstConnectionEstablished() {
        return firstConnectionEstablished;
    }

    /**
     * Assigns the connection state of the `FailoverContext` to the `firstConnectionEstablished` variable.
     */
    public void setFirstConnectionEstablished() {
        this.firstConnectionEstablished = true;
    }

    /**
     * Gets the initial index.
     *
     * @return initialIndex
     */
    public int getInitialIndex() {
        return initialIndex;
    }

    /**
     * Assigns the `initialIndex` of the FailoverContext to the `initialIndex` variable.
     *
     * @param initialIndex - the initial index
     */
    public void setInitialIndex(int initialIndex) {
        this.initialIndex = initialIndex;
    }
}
