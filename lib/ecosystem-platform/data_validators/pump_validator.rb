require 'pry'
require 'hashie'
require 'elasticsearch'
require 'base64'

require_relative '../utils/ep_logging.rb'

module EcosystemPlatform
  module DataValidators
    class PumpValidator

      PROGNAME = 'pump_validator.jobs.ep'

      include EcosystemPlatform::Utils::EPLogging

      def self.perform(index, file_path)
        begin
          logger.start_task
          logger.info("INITIALIZING CLIENT")

          @client = ::Elasticsearch::Client.new(host:ENV['ES_HOST']||'localhost',log: false)
          @client.indices.refresh index: index
          logger.info("SEARCHING EVENTS TO VALIDATE")
          mid_found = 0
          mid_not_found = 0
          Dir.glob(file_path) do |file_name|
            logger.info "PROCESSING FILE : #{file_name}"
            File.foreach(file_name) do |mid|
              response = @client.search({
                index: index,
                type: 'events_v1',
                sort: 'ts',
                body: {
                  "query"=> {
                    "filtered": {
                      "query": {
                       "query_string": {
                          "query": "mid: \"#{mid.strip}\""
                        }
                      }
                    }
                  }
                } 
              })
              binding.pry
              response = Hashie::Mash.new response
              logger.info "FOUND #{response.hits.hits.count} hits. - TOTAL #{response.hits.total}"
              response.hits.hits.each do |hit|
                metadata = hit._source.metadata
                if(metadata != nil)
                  if metadata.source_mid == mid
                    logger.info "MID #{mid} IS MATCHING WITH METADATA SOURCE MID #{metadata.source_mid}"
                    mid_found += 1
                  else
                    logger.info "#{mid} : MID NOT FOUND"
                    mid_not_found += 1
                  end
                end
              end
            end
          end
          logger.info "COUNT MID FOUND: #{mid_found}"
          logger.info "COUNT MID NOT FOUND: #{mid_not_found}"
          logger.end_task
        rescue => e
          logger.error(e,{backtrace: e.backtrace[0..4]})
          logger.end_task
        end
      end
    end
  end
end
