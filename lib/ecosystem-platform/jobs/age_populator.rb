require 'pry'
require 'hashie'
require 'elasticsearch'
require_relative '../utils/ep_logging.rb'

module EcosystemPlatform
  module Jobs
    class AgePopulator
      PROGNAME = 'age_populator.jobs.ep'
      include EcosystemPlatform::Utils::EPLogging
      def self.perform(index="ecosystem-*")
        begin
          logger.start_task
          logger.info("INITIALIZING CLIENT")
          @client = ::Elasticsearch::Client.new(log: false)
          @client.indices.refresh index: index
          logger.info("SEARCHING EVENTS FOR AGE ")
          response = @client.search({
            index: index,
            type: 'events_v1',
            size: 1000000,
            body: {
              "query"=> {
                "constant_score" => {
                  "filter" =>
                    {
                    "and" => {
                      "filters" => [
                        {
                          "exists" => {
                            "field" => "uid"
                          }
                        },
                        {
                          "not" => {
                            "filter" => {
                              "term" => {
                                "uid" => ""
                              }
                            }
                          }
                        },
                        {
                          "missing" => {
                            "field" => "udata.age",
                            "existence" => true,
                            "null_value" => true
                          }
                        }
                      ]
                    }
                  },
                }
              }
            }
          })
          response = Hashie::Mash.new response
          logger.info "FOUND #{response.hits.total} hits."
          # response.hits.hits.each do |hit|
          #   logger.info "-> UPDATE #{hit._source.eid} #{hit._id}"
          #   # result = @client.update({
          #   #   index: hit._index,
          #   #   type: hit._type,
          #   #   id: hit._id,
          #   #   body: {
          #   #     doc: {
          #   #       edata: {
          #   #         eks: {
          #   #           utype: 'CHILD'
          #   #         }
          #   #       }
          #   #     }
          #   #   }
          #   # })
          #   # logger.info "<- RESULT #{result}"
          # end
          logger.end_task
        rescue => e
          logger.error(e,{backtrace: e.backtrace[0..4]})
          logger.end_task
        end
      end
    end
  end
end

