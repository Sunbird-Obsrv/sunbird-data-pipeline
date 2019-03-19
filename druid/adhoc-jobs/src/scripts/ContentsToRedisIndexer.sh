#!/usr/bin/env bash
spark/bin/spark-submit \
--conf spark.driver.extraJavaOptions="-Dconfig.file=/home/hduser/adhoc-spark-scripts/resources/ESContentIndexer.conf" \
--class org.ekstep.analytics.jobs.ESToRedisIndexer \
/home/hduser/adhoc-spark-scripts/adhoc-jobs-1.0.jar
