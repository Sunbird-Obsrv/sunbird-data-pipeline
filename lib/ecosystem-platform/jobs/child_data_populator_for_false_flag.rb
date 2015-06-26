require 'hashie'
require 'elasticsearch'
require 'mysql2'
require 'yaml'

require_relative '../utils/ep_logging.rb'

module EcosystemPlatform
  module Jobs
    class ChildDataPopulatorForFalseFlag
      PROGNAME = 'child_data_populator_for_false_flag.jobs.ep'
      include EcosystemPlatform::Utils::EPLogging

      def self.perform(index="ecosystem-*")
        begin
          logger.start_task
          logger.info("INITIALIZING CLIENT")
          # TODO Terrible terrible
          # will be replaced by a config module
          @client = ::Elasticsearch::Client.new(host:ENV['ES_HOST']||'localhost',log: false)
          @client.indices.refresh index: index
          logger.info("SEARCHING EVENTS TO UPDATE DATA FOR FALSE FLAG")
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
                          "exists" => {
                            "field" => "flags.child_data_processed"
                          }
                        },
                        {
                          "filter" => {
                            "term" => {
                              "flags.child_data_processed" => false
                            }
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
            result = @client.update({
              index: hit._index,
              type: hit._type,
              id: hit._id,
              body: request_body(child,hit)
            })
            logger.info "<- RESULT #{result}"
          end
          logger.end_task
        rescue => e
          logger.error(e,{backtrace: e.backtrace[0..4]})
          logger.end_task
        end
      end

      def self.request_body(child,hit)
        data = Hash.new
        doc = {doc: {flags: {child_data_processed: true}}}
	  	begin
        	if(child.dob)
          		time_then = Time.strptime(hit._source.ts,'%Y-%m-%dT%H:%M:%S%z')
          		age_then = time_then - child.dob
          		seconds_in_a_year = 31556952
          		completed_years = (age_then / seconds_in_a_year).floor
          		data[:age]= age_then
          		data[:dob]= child.dob
          		data[:age_completed_years]= completed_years
          		logger.info "-> UPDATE #{hit._source.eid} #{hit._id}"
        	end
        	data[:gender]=child.gender if child.gender
          data[:ekstep_id] = child.ekstep_id if child.ekstep_id
          data[:name] = child.name if child.name
        	doc[:doc][:udata]=data unless data.empty?
		rescue => e
			logger.error(e,{backtrace: e.backtrace[0..4]})
			logger.end_task
		ensure
			return doc
		end
      end
    end
  end
end
