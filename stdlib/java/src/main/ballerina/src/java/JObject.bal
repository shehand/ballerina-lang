// Copyright (c) 2020 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

# The Ballerina abstract object which is to be extended by Ballerina
# objects representing Ballerina bindings for Java classes.
#
# + jObj - The `handle` reference to the corresponding Java object.
public type JObject object {

    public handle jObj;
};

# Returns the string representation of a Java object stored in a handle reference.
#
# + jObj - The `handle` reference to the corresponding Java object.
# + return - The `string` representation of the Java object.
public function jObjToString(handle jObj) returns string {
    handle jStringValue = toStringInternal(jObj);
    return toString(jStringValue) ?: "null";
}

function toStringInternal(handle jObj) returns handle = @Method {
    name: "toString",
    'class: "java.lang.Object"
} external;
