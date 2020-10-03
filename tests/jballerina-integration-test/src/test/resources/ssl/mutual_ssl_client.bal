// Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import ballerina/config;
import ballerina/http;
import ballerina/io;

http:ClientConfiguration mutualSslClientConf = {
    secureSocket:{
        keyStore:{
            path: config:getAsString("keystore"),
            password: "ballerina"
        },
        trustStore:{
            path: config:getAsString("truststore"),
            password: "ballerina"
        },
        protocol:{
            name: "TLS",
            versions: ["TLSv1.1"]
        },
        ciphers: ["TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA"],
        certValidation: {
            enable: false
        },
        ocspStapling: false,
        handshakeTimeoutInSeconds: 20,
        sessionTimeoutInSeconds: 30
    }
};

public function main() {
    http:Client httpClient = new("https://localhost:9116", mutualSslClientConf );
    var resp = httpClient->get("/echo/");
    if (resp is http:Response) {
        var payload = resp.getTextPayload();
        if (payload is string) {
            io:println(payload);
        } else {
            io:println(payload.message());
        }
    } else {
        io:println(resp.message());
    }
}
