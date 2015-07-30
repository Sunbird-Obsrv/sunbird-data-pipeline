mvn clean package
rm -rf deploy/samza
mkdir -p deploy/samza
tar -xvf ./target/ekstep-samza-0.0.1-dist.tar.gz -C deploy/samza
deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/reverse-search.properties