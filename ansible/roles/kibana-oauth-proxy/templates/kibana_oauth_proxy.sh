#! /bin/sh
#
# /etc/init.d/kibana_oauth_proxy -- startup script for kibana oauth proxy
#
### BEGIN INIT INFO
# Provides:          kibana_oauth_proxy
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Starts kibana_oauth_proxy
# Description:       Starts kibana_oauth_proxy using start-stop-daemon
### END INIT INFO

_SYSTEMCTL_SKIP_REDIRECT=1
KIBANA_OAUTH_PROXY_BIN=/opt/kibana_oauth_proxy/bin
CONFIG_PATH=/opt/kibana_oauth_proxy/config.cfg
NAME=kibana_oauth_proxy
PID_FILE=/var/run/$NAME.pid
PATH=/bin:/usr/bin:/sbin:/usr/sbin:$KIBANA_OAUTH_PROXY_BIN
DAEMON=$KIBANA_OAUTH_PROXY_BIN/oauth2_proxy
SCRIPTNAME=/etc/init.d/kibana_oauth_proxy
DESC="KibanaOauthProxy daemon"
LOGFILE=/var/log/kibana_oauth_proxy/kibana_oauth_proxy.stdout
ARGS="--config=$CONFIG_PATH"

# Exit if the package is not installed
if [ ! -x "$KIBANA_OAUTH_PROXY_BIN" ]; then
{
  echo "Couldn't find $KIBANA_OAUTH_PROXY_BIN"
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

  start-stop-daemon --start --pidfile $PID_FILE --quiet --exec $DAEMON --test > /dev/null \
  || return 1
  start-stop-daemon --start --make-pidfile --pidfile $PID_FILE --quiet --background --startas /bin/bash -- -c "exec $DAEMON $ARGS > $LOGFILE 2>&1" \
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
  start-stop-daemon --stop --pidfile $PID_FILE --quiet --oknodo
  RETVAL="$?"
  rm -f $PID_FILE
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
     status_of_proc -p $PID_FILE $KIBANA_OAUTH_PROXY_BIN $DESC && exit 0 || exit $?
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
