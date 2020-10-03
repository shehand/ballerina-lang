/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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
package org.ballerinalang.jvm;

import org.ballerinalang.jvm.api.BErrorCreator;
import org.ballerinalang.jvm.api.BStringUtils;
import org.ballerinalang.jvm.api.values.BString;
import org.ballerinalang.jvm.util.exceptions.BallerinaErrorReasons;

/**
 * Common utility methods used for arithmatic operations.
 *
 * @since 1.0
 */
public class MathUtils {

    private static final BString DIVIDE_BY_ZERO_ERROR = BStringUtils.fromString(" / by zero");

    private static final BString INT_RANGE_OVERFLOW_ERROR = BStringUtils.fromString(" int range overflow");

    public static long divide(long numerator, long denominator) {
        try {
            if (numerator == Long.MIN_VALUE && denominator == -1) {
                // a panic will occur on division by zero or overflow,
                // which happens if the first operand is -2^63 and the second operand is -1
                throw BErrorCreator.createError(BallerinaErrorReasons.NUMBER_OVERFLOW, INT_RANGE_OVERFLOW_ERROR);
            }
            return numerator / denominator;
        } catch (ArithmeticException e) {
            if (denominator == 0) {
                throw BErrorCreator.createError(BallerinaErrorReasons.DIVISION_BY_ZERO_ERROR, DIVIDE_BY_ZERO_ERROR);
            } else {
                throw BErrorCreator.createError(BallerinaErrorReasons.ARITHMETIC_OPERATION_ERROR,
                                                BStringUtils.fromString(e.getMessage()));
            }
        }
    }

    public static long remainder(long numerator, long denominator) {
        try {
            return numerator % denominator;
        } catch (ArithmeticException e) {
            if (denominator == 0) {
                throw BErrorCreator.createError(BallerinaErrorReasons.DIVISION_BY_ZERO_ERROR, DIVIDE_BY_ZERO_ERROR);
            } else {
                throw BErrorCreator.createError(BallerinaErrorReasons.ARITHMETIC_OPERATION_ERROR,
                                                BStringUtils.fromString(e.getMessage()));
            }
        }
    }
}
