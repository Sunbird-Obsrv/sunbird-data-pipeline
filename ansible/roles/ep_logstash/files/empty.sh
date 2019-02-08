#!/bin/bash

LOGSTASH_LOG=/var/log/logstash

tail -n 100 $LOGSTASH_LOG/logstash.stdout > $LOGSTASH_LOG/logstash.stdout.tmp
cat $LOGSTASH_LOG/logstash.stdout.tmp > $LOGSTASH_LOG/logstash.stdout
rm $LOGSTASH_LOG/logstash.stdout.tmp