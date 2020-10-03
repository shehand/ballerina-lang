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

io:ReadableByteChannel? rch = ();
io:WritableByteChannel? wch = ();

function initReadableChannel(string filePath) returns io:Error? {
    var result = io:openReadableFile(filePath);
    if (result is io:ReadableByteChannel) {
        rch = result;
    } else {
        return result;
    }
}

function initWritableChannel(string filePath) {
    wch = <io:WritableByteChannel> io:openWritableFile(filePath);
}

function readBytes(int numberOfBytes) returns @tainted byte[]|io:Error {
    io:ReadableByteChannel? rChannel = rch;
    if (rChannel is io:ReadableByteChannel) {
        return rChannel.read(numberOfBytes);
    } else {
        io:GenericError e = io:GenericError("ReadableByteChannel not initialized");
        return e;
    }
}

function writeBytes(byte[] content, int startOffset) returns int|io:Error {
    int empty = -1;
    io:WritableByteChannel? wChannel = wch;
    if (wChannel is io:WritableByteChannel) {
        var result = wChannel.write(content, startOffset);
        if (result is int) {
            return result;
        } else {
            return result;
        }
    } else {
       io:GenericError e = io:GenericError("WritableByteChannel not initialized");
       return e;
    }
}

function closeReadableChannel() {
    io:ReadableByteChannel? rChannel = rch;
    if rChannel is io:ReadableByteChannel {
        var result = rChannel.close();
    }
}

function closeWritableChannel() {
    io:WritableByteChannel? wChannel = wch;
    if wChannel is io:WritableByteChannel {
        var result = wChannel.close();
    }
}

function testBase64EncodeByteChannel(io:ReadableByteChannel contentToBeEncoded) returns io:ReadableByteChannel|io:Error {
    return contentToBeEncoded.base64Encode();
}

function testBase64DecodeByteChannel(io:ReadableByteChannel contentToBeDecoded) returns io:ReadableByteChannel|io:Error {
    return contentToBeDecoded.base64Decode();
}
