# Ballerina Performance Test Results

During each release, we execute various automated performance test scenarios and publish the results.

| Test Scenarios | Description |
| --- | --- |
| Passthrough HTTP service | An HTTP Service, which forwards all requests to a back-end service. |

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
| Heap Size | The amount of memory allocated to the application | 64M, 128M, 512M, 1G |
| Concurrent Users | The number of users accessing the application at the same time. | 1, 5, 50, 250 |
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

|  Scenario Name | Concurrent Users | Message Size (Bytes) | Back-end Service Delay (ms) | Error % | Throughput (Requests/sec) | Average Response Time (ms) | Standard Deviation of Response Time (ms) | 99th Percentile of Response Time (ms) | Ballerina GC Throughput (%) | Average of Ballerina Memory Footprint After Full GC (M) |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
|  Passthrough HTTP service | 2 | 50 | 0 | 0 | 4170.91 | 0.45 | 0.55 | 1 | 99.9 |  |
|  Passthrough HTTP service | 2 | 1024 | 0 | 0 | 3866.11 | 0.49 | 0.54 | 1 | 99.9 |  |
|  Passthrough HTTP service | 10 | 50 | 0 | 0 | 13646.86 | 0.7 | 0.76 | 2 | 99.66 |  |
|  Passthrough HTTP service | 10 | 1024 | 0 | 0 | 12716.3 | 0.75 | 0.76 | 2 | 99.68 |  |
|  Passthrough HTTP service | 100 | 50 | 0 | 0 | 20895.14 | 4.74 | 6.31 | 37 | 98.97 |  |
|  Passthrough HTTP service | 100 | 1024 | 0 | 0 | 19349.35 | 5.13 | 6.62 | 40 | 99.04 |  |
|  Passthrough HTTP service | 500 | 50 | 0 | 0 | 22417.41 | 22.24 | 15.98 | 93 | 95.24 | 42.087 |
|  Passthrough HTTP service | 500 | 1024 | 0 | 0 | 20792.04 | 23.98 | 17.04 | 97 | 95.88 | 42.703 |
|  Passthrough HTTP service | 2 | 50 | 0 | 0 | 4012.34 | 0.47 | 0.57 | 1 | 98.58 | 15.301 |
|  Passthrough HTTP service | 2 | 1024 | 0 | 0 | 3858.83 | 0.49 | 0.58 | 1 | 98.6 | 13.593 |
|  Passthrough HTTP service | 10 | 50 | 0 | 0 | 12288.46 | 0.78 | 0.88 | 3 | 95.58 | 13.832 |
|  Passthrough HTTP service | 10 | 1024 | 0 | 0 | 11482.48 | 0.84 | 0.87 | 3 | 95.73 | 13.441 |
|  Passthrough HTTP service | 100 | 50 | 0 | 0 | 15353.33 | 6.47 | 8.8 | 49 | 82.31 | 16.479 |
|  Passthrough HTTP service | 100 | 1024 | 0 | 0 | 14363.6 | 6.92 | 8.98 | 49 | 83.36 | 16.427 |
|  Passthrough HTTP service | 500 | 50 | 0 | 0 | 4886.11 | 102.27 | 81.48 | 419 | 30.06 | 30.066 |
|  Passthrough HTTP service | 500 | 1024 | 0 | 0 | 4780.48 | 104.53 | 81.46 | 421 | 30.85 | 30.095 |
|  Passthrough HTTP service | 2 | 50 | 0 | 0 | 3799.62 | 0.5 | 0.55 | 1 | 99.29 |  |
|  Passthrough HTTP service | 2 | 1024 | 0 | 0 | 3866.74 | 0.49 | 0.54 | 1 | 99.26 |  |
|  Passthrough HTTP service | 10 | 50 | 0 | 0 | 13021.19 | 0.74 | 0.84 | 3 | 97.37 | 21.538 |
|  Passthrough HTTP service | 10 | 1024 | 0 | 0 | 12176.95 | 0.79 | 0.82 | 3 | 97.52 | 19.655 |
|  Passthrough HTTP service | 100 | 50 | 0 | 0 | 18051.84 | 5.5 | 7.66 | 44 | 91.6 | 18.473 |
|  Passthrough HTTP service | 100 | 1024 | 0 | 0 | 17042.79 | 5.82 | 7.94 | 45 | 92.17 | 18.56 |
|  Passthrough HTTP service | 500 | 50 | 0 | 0 | 11248.18 | 44.38 | 38.19 | 190 | 56.24 | 33.384 |
|  Passthrough HTTP service | 500 | 1024 | 0 | 0 | 11070 | 45.1 | 38.18 | 192 | 58.12 | 33.209 |
|  Passthrough HTTP service | 2 | 50 | 0 | 0 | 4113.34 | 0.46 | 0.56 | 1 | 99.81 |  |
|  Passthrough HTTP service | 2 | 1024 | 0 | 0 | 3719.24 | 0.51 | 0.53 | 1 | 99.83 |  |
|  Passthrough HTTP service | 10 | 50 | 0 | 0 | 13692.37 | 0.7 | 0.81 | 2 | 99.31 |  |
|  Passthrough HTTP service | 10 | 1024 | 0 | 0 | 12790.58 | 0.75 | 0.77 | 2 | 99.35 |  |
|  Passthrough HTTP service | 100 | 50 | 0 | 0 | 20483.18 | 4.84 | 6.6 | 38 | 97.51 | 23.943 |
|  Passthrough HTTP service | 100 | 1024 | 0 | 0 | 19310.39 | 5.13 | 6.6 | 38 | 97.77 | 22.289 |
|  Passthrough HTTP service | 500 | 50 | 0 | 0 | 20746.56 | 24.04 | 18.65 | 105 | 90.66 | 35.276 |
|  Passthrough HTTP service | 500 | 1024 | 0 | 0 | 19452.76 | 25.63 | 18.8 | 105 | 91.39 | 34.812 |
