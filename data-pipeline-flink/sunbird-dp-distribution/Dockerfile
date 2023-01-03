FROM sunbird/flink:1.13.5-scala_2.12-java11
COPY target/sunbird-dp-distribution-1.0.tar.gz /tmp
USER flink
RUN tar -xvf /tmp/sunbird-dp-distribution-1.0.tar.gz -C $FLINK_HOME/lib/
RUN mkdir $FLINK_HOME/plugins/s3-fs-presto
RUN cp $FLINK_HOME/opt/flink-s3-fs-presto-1.13.5.jar $FLINK_HOME/plugins/s3-fs-presto/
USER root
RUN rm -f /tmp/sunbird-dp-distribution-1.0.tar.gz
USER flink
