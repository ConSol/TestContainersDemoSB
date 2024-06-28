export JAVA_TOOL_OPTIONS="-javaagent:opentelemetry-javaagent.jar"
export OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf;
export OTEL_EXPORTER_OTLP_TIMEOUT=60000
export OTEL_INSTRUMENTATION_MICROMETER_ENABLED=true;
export OTEL_JAVAAGENT_LOGGING="none"
export OTEL_SERVICE_NAME="Testcontainers"
mvn clean install
