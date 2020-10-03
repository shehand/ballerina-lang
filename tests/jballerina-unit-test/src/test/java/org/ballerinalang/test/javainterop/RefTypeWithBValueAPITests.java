/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.test.javainterop;

import org.ballerinalang.jvm.api.BErrorCreator;
import org.ballerinalang.jvm.api.BStringUtils;
import org.ballerinalang.jvm.api.values.BError;
import org.ballerinalang.jvm.scheduling.Scheduler;
import org.ballerinalang.jvm.types.BArrayType;
import org.ballerinalang.jvm.types.BTypes;
import org.ballerinalang.jvm.types.TypeTags;
import org.ballerinalang.jvm.values.ArrayValueImpl;
import org.ballerinalang.jvm.values.FPValue;
import org.ballerinalang.jvm.values.FutureValue;
import org.ballerinalang.jvm.values.MapValueImpl;
import org.ballerinalang.jvm.values.TypedescValueImpl;
import org.ballerinalang.jvm.values.XMLItem;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.model.values.BFloat;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BTypeDescValue;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.model.values.BValueArray;
import org.ballerinalang.model.values.BValueType;
import org.ballerinalang.model.values.BXML;
import org.ballerinalang.nativeimpl.jvm.tests.JavaInteropTestCheckedException;
import org.ballerinalang.test.util.BCompileUtil;
import org.ballerinalang.test.util.BRunUtil;
import org.ballerinalang.test.util.CompileResult;
import org.ballerinalang.util.exceptions.BLangRuntimeException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.xml.namespace.QName;

/**
 * Test cases for java interop with ballerina ref types.
 *
 * @since 1.0.0
 */
public class RefTypeWithBValueAPITests {

    private CompileResult result;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile("test-src/javainterop/ballerina_types_with_public_api.bal");
    }

    @Test(description = "Test interoperability with ballerina array value as return and map as a param")
    public void testInteropWithBalArrayAndMap() {
        BValue[] returns = BRunUtil.invoke(result, "interopWithArrayAndMap");
        Assert.assertEquals(returns.length, 1);

        Assert.assertTrue(returns[0] instanceof BValueArray);
        BValueArray arr = (BValueArray) returns[0];
        Assert.assertEquals(arr.stringValue(), "[1, 8]");
    }

    @Test(description = "Test interoperability with ballerina service type value as a param")
    public void testInteropWithServiceTypesAndStringReturn() {
        BValue[] returns = BRunUtil.invoke(result, "acceptServiceAndBooleanReturn");
        Assert.assertEquals(returns.length, 1);

        Assert.assertTrue(returns[0] instanceof BBoolean);
        BBoolean bool = (BBoolean) returns[0];
        Assert.assertTrue(bool.booleanValue());
    }

    @Test(description = "Test interoperability with ballerina ref types as params and map return")
    public void testInteropWithRefTypesAndMapReturn() {
        BValue[] returns = BRunUtil.invoke(result, "interopWithRefTypesAndMapReturn");
        Assert.assertEquals(returns.length, 1);

        Assert.assertTrue(returns[0] instanceof BMap);
        BMap bMap = (BMap) returns[0];
        Assert.assertEquals(bMap.stringValue(), "{\"a\":{}, \"b\":[5, \"hello\", {}]," +
                " \"c\":{name:\"sameera\"}, \"e\":{}, \"f\":83, \"g\":{name:\"sample\"}}");
    }

    @Test(description = "Test interoperability with ballerina error return")
    public void testInteropWithErrorReturn() {
        BValue[] returns = BRunUtil.invoke(result, "interopWithErrorReturn");
        Assert.assertEquals(returns.length, 1);

        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "example error with given reason");
    }

    @Test(description = "Test interoperability with ballerina union return")
    public void testInteropWithUnionReturn() {
        BValue[] returns = BRunUtil.invoke(result, "interopWithUnionReturn");
        Assert.assertEquals(returns.length, 1);

        Assert.assertTrue(returns[0] instanceof BBoolean);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test interoperability with ballerina object return")
    public void testInteropWithObjectReturn() {
        BValue[] returns = BRunUtil.invoke(result, "interopWithObjectReturn");
        Assert.assertEquals(returns.length, 1);

        Assert.assertTrue(returns[0] instanceof BBoolean);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test interoperability with ballerina record return")
    public void testInteropWithRecordReturn() {
        BValue[] returns = BRunUtil.invoke(result, "interopWithRecordReturn");
        Assert.assertEquals(returns.length, 1);

        Assert.assertTrue(returns[0] instanceof BBoolean);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test interoperability with ballerina any return")
    public void testInteropWithAnyReturn() {
        BValue[] returns = BRunUtil.invoke(result, "interopWithAnyReturn");
        Assert.assertEquals(returns.length, 1);

        Assert.assertTrue(returns[0] instanceof BBoolean);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test interoperability with ballerina anydata return")
    public void testInteropWithAnydataReturn() {
        BValue[] returns = BRunUtil.invoke(result, "interopWithAnydataReturn");
        Assert.assertEquals(returns.length, 1);

        Assert.assertTrue(returns[0] instanceof BBoolean);
        Assert.assertTrue(((BBoolean) returns[0]).booleanValue());
    }

    @Test(description = "Test interoperability with ballerina json return")
    public void testInteropWithJsonReturns() {
        BValue[] returns = BRunUtil.invoke(result, "testJsonReturns");
        Assert.assertEquals(returns.length, 5);

        Assert.assertTrue(returns[0] instanceof BMap);
        Assert.assertEquals(returns[0].toString(), "{\"name\":\"John\"}");

        Assert.assertTrue(returns[1] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[1]).intValue(), 4);

        Assert.assertTrue(returns[2] instanceof BMap);
        Assert.assertEquals(returns[2].toString(), "{\"name\":\"Doe\"}");

        Assert.assertTrue(returns[3] instanceof BValueArray);
        Assert.assertEquals(returns[3].toString(), "[\"John\"]");

        Assert.assertNull(returns[4]);
    }

    @Test(description = "Test interoperability with ballerina json params")
    public void testInteropWithJsonParams() {
        BValue[] returns = BRunUtil.invoke(result, "testJsonParams");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 7);
    }

    @Test
    public void testGetXML() {
        BValue[] returns = BRunUtil.invoke(result, "getXML");
        Assert.assertTrue(returns[0] instanceof BXML);
        Assert.assertEquals(returns[0].stringValue(), "<hello></hello>");
    }

    @Test
    public void testPassXML() {
        BValue[] returns = BRunUtil.invoke(result, "testPassingXML");
        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "<foo></foo>");
    }

    @Test
    public void testGetAllInts() {
        BValue[] returns = BRunUtil.invoke(result, "getAllInts");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 2);
    }

    @Test
    public void testAcceptAllInts() {
        BValue[] returns = BRunUtil.invoke(result, "testAcceptAllInts");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 4);
        Assert.assertTrue(returns[1] instanceof BFloat);
        Assert.assertEquals(((BFloat) returns[1]).floatValue(), 4.0);
        Assert.assertTrue(returns[2] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[2]).intValue(), 4);
    }

    @Test
    public void testGetMixType() {
        BValue[] returns = BRunUtil.invoke(result, "getMixType");
        Assert.assertTrue(returns[0] instanceof BValueType);
        Assert.assertEquals(((BValueType) returns[0]).intValue(), 5);
    }

    @Test
    public void testGetIntegersAsMixType() {
        BValue[] returns = BRunUtil.invoke(result, "getIntegersAsMixType");
        Assert.assertTrue(returns[0] instanceof BValueType);
        Assert.assertEquals(((BValueType) returns[0]).intValue(), 2);
    }

    @Test(expectedExceptions = {BLangRuntimeException.class},
          expectedExceptionsMessageRegExp = "error: \\{ballerina\\}TypeCastError \\{\"message\":\"incompatible " +
                  "types: 'int' cannot be cast to 'MIX_TYPE'.*")
    public void testGetInvalidIntegerAsMixType() {
        BValue[] returns = BRunUtil.invoke(result, "getInvalidIntegerAsMixType");
        Assert.assertTrue(returns[0] instanceof BValueType);
        Assert.assertEquals(((BValueType) returns[0]).intValue(), 3);
    }

    @Test
    public void testAcceptMixType() {
        BValue[] returns = BRunUtil.invoke(result, "testAcceptMixTypes");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 2);
        Assert.assertTrue(returns[1] instanceof BString);
        Assert.assertEquals(returns[1].stringValue(), "hello");
        Assert.assertTrue(returns[2] instanceof BBoolean);
        Assert.assertFalse(((BBoolean) returns[2]).booleanValue());
    }

    @Test
    public void testUseFunctionPointer() {
        BValue[] returns = BRunUtil.invoke(result, "testUseFunctionPointer");
        Assert.assertTrue(returns[0] instanceof BValueType);
        Assert.assertEquals(((BValueType) returns[0]).intValue(), 7);
    }

    @Test
    public void testGetFunctionPointer() {
        BValue[] returns = BRunUtil.invoke(result, "testGetFunctionPointer");
        Assert.assertTrue(returns[0] instanceof BValueType);
        Assert.assertEquals(((BValueType) returns[0]).intValue(), 10);
    }

    @Test
    public void testUseTypeDesc() {
        BValue[] returns = BRunUtil.invoke(result, "testUseTypeDesc");
        Assert.assertTrue(returns[0] instanceof BString);
        Assert.assertEquals(returns[0].stringValue(), "typedesc json");
    }

    @Test
    public void testGetTypeDesc() {
        BValue[] returns = BRunUtil.invoke(result, "testGetTypeDesc");
        Assert.assertTrue(returns[0] instanceof BTypeDescValue);
        Assert.assertEquals(returns[0].stringValue(), "xml");
    }

    @Test
    public void testUseFuture() {
        BValue[] returns = BRunUtil.invoke(result, "testUseFuture");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 4);
    }

    @Test
    public void testGetFuture() {
        BValue[] returns = BRunUtil.invoke(result, "testGetFuture");
        Assert.assertTrue(returns[0] instanceof BInteger);
        Assert.assertEquals(((BInteger) returns[0]).intValue(), 4);
    }

    // static methods

    // This scenario is for map value to be passed to interop and return array value.
    public static org.ballerinalang.jvm.api.values.BArray
    getArrayValueFromMap(org.ballerinalang.jvm.api.values.BString key, org.ballerinalang.jvm.api.values.BMap mapValue) {
        org.ballerinalang.jvm.api.values.BArray arrayValue = new ArrayValueImpl(new BArrayType(BTypes.typeInt));
        arrayValue.add(0, 1);
        long fromMap = (long) mapValue.get(key);
        arrayValue.add(1, fromMap);
        return arrayValue;
    }

    public static org.ballerinalang.jvm.api.values.BMap
    acceptRefTypesAndReturnMap(org.ballerinalang.jvm.api.values.BObject a,
                               org.ballerinalang.jvm.api.values.BArray b, Object c,
                               BError d, Object e, Object f,
                               org.ballerinalang.jvm.api.values.BMap g) {
        org.ballerinalang.jvm.api.values.BMap<String, Object> mapValue = new MapValueImpl();
        mapValue.put("a", a);
        mapValue.put("b", b);
        mapValue.put("c", c);
        mapValue.put("e", e);
        mapValue.put("f", f);
        mapValue.put("g", g);
        return mapValue;
    }

    public static Object acceptIntUnionReturn(int flag) {
        switch (flag) {
            case 1:
                return 25;
            case 2:
                return BStringUtils.fromString("sample value return");
            case 3:
                return 54.88;
            default:
                return true;
        }
    }

    public static org.ballerinalang.jvm.api.values.BObject
    acceptObjectAndObjectReturn(org.ballerinalang.jvm.api.values.BObject p, int newVal) {
        p.set(BStringUtils.fromString("age"), newVal);
        return p;
    }

    public static org.ballerinalang.jvm.api.values.BMap
    acceptRecordAndRecordReturn(org.ballerinalang.jvm.api.values.BMap e,
                                org.ballerinalang.jvm.api.values.BString newVal) {
        e.put(BStringUtils.fromString("name"), newVal);
        return e;
    }

    public static int acceptIntReturnIntThrowsCheckedException(long a) throws JavaInteropTestCheckedException {
        return (int) (a + 5);
    }

    public static org.ballerinalang.jvm.api.values.BMap
    acceptRecordAndRecordReturnWhichThrowsCheckedException(org.ballerinalang.jvm.api.values.BMap e,
                                                           org.ballerinalang.jvm.api.values.BString newVal)
            throws JavaInteropTestCheckedException {
        e.put("name", newVal);
        return e;
    }

    public static Object acceptIntUnionReturnWhichThrowsCheckedException(int flag)
            throws JavaInteropTestCheckedException {
        switch (flag) {
            case 1:
                return 25;
            case 2:
                return "sample value return";
            case 3:
                return 54.88;
            default:
                return true;
        }
    }

    public static org.ballerinalang.jvm.api.values.BMap
    acceptRefTypesAndReturnMapWhichThrowsCheckedException(org.ballerinalang.jvm.api.values.BObject a,
                                                          org.ballerinalang.jvm.api.values.BArray b, Object c,
                                                          BError d, Object e,
                                                          Object f, org.ballerinalang.jvm.api.values.BMap g)
            throws JavaInteropTestCheckedException {
        org.ballerinalang.jvm.api.values.BMap<String, Object> mapValue = new MapValueImpl<>();
        mapValue.put("a", a);
        mapValue.put("b", b);
        mapValue.put("c", c);
        mapValue.put("e", e);
        mapValue.put("f", f);
        mapValue.put("g", g);
        return mapValue;
    }

    public static BError acceptStringErrorReturnWhichThrowsCheckedException(
            org.ballerinalang.jvm.api.values.BString msg)
            throws JavaInteropTestCheckedException {
        return BErrorCreator.createError(msg, new MapValueImpl<>(BTypes.typeErrorDetail));
    }

    public static org.ballerinalang.jvm.api.values.BArray
    getArrayValueFromMapWhichThrowsCheckedException(
            org.ballerinalang.jvm.api.values.BString key, org.ballerinalang.jvm.api.values.BMap mapValue)
            throws JavaInteropTestCheckedException {
        org.ballerinalang.jvm.api.values.BArray arrayValue = new ArrayValueImpl(new BArrayType(BTypes.typeInt));
        arrayValue.add(0, 1);
        long fromMap = (long) mapValue.get(key);
        arrayValue.add(1, fromMap);
        return arrayValue;
    }

    public static boolean acceptServiceObjectAndReturnBoolean(org.ballerinalang.jvm.api.values.BObject serviceObject) {
        return TypeTags.SERVICE_TAG == serviceObject.getType().getTag();
    }

    public static BError acceptStringErrorReturn(org.ballerinalang.jvm.api.values.BString msg) {
        return BErrorCreator.createError(msg);
    }

    public static Object getJson() {
        MapValueImpl<org.ballerinalang.jvm.api.values.BString, org.ballerinalang.jvm.api.values.BString> map =
                new MapValueImpl<>(BTypes.typeJSON);
        map.put(BStringUtils.fromString("name"), BStringUtils.fromString("John"));
        return map;
    }

    public static org.ballerinalang.jvm.api.values.BMap<org.ballerinalang.jvm.api.values.BString,
            org.ballerinalang.jvm.api.values.BString> getJsonObject() {
        org.ballerinalang.jvm.api.values.BMap<org.ballerinalang.jvm.api.values.BString,
                org.ballerinalang.jvm.api.values.BString>
                map = new MapValueImpl<>(BTypes.typeJSON);
        map.put(BStringUtils.fromString("name"), BStringUtils.fromString("Doe"));
        return map;
    }

    public static org.ballerinalang.jvm.api.values.BArray getJsonArray() {
        org.ballerinalang.jvm.api.values.BArray array = new ArrayValueImpl(new BArrayType(BTypes.typeJSON));
        array.add(0, (Object) "John");
        return array;
    }

    public static Object getNullJson() {
        return null;
    }

    public static int getInt() {
        return 4;
    }

    public static int getIntFromJson(Object json) {
        return ((Number) json).intValue();
    }

    public static int getIntFromJsonInt(int json) {
        return json;
    }

    public static org.ballerinalang.jvm.api.values.BXML getXML() {
        return new XMLItem(new QName("hello"));
    }

    public static org.ballerinalang.jvm.api.values.BString getStringFromXML(org.ballerinalang.jvm.api.values.BXML x) {
        return BStringUtils.fromString(x.toString());
    }

    public static int getAllInts() {
        return 2;
    }

    public static int getInvalidMixType() {
        return 456;
    }

    public static int acceptAllInts(int x) {
        return x;
    }

    public static float getAllFloats() {
        return (float) 3.54;
    }

    public static float acceptAllFloats(float x) {
        return x;
    }

    public static Object getAny() {
        return 5;
    }

    public static Object acceptAny(Object x) {
        return x;
    }

    public static int useFunctionPointer(org.ballerinalang.jvm.api.values.BFunctionPointer fp) {
        return ((Long) fp.call(new Object[]{Scheduler.getStrand(), 3, true, 4, true})).intValue();
    }

    public static org.ballerinalang.jvm.api.values.BFunctionPointer getFunctionPointer(Object fp) {
        return (FPValue) fp;
    }

    public static org.ballerinalang.jvm.api.values.BString useTypeDesc(
            org.ballerinalang.jvm.api.values.BTypedesc type) {
        return BStringUtils.fromString(type.stringValue(null));
    }

    public static org.ballerinalang.jvm.api.values.BTypedesc getTypeDesc() {
        return new TypedescValueImpl(BTypes.typeXML);
    }

    public static Object useFuture(org.ballerinalang.jvm.api.values.BFuture future) {
        return future.getResult();
    }

    public static org.ballerinalang.jvm.api.values.BFuture getFuture(Object a) {
        return (FutureValue) a;
    }
}
