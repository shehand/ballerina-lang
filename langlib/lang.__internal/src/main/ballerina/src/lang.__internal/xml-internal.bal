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

# Get elements matching at least one of `elemNames`
#
# + x - The xml source
# + elemNames - ualified name of the elements to filter
# + return - All elements that match elemNames
public function getElements(xml x, string... elemNames) returns xml = external;

# Get childElements matching `elemNames` and childIndex for each children sequence.
# xml x = xml `<a><a0>x</a0><a0>y</a0></a><b><a0>j</a0><a0>k</a0></b>`;
# x.getFilteredChildrenFlat(0, "a0") => <a0>x</a0><a0>j</a0>
# x.getFilteredChildrenFlat(-1) => <a0>x</a0><a0>y</a0><a0>j</a0><a0>k</a0>
#
# + x - The xml source
# + index - child index to select from each child sequence, -1 to select all elements
# + elemNames - Qualified name of the elements to filter,
# + return - All child elements matching `index` condition and  `elemNames` condition.
public function getFilteredChildrenFlat(xml x, int index, string... elemNames) returns xml = external;

# Searches in children recursively for elements matching the qualified name and returns a sequence containing them
# all. Does not search within a matched result.
#
# + x - The xml source
# + qname - Qualified name of the element
# + return - All the descendants that matches the given qualified name, as a sequence
public function selectDescendants(xml x, string... qname) returns xml = external;

# Return attribute matching expanded attribute name
#
# + x - The xml value
# + attributeName - Attribute name in expanded from
# + isOptionalAccess - Is this a optoinal access expression or not
# + return - Attribute value
public function getAttribute(xml x, string attributeName, boolean isOptionalAccess) returns string|error? = external;

# Return name of the element if `x` is a element or nil if element name is not set, else error.
#
# + x - The xml value
# + return - Element name
public function getElementNameNilLifting(xml x) returns string|error? = external;

# Functional constructor for xml:Element subtype.
#
# + name - Name of element
# + attributeMap - Optional attribute map
# + children - Optional children
# + return - Constructed Element value
public function elementCtor(string name, map<string> attributeMap = {}, xml children = textCtor()) returns xml = external;

# Functional constructor for xml:ProcessingInstruction subtype.
#
# + target - Target potion
# + content - Content potion
# + return - Constructed ProcessingInstruction value
public function processingInstructionCtor(string target, string content = "") returns xml = external;

# Functional constructor for xml:Comment subtype.
#
# + content - Comment content
# + return - Constructed Comment value
public function commentCtor(string content = "") returns xml = external;

# Functional constructor for xml:Text subtype.
#
# + characters - Text content
# + return - Constructed Text value
public function textCtor(string characters = "") returns xml = external;
