require 'monitor'
require 'json'
require 'securerandom'

module EcosystemPlatform
  module Utils
    class Logger
      module Severity
        DEBUG = 0
        INFO = 1
        WARN = 2
        ERROR = 3
        FATAL = 4
        UNKNOWN = 5
        SEV_LABEL = %w(DEBUG INFO WARN ERROR FATAL ANY)
      end
      include Severity
      class Error < RuntimeError
      end
      VER = "1.0"
      attr_accessor :progname,:tid,:aid
      def initialize(logdev)
        @logdev = nil
        if logdev
          @logdev = LogDevice.new(logdev)
        end
      end
      def start_task(data={})
        @tid = SecureRandom.uuid
        add('EP_TASK_START',@progname,@tid,nil,data)
        tid
      end
      def end_task(data={})
        add('EP_TASK_END',@progname,@tid,nil,data)
        tid = nil
      end
      def info(msg,data={})
        data ||= {}
        data.merge!({ msg: msg,type: format_severity(INFO) })
        add('EP_LOG',@progname,@tid,@aid,data)
      end
      def error(msg,data={})
        data ||= {}
        data.merge!({ msg: msg,type: format_severity(ERROR) })
        add('EP_LOG',@progname,@tid,@aid,data)
      end
      def close
        @logdev.close if @logdev
      end
    private
      def format_severity(severity)
        SEV_LABEL[severity] || 'ANY'
      end
      def add(eid,progname,tid,aid,data)
        @logdev.write("#{JSON.generate(
          {
            eid: eid,
            progname: progname,
            ts: Time.now.to_i,
            tid: tid,
            aid: aid,
            ver: VER,
            data: data
          })}\n")
      end
      class LogDevice
        attr_reader :dev
        attr_reader :filename
        class LogDeviceMutex
          include MonitorMixin
        end
        def initialize(log = nil, opt = {})
          @dev = @filename = nil
          @mutex = LogDeviceMutex.new
          if log.respond_to?(:write) and log.respond_to?(:close)
            @dev = log
          else
            @dev = open_logfile(log)
            @dev.sync = true
            @filename = log
          end
        end
        def write(message)
          begin
            @mutex.synchronize do
              begin
                @dev.write(message)
              rescue
                warn("log writing failed. #{$!}")
              end
            end
          rescue Exception => ignored
            warn("log writing failed. #{ignored}")
          end
        end
        def close
          begin
            @mutex.synchronize do
              @dev.close rescue nil
            end
          rescue Exception
            @dev.close rescue nil
          end
        end
        private
        def open_logfile(filename)
          begin
            open(filename, (File::WRONLY | File::APPEND))
          rescue Errno::ENOENT
            create_logfile(filename)
          end
        end
        def create_logfile(filename)
          begin
            logdev = open(filename, (File::WRONLY | File::APPEND | File::CREAT | File::EXCL))
            logdev.flock(File::LOCK_EX)
            logdev.sync = true
            logdev.flock(File::LOCK_UN)
          rescue Errno::EEXIST
            # file is created by another process
            logdev = open_logfile(filename)
            logdev.sync = true
          end
          logdev
        end
      end
    end
  end
end
