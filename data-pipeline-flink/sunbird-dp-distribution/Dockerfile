FROM sunbird/flink:1.13.0-scala_2.12-java11
COPY target/sunbird-dp-distribution-1.0.tar.gz /tmp
USER flink
RUN tar -xvf /tmp/sunbird-dp-distribution-1.0.tar.gz -C $FLINK_HOME/lib/
USER root
RUN rm -f /tmp/sunbird-dp-distribution-1.0.tar.gz
USER flink
