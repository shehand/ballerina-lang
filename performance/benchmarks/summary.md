# Ballerina Performance Test Results

During each release, we execute various automated performance test scenarios and publish the results.

| Test Scenarios | Description |
| --- | --- |
| Passthrough HTTP2 (HTTPS) service | An HTTPS Service exposed over HTTP2 protocol, which forwards all requests to a HTTP2 (HTTPS) back-end service. |
| Passthrough HTTP2 (HTTPS) downgrade service | An HTTPS Service exposed over HTTP2 protocol, which forwards all requests to a HTTPS back-end service. |

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
| Concurrent Users | The number of users accessing the application at the same time. | 100, 300, 1000 |
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
|  Passthrough HTTP2 (HTTPS) service | 100 | 50 | 0 | 0 | 15241.21 | 6.35 | 3.81 | 21 | 99.6 | 24.723 |
|  Passthrough HTTP2 (HTTPS) service | 100 | 1024 | 0 | 1.16 | 14247.99 | 6.77 | 3.69 | 20 | 99.63 | 24.711 |
|  Passthrough HTTP2 (HTTPS) service | 300 | 50 | 0 | 0 | 15534.54 | 18.91 | 7.67 | 48 | 99.16 | 25.149 |
|  Passthrough HTTP2 (HTTPS) service | 300 | 1024 | 0 | 2 | 14862.72 | 19.67 | 8.13 | 49 | 99.18 | 25.129 |
|  Passthrough HTTP2 (HTTPS) service | 1000 | 50 | 0 | 0 | 14967.56 | 66.35 | 15.77 | 122 | 97.56 | 25.878 |
|  Passthrough HTTP2 (HTTPS) service | 1000 | 1024 | 0 | 2.19 | 14470.58 | 67.96 | 20.01 | 141 | 97.7 | 25.977 |
|  Passthrough HTTP2 (HTTPS) downgrade service | 100 | 50 | 0 | 0 | 14890.22 | 6.53 | 7.99 | 42 | 99.5 | 24.726 |
|  Passthrough HTTP2 (HTTPS) downgrade service | 100 | 1024 | 0 | 0 | 11662.02 | 8.36 | 6.16 | 30 | 99.55 | 24.7 |
|  Passthrough HTTP2 (HTTPS) downgrade service | 300 | 50 | 0 | 0 | 2135.05 | 140.2 | 625.08 | 3583 | 98.84 | 51.706 |
|  Passthrough HTTP2 (HTTPS) downgrade service | 300 | 1024 | 0 | 0 | 1562.59 | 191.05 | 526.73 | 2719 | 98.87 | 54.425 |
|  Passthrough HTTP2 (HTTPS) downgrade service | 1000 | 50 | 0 | 0 | 1188.94 | 838.72 | 1635.49 | 7999 | 97.89 | 104.915 |
|  Passthrough HTTP2 (HTTPS) downgrade service | 1000 | 1024 | 0 | 0 | 1071.3 | 928.91 | 1578.89 | 7167 | 97.81 | 109.527 |
