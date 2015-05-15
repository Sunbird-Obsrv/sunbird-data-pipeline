require 'logger'
require 'elasticsearch'
require 'pry'
require 'hashie'

module Removers
  class GeSignupCleanup
    # ES_URL='http://52.74.22.23:9200'
    def self.remove(index="ecosystem-*",type="events_v1")
      begin
        file = File.expand_path("./logs/logfile.log", File.dirname(__FILE__))
        logger = Logger.new(file)
        @client = ::Elasticsearch::Client.new log: false
        @client.indices.refresh index: index
        logger.info "Starting search"
        response = @client.search({
          index: index,
          type: type,
          size: 100000,
          body: {
            "query" =>  {
              "constant_score" => {
                "filter" => {
                  "and" => [
                    {
                      "term" => {
                        "eid" => "GE_SESSION_START"
                      }
                    },
                    {
                        "term" => {
                        "flags.signup_processed" => true
                      }
                    }
                  ]
                }
              }
            }
          }
        })
        response = ::Hashie::Mash.new response
        logger.info "FOUND #{response.hits.total} hits."
        if response.hits.total == 0
          logger.info "No more events to process"
          return
        end
        response.hits.hits.each do |hit|
          result = @client.update({
              index: hit._index,
              type: hit._type,
              id: hit._id,
              body: {
                doc: {
                  flags: nil
                }
              }
            })
          logger.info "GE_SESSION_START #{result.to_json}"
        end
        logger.info "Updated GE_SESSION_START"
      rescue => e
        logger.error e
      end
    end
  end
end

