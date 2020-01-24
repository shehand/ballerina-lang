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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.messaging.kafka.nativeimpl.consumer;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.ballerinalang.jvm.scheduling.Scheduler;
import org.ballerinalang.jvm.values.ObjectValue;
import org.ballerinalang.jvm.values.api.BArray;
import org.ballerinalang.messaging.kafka.observability.KafkaMetricsUtil;
import org.ballerinalang.messaging.kafka.observability.KafkaObservabilityConstants;
import org.ballerinalang.messaging.kafka.observability.KafkaTracingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;

import static org.ballerinalang.messaging.kafka.utils.KafkaConstants.ALIAS_DURATION;
import static org.ballerinalang.messaging.kafka.utils.KafkaConstants.CONSUMER_ERROR;
import static org.ballerinalang.messaging.kafka.utils.KafkaConstants.DURATION_UNDEFINED_VALUE;
import static org.ballerinalang.messaging.kafka.utils.KafkaConstants.NATIVE_CONSUMER;
import static org.ballerinalang.messaging.kafka.utils.KafkaConstants.NATIVE_CONSUMER_CONFIG;
import static org.ballerinalang.messaging.kafka.utils.KafkaUtils.createKafkaError;
import static org.ballerinalang.messaging.kafka.utils.KafkaUtils.getDefaultApiTimeout;
import static org.ballerinalang.messaging.kafka.utils.KafkaUtils.getIntFromLong;
import static org.ballerinalang.messaging.kafka.utils.KafkaUtils.getPartitionToMetadataMap;

/**
 * Native function commits given offsets of consumer to offset topic.
 */
public class CommitOffset {

    private static final Logger logger = LoggerFactory.getLogger(CommitOffset.class);

    public static Object commitOffset(ObjectValue consumerObject, BArray offsets, long duration) {
        KafkaTracingUtil.traceResourceInvocation(Scheduler.getStrand(), consumerObject);
        KafkaConsumer kafkaConsumer = (KafkaConsumer) consumerObject.getNativeData(NATIVE_CONSUMER);

        Properties consumerProperties = (Properties) consumerObject.getNativeData(NATIVE_CONSUMER_CONFIG);
        int defaultApiTimeout = getDefaultApiTimeout(consumerProperties);
        int apiTimeout = getIntFromLong(duration, logger, ALIAS_DURATION);
        Map<TopicPartition, OffsetAndMetadata> partitionToMetadataMap = getPartitionToMetadataMap(offsets);
        try {
            if (apiTimeout > DURATION_UNDEFINED_VALUE) { // API timeout should given the priority over the default value
                consumerCommitSyncWithDuration(kafkaConsumer, partitionToMetadataMap, apiTimeout);
            } else if (defaultApiTimeout > DURATION_UNDEFINED_VALUE) {
                consumerCommitSyncWithDuration(kafkaConsumer, partitionToMetadataMap, defaultApiTimeout);
            } else {
                kafkaConsumer.commitSync(partitionToMetadataMap);
            }
        } catch (KafkaException e) {
            KafkaMetricsUtil.reportConsumerError(consumerObject, KafkaObservabilityConstants.ERROR_TYPE_COMMIT);
            return createKafkaError("Failed to commit the offset: " + e.getMessage(), CONSUMER_ERROR);
        }
        return null;
    }

    private static void consumerCommitSyncWithDuration(KafkaConsumer consumer,
                                                       Map<TopicPartition, OffsetAndMetadata> metadataMap,
                                                       long timeout) {

        Duration duration = Duration.ofMillis(timeout);
        consumer.commitSync(metadataMap, duration);
    }
}
