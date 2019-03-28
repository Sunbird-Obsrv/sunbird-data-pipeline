#!/usr/bin/env bash
#Example: ./run.sh -s 2019-03-10 -e 2019-03-10 -v local -c azure -t summary -p derived/wfs/

while getopts ":s:e:c:t:b:v:p:" arguments; do
  case $arguments in
    s) startDate="$OPTARG"
    ;;
    e) endDate="$OPTARG"
    ;;
    c) service="$OPTARG"
    ;;
    t) eventType="$OPTARG"
    ;;
    b) bucket="$OPTARG"
    ;;
    v) env="$OPTARG"
    ;;
    p) prefix="$OPTARG"
    ;;
    \?) echo "Invalid arguments -$OPTARG" >&2
    ;;
  esac
done
today=$(date "+%Y-%m-%d-%h-%m")
echo "Started executing the script to fetch the data from $startDate to $endDate date from the $service service with this $prefix location prefix "
export JOB_LOGS="$PWD/logs"
nohup $SPARK_HOME/bin/spark-submit --master local[*] --class org.sunbird.EventsFetcher $TELEMETRY_FETCHER_JAR_PATH --startDate $startDate --endDate $endDate --env $env --service $service --eventType $eventType --prefix $prefix >> "$JOB_LOGS/$today-events-fetcher-output.log"
echo "Script execution completed please check the status in this $JOB_LOGS/$today-events-fetcher-output.log file"
