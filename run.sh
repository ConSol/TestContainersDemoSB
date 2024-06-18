export JAVA_TOOL_OPTIONS="-javaagent:opentelemetry-javaagent.jar"
export OTEL_SERVICE_NAME="Testcontainers"
export OTEL_JAVAAGENT_CONFIGURATION_FILE="agent.properties"
#export OTEL_METRICS_EXPORTER="otlp"
mvn clean install
