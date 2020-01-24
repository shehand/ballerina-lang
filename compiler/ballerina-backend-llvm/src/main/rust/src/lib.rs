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

extern crate libc;

use std::mem;
use std::os::raw::c_longlong;

#[no_mangle]
pub extern "C" fn new_int_array(size: c_longlong) -> *mut Vec<*mut c_longlong> {
    let mut size_t = size;
    if size < 0 {
        size_t = 8;
    }
    let size_t = size_t as usize;
    let foo: Box<Vec<*mut c_longlong>> = Box::new(Vec::with_capacity(size_t));
    let vec_pointer = Box::into_raw(foo);
    return vec_pointer as *mut Vec<*mut c_longlong>;
}

#[no_mangle]
pub extern "C" fn int_array_store(arr_ptr: *mut Vec<*mut c_longlong>, n: i64, ref_ptr: *mut c_longlong) {
    let mut arr = unsafe { Box::from_raw(arr_ptr) };
    let n_size = n as usize;
    let len = n_size + 1;
    if arr.len() < len {
        arr.resize(len, 0 as *mut c_longlong);
    }
    arr[n_size] = ref_ptr;
    mem::forget(arr);
}

#[no_mangle]
pub extern "C" fn int_array_load(arr_ptr: *mut Vec<*mut c_longlong>, n: i64) -> *mut c_longlong {
    let arr = unsafe { Box::from_raw(arr_ptr)};
    let n_size = n as usize;
    let return_val = arr[n_size];
    mem::forget(arr);
    return return_val;
}
