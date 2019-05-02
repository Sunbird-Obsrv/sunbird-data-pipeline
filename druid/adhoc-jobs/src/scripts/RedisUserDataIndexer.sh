#!/usr/bin/env bash

home=`echo $HOME`
jobJarPath="${home}/adhoc-jobs-1.0/adhoc-jobs-1.0.jar"
jobConfPath="${home}/adhoc-jobs-1.0/resources/cassandraRedis.conf"

spark/bin/spark-submit \
--conf spark.driver.extraJavaOptions="-Dconfig.file=${jobConfPath}" \
--class org.ekstep.analytics.jobs.CassandraRedisIndexer \
${jobJarPath}
