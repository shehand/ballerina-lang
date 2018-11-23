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

import ballerina/runtime;

function waitTest1() returns int {
    future<int> f1 = start add_1(5, 2);
    future<int> f2 = start add_2(10, 12);
    int result = wait f1 | f2;
    return result;
}

function waitTest2() returns int {
    future<int> f3 = start add_3(60, 22);
    future<int> f2 = start add_2(11, 11);
    future<int> f1 = start add_1(1, 1);
    int result = wait f3 | f2 | f1;
    return result;
}

function waitTest3() returns string {
    future<string> f4 = start concat("foo");
    future<string> f5 = start concat("bar");
    string result = wait f4 | f5;
    return result;
}

function waitTest4() returns any {
    future<int> f1 = start add_1(15, 15);
    future<string> f5 = start concat("bar");
    future<boolean> f6 = start status();
    any result = wait f1 | f5 | f6;
    return result;
}

function waitTest5() returns map {
    future<map> f9 = start getAddrMap();
    future<map> f10 = start getEmpMap();
    map m = wait f9 | f10;
    return m;
}

function waitTest6() returns string|int {
    future<int> f1 = start add_1(88, 88);
    future<int> f3 = start add_3(50, 100);
    future<string> f4 = start concat("foo");
    string|int result = wait f1 | f3 | f4;
    return result;
}

function waitTest7() returns string|int|boolean {
    future<int> f1 = start add_1(2, 2);
    future<int> f2 = start add_2(10, 12);
    future<boolean> f6 = start status();
    future<string> f4 = start concat("foo");
    string|int|boolean result = wait f1 | f2 | f6 | f4;
    return result;
}

function waitTest8() returns int|string {
    future<int|string> f7 =  start concat("xyz");
    int|string result = wait fuInt() | f7;
    return result;
}

function waitTest9() returns int|string|float {
    future<int|string> f7 =  start concat("xyz");
    future<int|float> f8 = start add_3(66, 33);
    int|string|float result  = wait f7 | f8;
    return result;
}

function waitTest10() returns int|string|float {
    future<int|float> f8 = start add_3(66, 11);
    future<int|string> f7 =  start concat("xyz");
    future<string> f4 = start concat("foo");
    future<string> f5 = start concat("bar");
    int|string|float result  = wait f8 | f7 | f4 |f5;
    return result;
}

function waitTest11() returns int|string|float {
    future<int> f1 = start add_panic1(88, 88);
    future<int> f3 = start add_panic2(50, 100);
    future<string> f4 = start concat("foo");
    string|int result = wait f1 | f3 | f4;
    return result;
}

function waitTest12() returns int {
    future<int> f1 = start add_panic1(88, 88);
    future<int> f3 = start add_panic2(50, 100);
    future<int> f4 = start add_panic3(4, 3);
    int result = wait f1 | f3 | f4;
    return result;
}

function waitTest13() returns int {
    future<int> f1 = start add_panic1(88, 88);
    future<int> f3 = start add_panic2(50, 100);
    var result = trap wait f1 | f3;
    if (result is int) {
        return result;
    } else if (result is error) {
        return 0;
    } else {
        return 1;
    }
}


function add_panic1(int i, int j) returns int {
    int k = i + j;
    if (true) {
        error err = error("err from panic" );
        panic err;
    }
    return k;
}

function add_panic2(int i, int j) returns int {
    int k = i + j;
    int l = 0;
    while (l < 9999999) {
        l = l + 1;
    }
    if (true) {
        error err = error("err from panic" );
        panic err;
    }
    return k;
}

function add_panic3(int i, int j) returns int {
    int k = i + j;
    int l = 0;
    while (l < 8888888) {
        l = l + 1;
    }
    if (true) {
        error err = error("err from panic" );
        panic err;
    }
    return k;
}
//function waitTest11() returns any { // Needs to be tested out
//    any result = 0;
//    worker w1 {
//        future<int> f1 = start add_1(5, 2);
//        future<int> f3 = start add_3(50, 100);
//        future<string> f4 = start concat("foo");
//        result = wait f1 | f3 | f4;
//    }
//    result = wait w1;
//    return result;
//}

function add_1(int i, int j) returns int {
    int k = i + j;
    //runtime:sleep(5000);
    int l = 0;
    while(l < 999999) {
        l = l + 1;
    }
    return k;
}

function add_2(int i, int j) returns int {
    int k = i + j;
    return k;
}

function add_3(int i, int j) returns int {
    int k = i + j;
    return k;
}

function concat(string name) returns string {
    return "hello " + name;
}

function status() returns boolean {
    return true;
}

function fuInt() returns future<int> {
    future <int> i = start add_3(11, 17);
    return i;
}

function getEmpMap() returns map {
    map empMap = { fname: "foo", lname: "bar"};
    runtime:sleep(2000);
    return empMap;
}

function getAddrMap() returns map {
    map addrMap = { line1: "No. 20", line2: "Palm Grove", city: "Colombo 03"};
    return addrMap;
}
