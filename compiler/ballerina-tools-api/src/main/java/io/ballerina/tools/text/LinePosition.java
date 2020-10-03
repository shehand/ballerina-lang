/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package io.ballerina.tools.text;

import java.util.Objects;

/**
 * The {@code LinePosition} represents a line number and a character offset from the start of the line.
 *
 * @since 2.0.0
 */
public class LinePosition {
    private final int line;
    private final int offset;

    private LinePosition(int line, int offset) {
        this.line = line;
        this.offset = offset;
    }

    public static LinePosition from(int line, int offset) {
        return new LinePosition(line, offset);
    }

    public int line() {
        return line;
    }

    public int offset() {
        return offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LinePosition linePosition = (LinePosition) o;
        return line == linePosition.line &&
                offset == linePosition.offset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, offset);
    }

    @Override
    public String toString() {
        return line + ":" + offset;
    }
}
