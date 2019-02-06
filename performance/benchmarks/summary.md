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
|  Passthrough HTTP2 (HTTPS) service | 1000 | 50 | 0 | 6.11 | 6584.49 | 150.79 | 215.73 | 1007 | 98.28 | 25.843 |
|  Passthrough HTTP2 (HTTPS) service | 1000 | 1024 | 0 | 2.94 | 8482.23 | 116.21 | 173.29 | 935 | 98.08 | 25.896 |
|  Passthrough HTTPS service | 1000 | 50 | 0 | 0.26 | 11491.14 | 82.74 | 1540.16 | 23 | 88.7 | 1436.236 |
|  Passthrough HTTPS service | 1000 | 1024 | 0 | 0.31 | 9294.77 | 102.41 | 1679.62 | 33 | 88.45 | 1436.976 |
|  Passthrough HTTP service | 1000 | 50 | 0 | 0 | 20151.32 | 49.55 | 25.72 | 154 | 96.43 |  |
|  Passthrough HTTP service | 1000 | 1024 | 0 | 0 | 19107.29 | 52.25 | 25.49 | 154 | 96.55 |  |
|  JSON to XML transformation HTTPS service | 1000 | 50 | 0 | 0.56 | 5374.43 | 176.88 | 2249.26 | 59 | 70.85 | 1651.044 |
|  JSON to XML transformation HTTPS service | 1000 | 1024 | 0 | 1.18 | 2848.24 | 339.23 | 3064.86 | 30079 | 62.38 | 1784.69 |
|  JSON to XML transformation HTTP service | 1000 | 50 | 0 | 0 | 13512.2 | 73.91 | 19.71 | 133 | 92.84 | 25.251 |
|  JSON to XML transformation HTTP service | 1000 | 1024 | 0 | 0 | 9631.32 | 103.72 | 21.83 | 163 | 91.67 | 25.315 |
