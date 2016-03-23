require 'pry'
require 'hashie'
require 'elasticsearch'
require 'base64'

require_relative '../utils/ep_logging.rb'

module EcosystemPlatform
  module Jobs
    class OESessionSummarizer

      PROGNAME = 'summarizer.jobs.ep'
      N = 1000
      DATE_MASK = '%Y-%m-%d'
      VERSION = 1.0

      include EcosystemPlatform::Utils::EPLogging

      def self.perform(index="learning-*")
        begin
          logger.start_task
          logger.info "VERSION: #{VERSION}"
          logger.info "INITIALIZING ES CLIENT"
          summary = Hashie::Mash.new
          summary.dateMask = 'YYYY-MM-DD'
          summary.error = nil
          summary.summary = Hashie::Mash.new
          summary.summary.type = 'session'
          @client = ::Elasticsearch::Client.new(host:ENV['ES_HOST']||'localhost',log: false)
          @client.indices.refresh index: index
          sync_date = ENV['SYNC_DATE']||(Date.today-1).strftime(DATE_MASK)
          logger.info "SYNC_DATE: #{sync_date}"
          summary.date = sync_date
          sync_date = DateTime.strptime(sync_date,DATE_MASK)
          sync_date_epoch_ms_start = sync_date.strftime('%Q').to_i
          sync_date_epoch_ms_stop = (sync_date+1).strftime('%Q').to_i
          logger.info("SEARCHING EVENTS TO SUMMARIZE")
          response = @client.search({
            index: index,
            type: 'events_v1',
            body: {
              "size" => 0,
              "query"=> {
                "filtered"=> {
                   "query"=> {
                        "match_all": {}
                   },
                   "filter"=> {
                       "range"=> {
                          "ts"=> {
                             "from"=> sync_date_epoch_ms_start,
                             "to"=> sync_date_epoch_ms_stop
                          }
                       }
                   }
                }
              },
            aggregations: {
              "totalTimeSpent"=> {
                "sum"=> {
                  "field"=> "edata.eks.timeSpent"
                  }
                },
                "totalTimeDiff"=> {
                  "sum"=> {
                      "field"=> "edata.eks.timeDiff"
                  }
                },
                "distinctContent" => {
                    "cardinality" => {
                        "field" => "dimensions.gdata.id"
                     }
                } ,
                "distinctDevices" => {
                    "cardinality" => {
                        "field" => "dimensions.did"
                     }
                },
                "contents" => {
                    "terms" => {
                        "field" => "dimensions.gdata.id",
                        "size"=> N
                    }
                },
                "statsContent" => {
                    "terms" => {
                        "field" => "dimensions.gdata.id",
                        "size"=> N,
                        "order" => { "timeSpent.sum" => "desc" }
                    },
                    "aggs" => {
                        "timeSpent" => { "stats" => { "field" => "edata.eks.timeSpent" } }
                    }
                },
                "statsInteraction" => {
                    "terms" => {
                        "field" => "dimensions.gdata.id",
                        "size"=> N,
                        "order" => { "interactEventsPerMin.avg" => "desc" }
                    },
                    "aggs" => {
                        "interactEventsPerMin" => { "stats" => { "field" => "edata.eks.interactEventsPerMin" } }
                    }
                }
              }
            }
          })
          response = Hashie::Mash.new response

          stats = Hashie::Mash.new
          summary.dimensions = []

          summary.took = response.took
          summary.summary.count = response.hits.total
          stats.timeSpent = Hashie::Mash.new({sum:response.aggregations.totalTimeSpent.value})
          stats.timeDiff = Hashie::Mash.new({sum:response.aggregations.totalTimeDiff.value})
          summary.summary.stats = stats

          device_dimension = Hashie::Mash.new(type: :device)
          content_dimension = Hashie::Mash.new(type: :content)
          device_dimension.unique = response.aggregations.distinctDevices.value
          content_dimension.unique = response.aggregations.distinctContent.value
          content_dimension.buckets = []

          summary.dimensions << device_dimension
          summary.dimensions << content_dimension

          stats_dictionary = {}
          response.aggregations.statsInteraction.buckets.each do |bucket|
            stats_dictionary[bucket[:key]] ||= {}
            stats_dictionary[bucket[:key]][:interactEventsPerMin] = bucket.interactEventsPerMin
          end
          response.aggregations.statsContent.buckets.each do |bucket|
            stats_dictionary[bucket[:key]] ||= {}
            stats_dictionary[bucket[:key]][:timeSpent] = bucket.timeSpent
          end
          response.aggregations.contents.buckets.each do |bucket|
            content_bucket = {
              bucket: bucket[:key],
              count: bucket.doc_count,
              stats: {
                interactEventsPerMin: stats_dictionary[bucket[:key]][:interactEventsPerMin],
                timeSpent: stats_dictionary[bucket[:key]][:timeSpent]
              }
            }
            content_dimension.buckets << content_bucket
          end

          logger.info "EVENTS SUMMARIZED - #{response.hits.total}"
        rescue => e
          summary.error = e.message
          logger.error(e,{backtrace: e.backtrace[0..4]})
        end
        begin
          filename = "#{ENV['DATA_FOLDER']||''}/content-session-summarizer-#{summary.date}.json"
          File.open(filename,"w") do |f|
            f.write(summary.to_json)
          end
        rescue => e
          logger.error(e,{backtrace: e.backtrace[0..4]})
        end
        logger.end_task
      end
    end
  end
end
