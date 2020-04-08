FROM flink:1.10.0-scala_2.12
COPY target/sunbird-dp-distribution-1.0.tar.gz /tmp
RUN tar -xvf /tmp/sunbird-dp-distribution-1.0.tar.gz -C $FLINK_HOME/lib/
USER flink