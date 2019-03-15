# Ballerina Performance Test Results

During each release, we execute various automated performance test scenarios and publish the results.

| Test Scenarios | Description |
| --- | --- |
| Passthrough HTTP service (h1c -> h1c) | An HTTP Service, which forwards all requests to an HTTP back-end service. |
| Passthrough HTTPS service (h1 -> h1) | An HTTPS Service, which forwards all requests to an HTTPS back-end service. |
| Passthrough HTTP/2(over TLS) service (h2 -> h2) | An HTTPS Service exposed over HTTP/2 protocol, which forwards all requests to an HTTP/2(over TLS) back-end service. |
| Passthrough HTTP/2(over TLS) service (h2 -> h1) | An HTTPS Service exposed over HTTP/2 protocol, which forwards all requests to an HTTPS back-end service. |
| Passthrough HTTP/2(over TLS) service (h2 -> h1c) | An HTTPS Service exposed over HTTP/2 protocol, which forwards all requests to an HTTP back-end service. |
| HTTP/2 client downgrade service (h2 -> h1) | An HTTP/2(with TLS) client, sends requests to an HTTP/1.1(with TLS) back-end service. With ALPN negotiation, the client connection is downgraded to HTTP/1.1(with TLS). |
| HTTP/2 server downgrade service (h1 -> h2) | An HTTP/2(with TLS) server, accepts requests from an HTTP/1.1(with TLS) client which downgrades the connection to HTTP/1.1(with TLS). |

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
|  Passthrough HTTP service (h1c -> h1c) | 100 | 50 | 0 | 0 | 19866.03 | 5 | 6.97 | 40 | 99.45 | 15.902 |
|  Passthrough HTTP service (h1c -> h1c) | 100 | 1024 | 0 | 0 | 18061.72 | 5.5 | 6.65 | 38 | 99.49 | 16.324 |
|  Passthrough HTTP service (h1c -> h1c) | 300 | 50 | 0 | 0 | 20867.56 | 14.33 | 11.84 | 69 | 98.75 | 16.839 |
|  Passthrough HTTP service (h1c -> h1c) | 300 | 1024 | 0 | 0 | 19216.01 | 15.56 | 12.61 | 74 | 98.83 | 16.483 |
|  Passthrough HTTP service (h1c -> h1c) | 1000 | 50 | 0 | 0 | 19099.9 | 52.29 | 27.12 | 159 | 96.5 | 17.216 |
|  Passthrough HTTP service (h1c -> h1c) | 1000 | 1024 | 0 | 0 | 18414.56 | 54.23 | 28.2 | 167 | 96.64 | 17.302 |
|  Passthrough HTTPS service (h1 -> h1) | 100 | 50 | 0 | 0 | 16894.98 | 5.88 | 6.51 | 30 | 99.44 | 23.201 |
|  Passthrough HTTPS service (h1 -> h1) | 100 | 1024 | 0 | 0 | 11792.08 | 8.43 | 6.15 | 28 | 99.54 | 23.193 |
|  Passthrough HTTPS service (h1 -> h1) | 300 | 50 | 0 | 0 | 17321.46 | 17.26 | 12.48 | 68 | 98.81 | 23.557 |
|  Passthrough HTTPS service (h1 -> h1) | 300 | 1024 | 0 | 0 | 12009.39 | 24.91 | 11.74 | 62 | 99.03 | 23.845 |
|  Passthrough HTTPS service (h1 -> h1) | 1000 | 50 | 0 | 0 | 14952.13 | 66.8 | 29.92 | 165 | 96.85 | 24.681 |
|  Passthrough HTTPS service (h1 -> h1) | 1000 | 1024 | 0 | 0 | 11339.16 | 88.1 | 29.48 | 172 | 97.35 | 24.734 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h2) | 100 | 50 | 0 | 0 | 16043.07 | 6.07 | 8.01 | 43 | 99.54 | 24.271 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h2) | 100 | 1024 | 0 | 0 | 15388.52 | 6.22 | 7.51 | 40 | 99.56 | 24.225 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h2) | 300 | 50 | 0 | 0 | 16955.12 | 17.28 | 13.26 | 73 | 99.09 | 24.437 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h2) | 300 | 1024 | 0 | 0 | 16178.32 | 17.66 | 13.12 | 70 | 99.16 | 24.735 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h2) | 1000 | 50 | 0 | 0 | 16498.51 | 60.05 | 30.81 | 165 | 97.34 | 25.316 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h2) | 1000 | 1024 | 0 | 0 | 15512.25 | 62.49 | 30.59 | 163 | 97.56 | 25.314 |
