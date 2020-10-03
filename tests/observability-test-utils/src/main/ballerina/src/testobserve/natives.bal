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

import ballerina/java;

# Get all the finished spans.
#
# + serviceName - The name of the service of which the finished spans should be fetched
# + return - The finished spans as a json
public function getFinishedSpans(string serviceName) returns json {
    handle serviceNameHandle = java:fromString(serviceName);
    return externGetFinishedSpans(serviceNameHandle);
}

# Get all the finished spans.
#
# + return - The finished spans as a json
function externGetFinishedSpans(handle serviceName) returns json = @java:Method {
    name: "getFinishedSpans",
    'class: "org.ballerina.testobserve.MockTracerUtils"
} external;
