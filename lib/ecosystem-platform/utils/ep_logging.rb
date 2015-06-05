require_relative 'logger.rb'

module EcosystemPlatform
  module Utils
    module EPLogging
      def logger
        EPLogging.logger_for(self::PROGNAME||self.name)
      end
      @loggers = {}
      def self.included(base)
        class << base
          def logger
            EPLogging.logger_for(self::PROGNAME||self.name)
          end
        end
      end
      class << self
        def logger_for(classname)
          @loggers[classname] ||= configure_logger_for(classname)
        end
        def configure_logger_for(classname)
          log_file = "#{ENV['EP_LOG_DIR']}/#{classname}.log"
          logger = EcosystemPlatform::Utils::Logger.new(log_file)
          logger.progname = classname
          logger
        end
      end
    end
  end
end
