# SpringBoot TestContainers Demo 
This Demo consists of 2 parts:
* SpringBoot + Testcontainers (LocalStack)
* SpringBoot + Testcontainers (LocalStack + OTEL Collector + Clickhouse)

## SpringBoot + Testcontainers (LocalStack)
Checkout tag NO_OTEL, e.g. using
```
git checkout NO_OTEL
```
The test can be executed by running command
```
mvn clean install
```
in the command line or be started within an IDE.

## SpringBoot + Testcontainers (LocalStack + OTEL Collector + Clickhouse)
Checkout branch OTEL, e.g. using
```
git checkout OTEL
```
The test only finishes successfully if following ENV variables are set:
```
JAVA_TOOL_OPTIONS="-javaagent:opentelemetry-javaagent.jar"
OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf;
OTEL_EXPORTER_OTLP_TIMEOUT=60000
OTEL_INSTRUMENTATION_MICROMETER_ENABLED=true;
OTEL_SERVICE_NAME="Testcontainers"
```
There is a run.sh script exporting mentioned ENV variables. Following command starts the script:
```
./run.sh
```
The test can also be started from an IDE but then the ENV variables have to be configured within the run configuration.
