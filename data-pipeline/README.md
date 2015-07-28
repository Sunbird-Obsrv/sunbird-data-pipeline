To create a working kafka-samza pipeline, do the following:
- bin/grid bootstrap
- mvn clean package
- mkdir -p deploy/samza
- tar -xvf ./target/ekstep-samza-0.0.1-dist.tar.gz -C deploy/samza

Assuming that kafka is running
- deploy/kafka/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test11

Starting a producer
- deploy/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test11

Starting a consumer (to see if everything is working)
- deploy/kafka/bin/kafka-console-consumer.sh  --zookeeper localhost:2181 --topic test13

Sample events for producer
- {"did":"bc811958-b4b7-4873-a43a-03718edba45b","edata":{"eks":{"loc":"12.9310593,77.6238299","ueksid":"sharan"}},"eid":"GE_SESSION_START","gdata":{"id":"genie.android","ver":"2.2.18"},"sid":"6d5d6eeb-4f1b-4eed-8641-ec9e1884a218","ts":"2015-07-14T12:43:47+05:30","uid":"31e1cbf2b23a01ea035ee3323fe2ab95950c8284","ver":"1.0"}
- {"did":"bc811958-b4b7-4873-a43a-03718edba45b","edata":{"eks":{"err":"","gid":"org.ekstep.math.pp","length":9,"tmsize":0}},"eid":"GE_GAME_END","gdata":{"id":"genie.android","ver":"2.2.18"},"sid":"6d5d6eeb-4f1b-4eed-8641-ec9e1884a218","ts":"2015-07-14T12:43:59+05:30","uid":"31e1cbf2b23a01ea035ee3323fe2ab95950c8284","ver":"1.0"}

Deploying job on YARN
- deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/reverseSearch.properties

So, simple setup will do the following
test11 -> reverse_search -> test13
