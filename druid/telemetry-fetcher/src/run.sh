#!/bin/bash
#Example : ./run.sh --startDate 2019-03-10 --endDate 2019-03-10 --env local --service azure --eventType summary --prefix derived/wfs/
POSITIONAL=()
while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    -s|--startDate)
    STARTDATE="$2"
    shift # past argument
    shift # past value
    ;;
    -e|--endDate)
    ENDDATE="$2"
    shift # past argument
    shift # past value
    ;;
    -c|--service)
    SERVICE="$2"
    shift # past argument
    shift # past value
    ;;
    -t|--eventType)
    EVENTTYPE="$2"
    shift # past argument
    shift # past value
    ;;
    -b|--bucket)
    BUCKET="$2"
    shift # past argument
    shift # past value
    ;;

    -p|--prefix)
    PREFIX="$2"
    shift # past argument
    shift # past value
    ;;
    -m|--executorMemory)
    EXECUTORMEMORY="$2"
    shift # past argument
    shift # past value
    ;;
    -r|--master)
    MASTER="$2"
    shift # past argument
    shift # past value
    ;;
    -d|--deployeMode)
    DEPLOYEMODE="$2"
    shift # past argument
    shift # past value
    ;;
    -x|--totalExecutor)
    TOTAL_EXECUTOR_CORE="$2"
    shift # past argument
    shift # past value
    ;;

    -i|--topic)
    TOPIC="$2"
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
#export TELEMETRY_FETCHER_JAR_PATH="/Users/admin/Documents/sunbird-forked-repo/sunbird-data-pipeline/druid/telemetry-fetcher/target/telemetry-fetcher-1.0.jar"
echo "Started executing the script to fetch the data from $STARTDATE to $ENDDATE date from the $SERVICE service with this $PREFIX location prefix in $BUCKET and pushing to $TOPIC"
export JOB_LOGS="$PWD/logs"
nohup $SPARK_HOME/bin/spark-submit --executor-memory $EXECUTORMEMORY --total-executor-cores $TOTAL_EXECUTOR_CORE --deploy-mode $DEPLOYEMODE --master $MASTER --class org.sunbird.EventsFetcher $TELEMETRY_FETCHER_JAR_PATH --startDate $STARTDATE --endDate $ENDDATE --bucket $BUCKET --service $SERVICE --eventType $EVENTTYPE --prefix $PREFIX --topic $TOPIC  >> "$JOB_LOGS/$today-events-fetcher-output.log"
echo "Script execution completed please check the status in this $JOB_LOGS/$today-events-fetcher-output.log file"
