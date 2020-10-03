/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.observe.nativeimpl;

import org.ballerinalang.jvm.api.BStringUtils;
import org.ballerinalang.jvm.api.values.BString;
import org.ballerinalang.jvm.types.BPackage;

import static org.ballerinalang.jvm.util.BLangConstants.BALLERINA_BUILTIN_PKG_PREFIX;

/**
 * Constants used in Ballerina Observe package.
 *
 * @since 0.980.0
 */
public final class ObserveNativeImplConstants {

    private ObserveNativeImplConstants() {
    }

    public static final String OBSERVE_PACKAGE_PATH = "ballerina/observe";
    public static final BPackage OBSERVE_PACKAGE_ID = new BPackage(BALLERINA_BUILTIN_PKG_PREFIX, "observe", "0.8.0");
    public static final String GAUGE = "Gauge";
    public static final String COUNTER = "Counter";
    public static final String SNAPSHOT = "Snapshot";
    public static final String METRIC = "Metric";
    public static final String STATISTIC_CONFIG = "StatisticConfig";
    public static final String PERCENTILE_VALUE = "PercentileValue";
    public static final String METRIC_NATIVE_INSTANCE_KEY = "__metric_native_instance__";

    public static final BString NAME_FIELD = BStringUtils.fromString("name");
    public static final BString DESCRIPTION_FIELD = BStringUtils.fromString("description");
    public static final BString TAGS_FIELD = BStringUtils.fromString("metricTags");
    public static final BString STATISTICS_CONFIG_FIELD = BStringUtils.fromString("statisticConfigs");
    public static final BString EXPIRY_FIELD = BStringUtils.fromString("timeWindow");
    public static final BString BUCKETS_FIELD = BStringUtils.fromString("buckets");
    public static final BString PERCENTILES_FIELD = BStringUtils.fromString("percentiles");
}
