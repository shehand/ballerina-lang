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
import ballerina/io;
import ballerina/lang.'xml as xmllib;

public type Employee record {
    string name;
};

public function main(int i, float f, string s, byte b, boolean bool, json j, xml x, Employee e, string... args) {
    xmllib:Element element = <xmllib:Element> x;
    string restArgs = "";
    foreach var str in args {
        restArgs += str + " ";
    }
    string boolStr = "false";
    if (bool) {
        boolStr = "true";
    }

    io:print("integer: " + i.toHexString() + ", float: " + f.toString() + ", string: " + s + ", byte: " +
            b.toString() + ", boolean: " + boolStr + ", JSON Name Field: " +
            j.name.toString() + ", XML Element Name: " + element.getName() + ", Employee Name Field: " + e.name +
            ", string rest args: " + restArgs);
}
