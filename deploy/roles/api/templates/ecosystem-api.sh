#! /bin/sh
### BEGIN INIT INFO
# Provides: Ecosystem-Platform-API
# Default-Start: 2 3 4 5
# Default-Stop: S 0 1 6
# Short-Description: Ecosystem-Platform-API
# Description: Starts Ecosystem-Platform-API as a daemon.
### END INIT INFO

DESC="Ecosystem-Platform-API Daemon"
NAME=/usr/bin/Ecosystem-Platform-API
LOGFILE="/var/log/telemetry.log"
PORT="8080"
SCRIPTNAME=/etc/init.d/ecosystem-api
PID="/var/run/ecosystem-api.pid"

ARGS="-l ${LOGFILE} -p ${PORT}"
API_USER="{{ apiuser }}"
API_PASS="{{ apipass }}"

# Exit if the package is not installed
if [ ! -x "$NAME" ]; then
{
  echo "Couldn't find $NAME"
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

  start-stop-daemon --start --pidfile $PID --quiet --exec $NAME --test > /dev/null \
  || return 1
  
  start-stop-daemon --start --make-pidfile --pidfile $PID --quiet --background --exec /usr/bin/env API_USER=$API_USER API_PASS=$API_PASS $NAME -- $ARGS \
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
  status)
     status_of_proc -p $PID $NAME $DESC && exit 0 || exit $?
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
