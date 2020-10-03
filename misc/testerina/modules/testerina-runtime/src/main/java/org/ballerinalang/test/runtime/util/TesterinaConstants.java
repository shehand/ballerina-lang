/*
 * Copyright (c) 2018, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 * <p>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.test.runtime.util;

/**
 * Testerina Constant Class holds constants used in runtime.
 *
 * @since 0.970.0
 */
public class TesterinaConstants {

    public static final String BALLERINA_SOURCE_ROOT = "ballerina.source.root";
    public static final String TESTERINA_TEMP_DIR = ".testerina";
    public static final String TESTERINA_TEST_SUITE = "test_suit.json";
    public static final String TESTERINA_LAUNCHER_CLASS_NAME = "org.ballerinalang.test.runtime.Main";
    public static final String CODE_COV_GENERATOR_CLASS_NAME = "org.ballerinalang.test.runtime.CoverageMain";
    public static final String TEST_RUNTIME_JAR_PREFIX = "testerina-runtime-";

    public static final String TARGET_DIR_NAME = "target";
    public static final String CACHES_DIR_NAME = "caches";
    public static final String JSON_CACHE_DIR_NAME = "json_cache";

    public static final String DOT = ".";
    public static final String ANON_ORG = "$anon";

    //Coverage constants
    public static final String BIN_DIR = "bin";
    public static final String SRC_DIR = "src";
    public static final String EXEC_FILE_NAME = "ballerina.exec";
    public static final String AGENT_FILE_NAME = "jacocoagent.jar";
    public static final String COVERAGE_DIR = "coverage";
    public static final String STATUS_FILE = "module_status.json";
    public static final String COVERAGE_FILE = "module_coverage.json";
    public static final String RESULTS_JSON_FILE = "test_results.json";
    public static final String RERUN_TEST_JSON_FILE = "rerun_test.json";
    public static final String RESULTS_HTML_FILE = "index.html";
    public static final String REPORT_DIR_NAME = "report";
    public static final String REPORT_ZIP_NAME = REPORT_DIR_NAME + ".zip";
    public static final String REPORT_DATA_PLACEHOLDER = "__data__";
    public static final String FILE_PROTOCOL = "file://";
}
