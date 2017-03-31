require 'pry'
require 'tempfile'
require 'zip'

class DataExhaustController
  def initialize(aggregate_date, dataset_id, resource_id,
                 data_exhaust_api, logger)
    @aggregate_date = aggregate_date
    @data_exhaust_api = data_exhaust_api
    @logger = logger
    @dataset_id = dataset_id
    @resource_id = resource_id
  end

  def aggregate
    begin
      from_date = @aggregate_date.start_date
      to_date = @aggregate_date.end_date
      if from_date >= Date.today
        @logger.info('NOTHING NEW TO AGGREGATE, GOING AWAY')
        return
      end
      @data_exhaust_api.aggregate(@dataset_id, @resource_id, from_date, to_date)
      @aggregate_date.update(to_date)
    rescue => e
      @logger.error("EXCEPTION: #{e}")
      return
    end
  end
end
