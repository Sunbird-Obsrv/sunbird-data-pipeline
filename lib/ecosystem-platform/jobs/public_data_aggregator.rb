require 'pry'
require 'yaml'
require 'date'
require_relative '../controllers/data_exhaust_controller.rb'
require_relative '../external/data_exhaust_api.rb'
require_relative '../model/aggregate_date.rb'
require_relative '../config/config.rb'
require_relative '../utils/ep_logging.rb'

module EcosystemPlatform
  module Jobs
    class PublicDataAggregator

      PROGNAME = 'public_data_aggregate.jobs.ep'
      include EcosystemPlatform::Utils::EPLogging

      def self.perform
        begin
          logger.start_task
          config = Config.load
          api = DataExhaustApi.new(config.dataset_aggregate_endpoint, logger)
          DataExhaustController.new(
            AggregateDate.new(config.data_dir, config.store_file_name, config.initial_aggregate_date,logger),
            config.dataset_id, config.resource_id,api,logger)
          .aggregate()
          logger.end_task
        rescue => e
          logger.error(e, {backtrace: e.backtrace[0..4]})
          logger.end_task
        end
      end
    end
  end
end
