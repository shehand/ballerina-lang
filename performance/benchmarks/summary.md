# Ballerina Performance Test Results

During each release, we execute various automated performance test scenarios and publish the results.

| Test Scenarios | Description |
| --- | --- |
| Passthrough HTTP service | An HTTP Service, which forwards all requests to a back-end service. |
| Passthrough HTTPS service | An HTTPS Service, which forwards all requests to an HTTPS back-end service. |
| JSON to XML transformation HTTP service | An HTTP Service, which transforms JSON requests to XML and then forwards all requests to a back-end service. |
| JSON to XML transformation HTTPS service | An HTTPS Service, which transforms JSON requests to XML and then forwards all requests to an HTTPS back-end service. |
| Passthrough HTTP2 (HTTPS) service | An HTTPS Service exposed over HTTP2 protocol, which forwards all requests to a back-end service. |

Our test client is [Apache JMeter](https://jmeter.apache.org/index.html). We test each scenario for a fixed duration of
time. We split the test results into warmup and measurement parts and use the measurement part to compute the
performance metrics.

A majority of test scenarios use a [Netty](https://netty.io/) based back-end service which echoes back any request
posted to it after a specified period of time.

We run the performance tests under different numbers of concurrent users, message sizes (payloads) and back-end service
delays.

The main performance metrics:

1. **Throughput**: The number of requests that the Ballerina service processes during a specific time interval (e.g. per second).
2. **Response Time**: The end-to-end latency for an operation of invoking a Ballerina service. The complete distribution of response times was recorded.

In addition to the above metrics, we measure the load average and several memory-related metrics.

The following are the test parameters.

| Test Parameter | Description | Values |
| --- | --- | --- |
| Scenario Name | The name of the test scenario. | Refer to the above table. |
| Heap Size | The amount of memory allocated to the application | 2G |
| Concurrent Users | The number of users accessing the application at the same time. | 1000 |
| Message Size (Bytes) | The request payload size in Bytes. | 50, 1024 |
| Back-end Delay (ms) | The delay added by the back-end service. | 0 |

The duration of each test is **900 seconds**. The warm-up period is **300 seconds**.
The measurement results are collected after the warm-up period.

A [**c5.xlarge** Amazon EC2 instance](https://aws.amazon.com/ec2/instance-types/) was used to install Ballerina.

The following are the measurements collected from each performance test conducted for a given combination of
test parameters.

| Measurement | Description |
| --- | --- |
| Error % | Percentage of requests with errors |
| Average Response Time (ms) | The average response time of a set of results |
| Standard Deviation of Response Time (ms) | The “Standard Deviation” of the response time. |
| 99th Percentile of Response Time (ms) | 99% of the requests took no more than this time. The remaining samples took at least as long as this |
| Throughput (Requests/sec) | The throughput measured in requests per second. |
| Average Memory Footprint After Full GC (M) | The average memory consumed by the application after a full garbage collection event. |

The following is the summary of performance test results collected for the measurement period.

|  Scenario Name | Concurrent Users | Message Size (Bytes) | Back-end Service Delay (ms) | Error % | Throughput (Requests/sec) | Average Response Time (ms) | Standard Deviation of Response Time (ms) | 99th Percentile of Response Time (ms) | Ballerina GC Throughput (%) | Average Ballerina Memory Footprint After Full GC (M) |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
|  Passthrough HTTP2 (HTTPS) service | 1000 | 50 | 0 | 5.98 | 6826.32 | 145.88 | 207.67 | 991 | 98.25 | 25.744 |
|  Passthrough HTTP2 (HTTPS) service | 1000 | 1024 | 0 | 2.42 | 9387.31 | 104.96 | 157.21 | 871 | 98 | 25.875 |
|  Passthrough HTTPS service | 1000 | 50 | 0 | 0.27 | 11229.12 | 84.56 | 1554.44 | 21 | 86.66 | 1455.165 |
|  Passthrough HTTPS service | 1000 | 1024 | 0 | 0.31 | 9437.73 | 100.82 | 1666.22 | 32 | 88.75 | 1435.248 |
|  Passthrough HTTP service | 1000 | 50 | 0 | 0 | 21321.09 | 46.84 | 24.2 | 143 | 96.21 |  |
|  Passthrough HTTP service | 1000 | 1024 | 0 | 0 | 19383.52 | 51.51 | 25.52 | 152 | 96.55 |  |
|  JSON to XML transformation HTTPS service | 1000 | 50 | 0 | 0.53 | 5648.39 | 168.55 | 2196.14 | 54 | 70.54 | 1648.973 |
|  JSON to XML transformation HTTPS service | 1000 | 1024 | 0 | 1.15 | 2965.69 | 326.24 | 2996.11 | 10047 | 62.56 | 1780.345 |
|  JSON to XML transformation HTTP service | 1000 | 50 | 0 | 0 | 13753.32 | 72.64 | 19.38 | 131 | 92.68 | 25.226 |
|  JSON to XML transformation HTTP service | 1000 | 1024 | 0 | 0 | 9936.91 | 100.54 | 20.95 | 157 | 91.47 | 25.322 |
