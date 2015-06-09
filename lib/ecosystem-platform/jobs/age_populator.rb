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
          db_connect_opts = {
            :host => ENV['DB_HOST']||"localhost",
            :username => "root",
            :database => "ecosystem"
          }
          db_connect_opts.merge!(password:ENV['DB_PASS']) if(ENV['DB_PASS'])
          @db_client = Mysql2::Client.new(db_connect_opts)
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
                  }
                }
              }
            }
          })
          response = Hashie::Mash.new response
          logger.info "FOUND #{response.hits.total} hits."
          response.hits.hits.each do |hit|
            uid = hit._source.uid
            results = @db_client.query("SELECT * FROM children where encoded_id = '#{uid}'")
            child = Hashie::Mash.new results.first
            if(child.dob)
              time_then = Time.strptime(hit._source.ts,'%Y-%m-%dT%H:%M:%S%z')
              age_then = time_then - child.dob
              logger.info "-> UPDATE #{hit._source.eid} #{hit._id}"
              result = @client.update({
                index: hit._index,
                type: hit._type,
                id: hit._id,
                body: {
                  doc: {
                    udata: {
                      age: age_then,
                      dob: child.dob
                    }
                  }
                }
              })
              logger.info "<- RESULT #{result}"
            end
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

