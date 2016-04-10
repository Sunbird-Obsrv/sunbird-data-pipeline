require 'hashie'
require 'elasticsearch'
require 'mysql2'
require 'yaml'

require_relative '../utils/ep_logging.rb'

PROCESSED_COUNT_THRESHOLD = 5
UID_SIZE = 20000

module EcosystemPlatform
  module Jobs
    class LearnerInsertion
      PROGNAME = 'learner_insertion.jobs.ep'
      include EcosystemPlatform::Utils::EPLogging

      def self.perform(index="ecosystem-*")
        begin
          logger.start_task
          logger.info("INITIALIZING CLIENT")
          db_config = YAML::load_file(File.expand_path('../../../../config/database.yml',__FILE__))
          @db_client = Mysql2::Client.new(db_config)
          @client = ::Elasticsearch::Client.new(host:ENV['ES_HOST']||'localhost',log: false)
          @client.indices.refresh index: index
          logger.info("SEARCHING LEARNERS TO INSERT")
          response = @client.search({
            index: index,
            type: 'events_v1',
            size: 0,
            body:
              {
                "size" => 0,
                "query" => {
                    "filtered" => {
                       "query" => {
                            "range" => {
                               "metadata.processed_count" => {
                                  "gte" => PROCESSED_COUNT_THRESHOLD
                               }
                            }
                       },
                       "filter" => {}
                    }
                },
                "aggs" => {
                    "users" => {
                        "cardinality" => {
                            "field" => "uid"
                        }
                    },
                    "uids" => {
                        "terms" => {
                            "field" => "uid",
                            "size" => UID_SIZE
                        }
                    }
                }
            }
          })
          response = Hashie::Mash.new response
          uid_count = response.aggregations.users.value
          logger.info "FOUND #{uid_count} hits."
          response.aggregations.uids.buckets.each do |bucket|
            uid = bucket["key"]
            logger.info("UID #{uid}")
            result = @db_client.query("SELECT * FROM learner where uid = '#{uid}'")
            if(result.size==0)
              logger.info "INSERTING #{uid}"
              @db_client.query("insert into learner (created_at, uid) VALUES ('#{Time.now}', '#{uid}');")
            else
              logger.info "NOTHING TO DO #{uid}"
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
