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
|  Passthrough HTTP service (h1c -> h1c) | 100 | 50 | 0 | 0 | 18396.1 | 5.38 | 6.24 | 34 | 99.49 | 15.421 |
|  Passthrough HTTP service (h1c -> h1c) | 100 | 1024 | 0 | 0 | 17188.14 | 5.77 | 7.25 | 44 | 99.51 | 16.354 |
|  Passthrough HTTP service (h1c -> h1c) | 300 | 50 | 0 | 0 | 19965.34 | 14.97 | 12.04 | 71 | 98.79 | 16.389 |
|  Passthrough HTTP service (h1c -> h1c) | 300 | 1024 | 0 | 0 | 18276.08 | 16.36 | 13.39 | 78 | 98.87 | 16.725 |
|  Passthrough HTTP service (h1c -> h1c) | 1000 | 50 | 0 | 0 | 17882.11 | 55.86 | 28.14 | 164 | 96.73 | 17.459 |
|  Passthrough HTTP service (h1c -> h1c) | 1000 | 1024 | 0 | 0 | 17454.81 | 57.22 | 28.39 | 167 | 96.79 | 17.384 |
|  Passthrough HTTPS service (h1 -> h1) | 100 | 50 | 0 | 0 | 16033.72 | 6.2 | 8.01 | 39 | 99.45 | 23.202 |
|  Passthrough HTTPS service (h1 -> h1) | 100 | 1024 | 0 | 0 | 11313.59 | 8.79 | 6.86 | 32 | 99.56 | 23.214 |
|  Passthrough HTTPS service (h1 -> h1) | 300 | 50 | 0 | 0 | 16222.17 | 18.43 | 13.72 | 76 | 98.89 | 23.611 |
|  Passthrough HTTPS service (h1 -> h1) | 300 | 1024 | 0 | 0 | 11572.93 | 25.86 | 12.25 | 66 | 99.07 | 23.627 |
|  Passthrough HTTPS service (h1 -> h1) | 1000 | 50 | 0 | 0 | 14427.56 | 69.23 | 31.14 | 174 | 96.88 | 24.795 |
|  Passthrough HTTPS service (h1 -> h1) | 1000 | 1024 | 0 | 0 | 10928.88 | 91.39 | 31.15 | 182 | 97.42 | 24.729 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h1c) | 100 | 50 | 0 | 0 | 14833.65 | 6.6 | 7.71 | 44 | 99.51 | 24.193 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h1c) | 100 | 1024 | 0 | 0 | 14285.43 | 6.73 | 7.85 | 42 | 99.53 | 24.25 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h1c) | 300 | 50 | 0 | 0 | 15311.94 | 19.26 | 14.35 | 76 | 98.74 | 24.682 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h1c) | 300 | 1024 | 0 | 0 | 14811.54 | 19.57 | 13.72 | 75 | 98.71 | 24.717 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h1c) | 1000 | 50 | 0 | 0 | 14240.68 | 69.72 | 30.27 | 173 | 96.91 | 25.362 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h1c) | 1000 | 1024 | 0 | 0 | 13701 | 71.74 | 30.79 | 176 | 97.01 | 25.388 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h1) | 100 | 50 | 0 | 0 | 13296.37 | 7.36 | 6.46 | 30 | 99.5 | 24.229 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h1) | 100 | 1024 | 0 | 0 | 10837.71 | 9.05 | 6.64 | 32 | 99.53 | 24.288 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h1) | 300 | 50 | 0 | 0 | 13680.68 | 21.64 | 13.1 | 72 | 98.89 | 24.456 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h1) | 300 | 1024 | 0 | 0 | 11295.28 | 26.05 | 12.61 | 68 | 99 | 24.455 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h1) | 1000 | 50 | 0 | 0 | 12847.41 | 77.39 | 31.93 | 184 | 96.9 | 25.272 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h1) | 1000 | 1024 | 0 | 0 | 10561.24 | 93.66 | 32.32 | 191 | 97.05 | 25.389 |
|  HTTP/2 client downgrade service (h2 -> h1) | 100 | 50 | 0 | 0 | 14361.07 | 6.82 | 8.28 | 44 | 99.51 | 24.225 |
|  HTTP/2 client downgrade service (h2 -> h1) | 100 | 1024 | 0 | 0 | 13755.16 | 7.06 | 7.92 | 39 | 99.54 | 24.234 |
|  HTTP/2 client downgrade service (h2 -> h1) | 300 | 50 | 0 | 0 | 14427.34 | 20.49 | 13.88 | 76 | 98.83 | 24.435 |
|  HTTP/2 client downgrade service (h2 -> h1) | 300 | 1024 | 0 | 0 | 13873.81 | 21.01 | 13.73 | 74 | 98.92 | 24.46 |
|  HTTP/2 client downgrade service (h2 -> h1) | 1000 | 50 | 0 | 0 | 13428.93 | 73.83 | 31.8 | 179 | 97.02 | 25.312 |
|  HTTP/2 client downgrade service (h2 -> h1) | 1000 | 1024 | 0 | 0 | 12885.63 | 76.65 | 31.43 | 179 | 97.03 | 25.312 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h2) | 100 | 50 | 0 | 0 | 15144.29 | 6.44 | 7.85 | 42 | 99.57 | 24.224 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h2) | 100 | 1024 | 0 | 0 | 14230.39 | 6.76 | 8.01 | 44 | 99.59 | 24.215 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h2) | 300 | 50 | 0 | 0 | 15629.31 | 18.83 | 13.53 | 73 | 99.15 | 24.709 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h2) | 300 | 1024 | 0 | 0 | 14886.17 | 19.38 | 13.27 | 70 | 99.22 | 24.45 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h2) | 1000 | 50 | 0 | 0 | 15414.77 | 64.45 | 37.79 | 167 | 97.41 | 25.375 |
|  Passthrough HTTP/2(over TLS) service (h2 -> h2) | 1000 | 1024 | 0 | 0 | 14417.72 | 68.15 | 36.87 | 173 | 97.65 | 25.401 |
|  HTTP/2 server downgrade service (h1 -> h2) | 100 | 50 | 0 | 0 | 14403.99 | 6.88 | 6.06 | 14 | 99.61 | 24.133 |
|  HTTP/2 server downgrade service (h1 -> h2) | 100 | 1024 | 0 | 0 | 12819.38 | 7.75 | 5.58 | 15 | 99.64 | 24.115 |
|  HTTP/2 server downgrade service (h1 -> h2) | 300 | 50 | 0 | 0 | 15327.27 | 19.52 | 11.31 | 34 | 99.25 | 24.307 |
|  HTTP/2 server downgrade service (h1 -> h2) | 300 | 1024 | 0 | 0 | 12970.94 | 23.07 | 8.11 | 38 | 99.37 | 24.383 |
|  HTTP/2 server downgrade service (h1 -> h2) | 1000 | 50 | 0 | 0 | 14401.05 | 69.37 | 16.59 | 116 | 98.04 | 25.371 |
|  HTTP/2 server downgrade service (h1 -> h2) | 1000 | 1024 | 0 | 0 | 12767.47 | 78.21 | 14.07 | 126 | 98.24 | 25.35 |
