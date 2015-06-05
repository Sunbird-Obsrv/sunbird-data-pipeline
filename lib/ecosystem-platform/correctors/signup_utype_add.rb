require 'pry'
require 'hashie'
require 'elasticsearch'
require_relative '../utils/ep_logging.rb'

module EcosystemPlatform
  module Correctors
    class SignupUtypeAdd
      PROGNAME = 'signup_utype_add.correctors.ep'
      include EcosystemPlatform::Utils::EPLogging
      def self.perform(index="ecosystem-*")
        begin
          logger.start_task
          logger.info("INITIALIZING CLIENT")
          @client = ::Elasticsearch::Client.new(log: false)
          @client.indices.refresh index: index
          logger.info("SEARCHING GE_SIGNUPs")
          response = @client.search({
            index: index,
            type: 'events_v1',
            size: 1000000,
            body: {
              "query"=> {
                "constant_score" => {
                  "filter" => {
                    "and"=> [
                      {
                        "term"=> {
                          "eid"=>"GE_SIGNUP"
                        }
                      },
                      {
                        "missing" => {
                          "field" => "edata.eks.utype",
                          "existence" => true,
                          "null_value" => true
                        }
                      }
                    ]
                  }
                }
              }
            }
          })
          response = Hashie::Mash.new response
          logger.info "FOUND #{response.hits.total} hits."
          response.hits.hits.each do |hit|
            logger.info "-> UPDATE GE_SIGNUP #{hit._id}"
            result = @client.update({
              index: hit._index,
              type: hit._type,
              id: hit._id,
              body: {
                doc: {
                  edata: {
                    eks: {
                      utype: 'CHILD'
                    }
                  }
                }
              }
            })
            logger.info "<- RESULT #{result}"
          end
          logger.end_task
        rescue => e
          logger.error(e,{backtrace: e.backtrace[0..4]})
          logger.end_task
        end
      end
    end
  end
end

