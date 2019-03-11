#!/bin/sh
#Command to run this script
#./run.sh storage fromDate toDate path
#Example: ./run.sh azure 2019-03-10 2019-03-10 raw/

echo Started executing script
export SPARK_HOME="" #Include the spark home path here
export JOB_LOGS="$PWD/logs"
export FETCHER_JAR_PATH="$PWD/target/data-fetcher-1.0.jar"
echo "Starting the job - $1" >> "$JOB_LOGS/$today-data-fetcher-output.log"
today=$(date "+%Y-%m-%d")
nohup $SPARK_HOME/bin/spark-submit --master local[*] --class eventFetcher.EventsFetcher $FETCHER_JAR_PATH "$1 $2 $3 $4" >> "$JOB_LOGS/$today-data-fetcher-output.log"
echo "Job execution completed - fetched from $1" >> "$JOB_LOGS/$today-data-fetcher-output.log"

echo "Finished data pusing to kafka for this $2 to $3 date"
