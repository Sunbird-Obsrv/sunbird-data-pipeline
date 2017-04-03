require 'pry'
require 'yaml'
require 'date'
require_relative '../controllers/data_exhaust_controller.rb'
require_relative '../external/data_exhaust_api.rb'
require_relative '../external/partner_service.rb'
require_relative '../model/aggregate_date.rb'
require_relative '../model/resource.rb'
require_relative '../config/config.rb'
require_relative '../utils/ep_logging.rb'

module EcosystemPlatform
  module Jobs
    class PartnerDataAggregator

      PROGNAME = 'partner_data_aggregate.jobs.ep'
      include EcosystemPlatform::Utils::EPLogging

      def self.perform
        begin
          logger.start_task
          config = Config.load_partner_spec()
          api = DataExhaustApi.new(config.dataset_aggregate_endpoint, logger)
          partner_service = PartnerService.new(config.get_partners_endpoint, logger)
          resources = partner_service.get_all_resources()
          resources.each do |resource|
            begin
              logger.info("AGGREGATING FOR RESOURCE: #{resource.to_s}")
              controller = DataExhaustController.new(
                AggregateDate.new(config.data_dir,
                                  "#{resource.partnerId}.yml", config.initial_aggregate_date,logger),
              config.dataset_id, resource.partnerId, api,logger)
              controller.aggregate()
            rescue => e
              logger.error("ERROR WHEN AGGREGATING RESOURCE: #{resource.partnerId}. Exception: #{e}", {backtrace: e.backtrace[0..4]})
            end
          end
          logger.end_task
        rescue => e
          logger.error(e, {backtrace: e.backtrace[0..4]})
          logger.end_task
        end
      end
    end
  end
end
