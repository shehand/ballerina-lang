# Ballerina Performance Test Results

During each release, we execute various automated performance test scenarios and publish the results.

| Test Scenarios | Description |
| --- | --- |
| Passthrough HTTP2 (HTTPS) service | An HTTPS Service exposed over HTTP2 protocol, which forwards all requests to a HTTP2 (HTTPS) back-end service. |
| Passthrough HTTP2 (HTTPS) downgrade service | An HTTPS Service exposed over HTTP2 protocol, which forwards all requests to a HTTPS back-end service. |
| Passthrough HTTP2 downgrade service | An HTTPS Service exposed over HTTP2 protocol, which forwards all requests to a HTTP back-end service. |

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
|  Passthrough HTTP2 (HTTPS) service | 100 | 50 | 0 | 0 | 15446.6 | 6.26 | 6.5 | 24 | 99.53 | 24.593 |
|  Passthrough HTTP2 (HTTPS) service | 100 | 1024 | 0 | 8.73 | 300.42 | 3.13 | 51.27 | 5 | 99.96 | 24.695 |
|  Passthrough HTTP2 (HTTPS) service | 300 | 50 | 0 | 0 | 16251.55 | 18 | 15.59 | 69 | 99.07 | 25.074 |
|  Passthrough HTTP2 (HTTPS) service | 300 | 1024 | 0 | 2.18 | 1127.88 | 3.89 | 57.84 | 8 | 99.93 | 25.171 |
|  Passthrough HTTP2 (HTTPS) service | 1000 | 50 | 0 | 0 | 16001.37 | 61.69 | 35.6 | 178 | 97.47 | 26.08 |
|  Passthrough HTTP2 (HTTPS) service | 1000 | 1024 | 0 | 10.39 | 673.18 | 56.3 | 833.98 | 36 | 99.87 | 26.017 |
|  Passthrough HTTP2 (HTTPS) downgrade service | 100 | 50 | 0 | 0 | 14493.84 | 6.71 | 7.76 | 39 | 99.5 | 24.617 |
|  Passthrough HTTP2 (HTTPS) downgrade service | 100 | 1024 | 0 | 0 | 11386.73 | 8.58 | 6.29 | 30 | 99.54 | 24.599 |
|  Passthrough HTTP2 (HTTPS) downgrade service | 300 | 50 | 0 | 0 | 1986.58 | 150.66 | 657.37 | 3775 | 98.91 | 54.546 |
|  Passthrough HTTP2 (HTTPS) downgrade service | 300 | 1024 | 0 | 0 | 1586.86 | 188.85 | 536.63 | 2687 | 98.83 | 54.071 |
|  Passthrough HTTP2 (HTTPS) downgrade service | 1000 | 50 | 0 | 0 | 1109.99 | 898.38 | 1715.55 | 8031 | 97.82 | 105.861 |
|  Passthrough HTTP2 (HTTPS) downgrade service | 1000 | 1024 | 0 | 0 | 1102.32 | 903.36 | 1585.59 | 7039 | 97.83 | 113.03 |
|  Passthrough HTTP2 downgrade service | 100 | 50 | 0 | 0 | 15372.98 | 6.33 | 8.59 | 49 | 99.53 | 24.612 |
|  Passthrough HTTP2 downgrade service | 100 | 1024 | 0 | 0 | 14454.52 | 6.7 | 8.54 | 47 | 99.57 | 24.587 |
|  Passthrough HTTP2 downgrade service | 300 | 50 | 0 | 0 | 15758.07 | 18.61 | 15.65 | 81 | 98.95 | 25.104 |
|  Passthrough HTTP2 downgrade service | 300 | 1024 | 0 | 0 | 14717.28 | 19.83 | 15.73 | 79 | 99.07 | 25.062 |
|  Passthrough HTTP2 downgrade service | 1000 | 50 | 0 | 5.14 | 6706.65 | 148.67 | 216.78 | 1047 | 98.17 | 26.116 |
|  Passthrough HTTP2 downgrade service | 1000 | 1024 | 0 | 1.62 | 9578.61 | 103.36 | 155.03 | 903 | 97.93 | 25.777 |
