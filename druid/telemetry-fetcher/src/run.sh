#!/bin/bash
#Example : ./run.sh --executorMemory 20G --master local[8] --startDate 2019-03-10 --endDate 2019-03-11 --service azure --eventType summary --prefix telemetry-denormalized/raw/ --dataSource telemetry-events --verification true --bucket telemetry-data-store --topic dev.telemetry
POSITIONAL=()
while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    -s|--startDate)  # start date to fetch the data ex: (YYYY-MM-DD) 2019-03-10
    STARTDATE="$2"
    shift # past argument
    shift # past value
    ;;
    -e|--endDate)  # end date to fetch the data ex:(YYYY-MM-DD) 2019-03-11
    ENDDATE="$2"
    shift # past argument
    shift # past value
    ;;
    -c|--service) # service it can be either azure or s3
    SERVICE="$2"
    shift # past argument
    shift # past value
    ;;
    -t|--eventType)  # eventType it can be either "telemetry" or "summary"
    EVENTTYPE="$2"
    shift # past argument
    shift # past value
    ;;
    -b|--bucket) # Bucket it can be storage bucket name of s3 or azure
    BUCKET="$2"
    shift # past argument
    shift # past value
    ;;

    -p|--prefix) # Path of the folder ex: for summary - derived/wfs/  and for telemetry - raw/
    PREFIX="$2"
    shift # past argument
    shift # past value
    ;;
    -m|--executorMemory) # spark executor memory -ex: 20G
    EXECUTORMEMORY="$2"
    shift # past argument
    shift # past value
    ;;
    -r|--master) #spark cluster manager ex: local[*] || yarn || local[N]
    MASTER="$2"
    shift # past argument
    shift # past value
    ;;
    -d|--deployeMode) #which defines the spark deploy-mode ex: cluster || client
    DEPLOYEMODE="$2"
    shift # past argument
    shift # past value
    ;;
    -x|--totalExecutor) # Which defines the Spark no'f executors
    TOTAL_EXECUTOR_CORE="$2"
    shift # past argument
    shift # past value
    ;;

    -i|--topic) # To which topic the events should push ex: name of the topic
    TOPIC="$2"
    shift # past argument
    shift # past value
    ;;

    -v|--verification) # When verification is true which generate the audit script else it will push the events to kafka
    VERIFICATION="$2"
    shift # past argument
    shift # past value
    ;;

    -d|--dataSource) # Name of the druid data source
    DATASOURCE="$2"
    shift # past argument
    shift # past value
    ;;

    --default)
    DEFAULT=YES
    shift # past argument
    ;;
    *)    # unknown option
    POSITIONAL+=("$1") # save it in an array for later
    shift # past argument
    ;;
esac
done
set -- "${POSITIONAL[@]}" # restore positional parameters

today=$(date "+%Y-%m-%d-%h-%m")
#export TELEMETRY_FETCHER_JAR_PATH="/Users/admin/Documents/projects/sunbird-data-pipeline/druid/telemetry-fetcher/target/telemetry-fetcher-1.0.jar"
echo "Started executing the script to fetch the data from $STARTDATE to $ENDDATE date from the $SERVICE service with this $PREFIX location prefix in $BUCKET and pushing to $TOPIC $DATASOURCE"
export JOB_LOGS="$PWD/logs"
nohup $SPARK_HOME/bin/spark-submit --executor-memory $EXECUTORMEMORY --total-executor-cores $TOTAL_EXECUTOR_CORE --deploy-mode $DEPLOYEMODE --master $MASTER --class org.sunbird.EventsFetcher $TELEMETRY_FETCHER_JAR_PATH --startDate $STARTDATE --endDate $ENDDATE --bucket $BUCKET --service $SERVICE --eventType $EVENTTYPE --prefix $PREFIX --verification $VERIFICATION --dataSource $DATASOURCE --topic $TOPIC  >> "$JOB_LOGS/$today-events-fetcher-output.log"
echo "Script execution completed please check the status in this $JOB_LOGS/$today-events-fetcher-output.log file"
