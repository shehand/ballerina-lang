/*
*   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.ballerinalang.test.expressions.stamp;

import org.ballerinalang.model.types.BErrorType;
import org.ballerinalang.model.values.BError;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.test.util.BAssertUtil;
import org.ballerinalang.test.util.BCompileUtil;
import org.ballerinalang.test.util.BRunUtil;
import org.ballerinalang.test.util.CompileResult;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Negative test cases for stamping variables.
 *
 * @since 0.985.0
 */
public class StampInbuiltFunctionNegativeTest {

    private CompileResult compileResult;
    private CompileResult recordNegativeTestCompileResult;
    private CompileResult jsonNegativeTestCompileResult;
    private CompileResult xmlNegativeTestCompileResult;
    private CompileResult mapNegativeTestCompileResult;
    private CompileResult objectNegativeTestCompileResult;
    private CompileResult arrayNegativeTestCompileResult;
    private CompileResult tupleNegativeTestCompileResult;
    private CompileResult unionNegativeTestCompileResult;

    @BeforeClass
    public void setup() {
        compileResult = BCompileUtil.compile("test-src/expressions/stamp/negative/stamp-expr-negative-test.bal");
        recordNegativeTestCompileResult = BCompileUtil.
                compile("test-src/expressions/stamp/negative/record-stamp-expr-negative-test.bal");
        jsonNegativeTestCompileResult = BCompileUtil.
                compile("test-src/expressions/stamp/negative/json-stamp-expr-negative-test.bal");
        xmlNegativeTestCompileResult = BCompileUtil.
                compile("test-src/expressions/stamp/negative/xml-stamp-expr-negative-test.bal");
        mapNegativeTestCompileResult = BCompileUtil.
                compile("test-src/expressions/stamp/negative/map-stamp-expr-negative-test.bal");
        objectNegativeTestCompileResult = BCompileUtil.
                compile("test-src/expressions/stamp/negative/object-stamp-expr-negative-test.bal");
        arrayNegativeTestCompileResult = BCompileUtil.
                compile("test-src/expressions/stamp/negative/array-stamp-expr-negative-test.bal");
        tupleNegativeTestCompileResult = BCompileUtil.
                compile("test-src/expressions/stamp/negative/tuple-stamp-expr-negative-test.bal");
        unionNegativeTestCompileResult = BCompileUtil.
                compile("test-src/expressions/stamp/negative/union-stamp-expr-negative-test.bal");
    }

    //----------------------------- NegativeTest cases ------------------------------------------------------

    @Test
    public void testStampNegativeTest() {

        int index = 0;
        Assert.assertEquals(compileResult.getErrorCount(), 7);
        BAssertUtil.validateError(compileResult, index++, "too many arguments in call to 'constructFrom()'", 50, 24);
        BAssertUtil.validateError(compileResult, index++,
                                  "incompatible types: expected 'typedesc<anydata>', found 'typedesc'", 64, 26);
        BAssertUtil
                .validateError(compileResult, index++, "incompatible types: expected 'anydata', found 'any'", 71, 54);
        BAssertUtil.validateError(compileResult, index++, "undefined symbol 'TestType'", 79, 30);
        BAssertUtil.validateError(compileResult, index++,
                                  "incompatible types: expected 'typedesc<anydata>', found 'typedesc<EmployeeObject>'",
                                  87, 40);
        BAssertUtil.validateError(compileResult, index++,
                                  "incompatible types: expected 'typedesc<anydata>', found 'typedesc<map>'", 95, 31);
        BAssertUtil.validateError(compileResult, index++,
                                  "incompatible types: expected 'anydata', found 'ExtendedEmployee'", 103, 56);
    }

    //----------------------------- Object NegativeTest cases ------------------------------------------------------

    @Test
    public void testObjectNegativeTest() {
        int index = 0;
        BAssertUtil.validateError(objectNegativeTestCompileResult, index++,
                                  "incompatible types: expected 'anydata', found 'PersonObj'", 85, 54);
        BAssertUtil.validateError(objectNegativeTestCompileResult, index++,
                                  "incompatible types: expected 'anydata', found 'PersonObj'", 92, 47);
        BAssertUtil.validateError(objectNegativeTestCompileResult, index++,
                                  "incompatible types: expected 'anydata', found 'PersonObj'", 99, 44);
        BAssertUtil.validateError(objectNegativeTestCompileResult, index++,
                                  "incompatible types: expected 'typedesc<anydata>', found 'typedesc<map>'", 106, 31);
        BAssertUtil.validateError(objectNegativeTestCompileResult, index++,
                                  "incompatible types: expected 'anydata', found 'PersonObj'", 106, 54);
        BAssertUtil.validateError(objectNegativeTestCompileResult, index++,
                                  "incompatible types: expected 'typedesc<anydata>', found 'typedesc<any[]>'", 113, 28);
        BAssertUtil.validateError(objectNegativeTestCompileResult, index++,
                                  "incompatible types: expected 'anydata', found 'PersonObj'", 113, 48);
        BAssertUtil.validateError(objectNegativeTestCompileResult, index++,
                                  "incompatible types: expected 'anydata', found 'PersonObj'", 120, 65);
        BAssertUtil.validateError(objectNegativeTestCompileResult, index++,
                                  "incompatible types: expected 'anydata', found 'PersonObj'", 127, 28);
        BAssertUtil.validateError(objectNegativeTestCompileResult, index++,
                                  "incompatible types: expected 'typedesc<anydata>', found 'typedesc<PersonObj>'", 128,
                                  33);
        BAssertUtil.validateError(objectNegativeTestCompileResult, index++,
                                  "incompatible types: expected 'typedesc<anydata>', found 'typedesc<EmployeeObject>'",
                                  136, 40);
        BAssertUtil.validateError(objectNegativeTestCompileResult, index++,
                                  "incompatible types: expected 'typedesc<anydata>', found 'typedesc<EmployeeObject>'",
                                  144, 40);
        BAssertUtil.validateError(objectNegativeTestCompileResult, index++,
                                  "incompatible types: expected '(EmployeeObj|error)', found '(EmployeeObject|error)'",
                                  145, 12);
        BAssertUtil.validateError(objectNegativeTestCompileResult, index++,
                                  "incompatible types: expected 'typedesc<anydata>', found 'typedesc<BookObject>'", 152,
                                  36);
        BAssertUtil.validateError(objectNegativeTestCompileResult, index++,
                                  "incompatible types: expected 'typedesc<anydata>', found 'typedesc<IntObject>'", 158,
                                  35);
        BAssertUtil.validateError(objectNegativeTestCompileResult, index++,
                                  "incompatible types: expected 'typedesc<anydata>', found 'typedesc<TeacherObj>'", 166,
                                  36);
        BAssertUtil.validateError(objectNegativeTestCompileResult, index,
                                  "incompatible types: expected 'typedesc<anydata>', found 'typedesc<EmployeeObj>'",
                                  174, 37);
    }

    //----------------------------- Record NegativeTest cases ------------------------------------------------------

    @Test
    public void stampRecordToXML() {
        BValue[] results = BRunUtil.invoke(recordNegativeTestCompileResult, "stampRecordToXML");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'Employee' value cannot be converted to 'xml'");
    }

    @Test
    public void stampOpenRecordToClosedRecord() {
        BValue[] results = BRunUtil.invoke(recordNegativeTestCompileResult, "stampOpenRecordToClosedRecord");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'Teacher' value cannot be converted to 'Employee'");
    }

    @Test
    public void stampClosedRecordToClosedRecord() {
        BValue[] results = BRunUtil.invoke(recordNegativeTestCompileResult, "stampClosedRecordToClosedRecord");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'Person' value cannot be converted to 'Student'");
    }

    @Test
    public void stampClosedRecordToMap() {
        BValue[] results = BRunUtil.invoke(recordNegativeTestCompileResult, "stampClosedRecordToMap");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'Person' value cannot be converted to 'map<string>'");
    }

    @Test
    public void stampRecordToArray() {
        BValue[] results = BRunUtil.invoke(recordNegativeTestCompileResult, "stampRecordToArray");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'Employee' value cannot be converted to 'string[]'");
    }

    @Test
    public void stampRecordToTuple() {
        BValue[] results = BRunUtil.invoke(recordNegativeTestCompileResult, "stampRecordToTuple");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'Employee' value cannot be converted to '[string,string]'");
    }

    //----------------------------- JSON NegativeTest cases ------------------------------------------------------

    @Test
    public void stampJSONToXML() {
        BValue[] results = BRunUtil.invoke(jsonNegativeTestCompileResult, "stampJSONToXML");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'map<json>' value cannot be converted to 'xml'");
    }

    @Test
    public void stampJSONToTuple() {
        BValue[] results = BRunUtil.invoke(jsonNegativeTestCompileResult, "stampJSONToTuple");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'map<json>' value cannot be converted to '[string,string]'");
    }

    //----------------------------- XML NegativeTest cases ------------------------------------------------------

    @Test
    public void stampXMLToRecord() {
        BValue[] results = BRunUtil.invoke(xmlNegativeTestCompileResult, "stampXMLToRecord");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'xml' value cannot be converted to 'BookRecord'");
    }

    @Test
    public void stampXMLToJson() {
        BValue[] results = BRunUtil.invoke(xmlNegativeTestCompileResult, "stampXMLToJson");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'xml' value cannot be converted to 'json'");
    }

    @Test
    public void stampXMLToMap() {
        BValue[] results = BRunUtil.invoke(xmlNegativeTestCompileResult, "stampXMLToMap");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'xml' value cannot be converted to 'map<anydata>'");
    }

    @Test
    public void stampXMLToArray() {
        BValue[] results = BRunUtil.invoke(xmlNegativeTestCompileResult, "stampXMLToArray");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'xml' value cannot be converted to 'BookRecord[]'");
    }

    @Test
    public void stampXMLToTuple() {
        BValue[] results = BRunUtil.invoke(xmlNegativeTestCompileResult, "stampXMLToTuple");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'xml' value cannot be converted to '[string,string]'");
    }

    //----------------------------- Map NegativeTest cases ------------------------------------------------------
    
    @Test
    public void stampMapToXML() {
        BValue[] results = BRunUtil.invoke(mapNegativeTestCompileResult, "stampMapToXML");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'map<anydata>' value cannot be converted to 'xml'");
    }

    @Test
    public void stampMapToArray() {
        BValue[] results = BRunUtil.invoke(mapNegativeTestCompileResult, "stampMapToArray");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'map<anydata>' value cannot be converted to 'string[]'");
    }

    @Test
    public void stampMapToTuple() {
        BValue[] results = BRunUtil.invoke(mapNegativeTestCompileResult, "stampMapToTuple");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'map<anydata>' value cannot be converted to '[string,string]'");
    }
    
    //----------------------------- Array NegativeTest cases ------------------------------------------------------
    
    @Test
    public void stampAnyArrayToRecord() {
        BValue[] results = BRunUtil.invoke(arrayNegativeTestCompileResult, "stampAnyArrayToRecord");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'anydata[]' value cannot be converted to 'Employee'");
    }

    @Test
    public void stampAnyArrayToXML() {
        BValue[] results = BRunUtil.invoke(arrayNegativeTestCompileResult, "stampAnyArrayToXML");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'anydata[]' value cannot be converted to 'xml'");
    }

    //----------------------------- Tuple NegativeTest cases ------------------------------------------------------

    @Test
    public void stampTupleToRecord() {
        BValue[] results = BRunUtil.invoke(tupleNegativeTestCompileResult, "stampTupleToRecord");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'[string,string,string]' value cannot be converted to 'Employee'");
    }
    
    @Test
    public void stampTupleToJSON() {
        BValue[] results = BRunUtil.invoke(tupleNegativeTestCompileResult, "stampTupleToJSON");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'[string,string,string]' value cannot be converted to 'json'");
    }
    
    @Test
    public void stampTupleToXML() {
        BValue[] results = BRunUtil.invoke(tupleNegativeTestCompileResult, "stampTupleToXML");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'[string,string,string]' value cannot be converted to 'xml'");
    }

    @Test
    public void stampTupleToMap() {
        BValue[] results = BRunUtil.invoke(tupleNegativeTestCompileResult, "stampTupleToMap");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'[string,string,string]' value cannot be converted to 'map<anydata>'");
    }
    
    //----------------------------- Union NegativeTest cases ------------------------------------------------------

    @Test
    public void stampUnionToXML() {
        BValue[] results = BRunUtil.invoke(unionNegativeTestCompileResult, "stampUnionToXML");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'xml' value cannot be converted to 'Employee'");
    }

    @Test
    public void stampUnionToConstraintMapToUnionNegative() {
        BValue[] results = BRunUtil.invoke(unionNegativeTestCompileResult, "stampUnionToConstraintMapToUnionNegative");
        BValue error = results[0];
        Assert.assertEquals(error.getType().getClass(), BErrorType.class);
        Assert.assertEquals(((BMap<String, BString>) ((BError) results[0]).getDetails()).get("message").stringValue(),
                            "'int' value cannot be converted to 'float|decimal|[string,int]': ambiguous target type");
    }
}
