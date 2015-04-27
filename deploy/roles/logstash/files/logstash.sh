#! /bin/sh
### BEGIN INIT INFO
# Provides: logstash
# Required-Start: $local_fs $remote_fs
# Required-Stop: $local_fs $remote_fs
# Default-Start: 2 3 4 5
# Default-Stop: S 0 1 6
# Short-Description: Logstash
# Description: Starts Logstash as a daemon.
# Author: christian.paredes@sbri.org
### END INIT INFO

# Amount of memory for Java
JAVAMEM=256M

# User to run logstash server as
LOGSTASH_USER=logstash
LOGSTASH_GROUP=logstash

# Location of logstash files
LOCATION=/opt/logstash

PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
DESC="Logstash Daemon"
NAME=java
#DAEMON=`which java`
DAEMON=/usr/bin/java
ulimit -n 32000
CONFIG_DIR="/etc/logstash.d"
LOGFILE="/var/log/logstash/logstash.log"
PATTERNSPATH="/opt/logstash/patterns"
ARGS="-Xmx$JAVAMEM -Xms$JAVAMEM -jar logstash-monolithic.jar agent --config ${CONFIG_DIR} --log ${LOGFILE} --grok-patterns-path ${PATTERNSPATH}"
SCRIPTNAME=/etc/init.d/logstash
PID="/var/run/logstash-agent.pid"

# Exit if the package is not installed
if [ ! -x "$DAEMON" ]; then
{
  echo "Couldn't find $DAEMON"
  exit 99
}
fi

# Define LSB log_* functions.
# Depend on lsb-base (>= 3.0-6) to ensure that this file is present.
. /lib/lsb/init-functions

#
# Function that starts the daemon/service
#
do_start()
{
  start-stop-daemon --start --make-pidfile --pidfile $PID --quiet --background --chuid $LOGSTASH_USER:$LOGSTASH_GROUP -d $LOCATION --exec $DAEMON -- $ARGS \
  || return 2
}

#
# Function that stops the daemon/service
#
do_stop()
{
  # Return
  # 0 if daemon has been stopped
  # 1 if daemon was already stopped
  # 2 if daemon could not be stopped
  # other if a failure occurred
  start-stop-daemon --stop --pidfile $PID --quiet --oknodo
  RETVAL="$?"
  rm -f $PID
  return "$RETVAL"
}

case "$1" in
  start)
    log_daemon_msg "Starting $DESC"
    do_start
    case "$?" in
      0|1) log_end_msg 0 ;;
      2) log_end_msg 1 ;;
    esac
    ;;
  stop)
    log_daemon_msg "Stopping $DESC"
    do_stop
    case "$?" in
      0|1) log_end_msg 0 ;;
      2) log_end_msg 1 ;;
    esac
    ;;
  restart)
    log_daemon_msg "Restarting $DESC"
    do_stop
    case "$?" in
      0|1)
        do_start
        case "$?" in
          0) log_end_msg 0 ;;
          1) log_end_msg 1 ;; # Old process is still running
          *) log_end_msg 1 ;; # Failed to start
        esac
        ;;
      *)
        # Failed to stop
        log_end_msg 1
        ;;
    esac
    ;;
  *)
    echo "Usage: $SCRIPTNAME {start|stop|restart}" >&2
    exit 3
    ;;
esac

exit 0