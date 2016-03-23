require 'pry'
require 'hashie'
require 'elasticsearch'
require 'base64'
require 'digest'
require 'kafka'

require_relative '../utils/ep_logging.rb'

module EcosystemPlatform
  module Jobs
    class OESessionSummarizer

      PROGNAME = 'summarizer.jobs.ep'
      N = 1000
      DATE_MASK = '%Y-%m-%d'
      VERSION = 1.0
      EVENT_VERSION = 1.0

      include EcosystemPlatform::Utils::EPLogging

      def self.perform(index="learning-*")
        begin
          logger.start_task
          logger.info "VERSION: #{VERSION}"
          @kafka = Kafka.new(logger: logger,seed_brokers: ENV['KAFKA_BROKERS'].split(','))
          @producer = @kafka.producer
          logger.info "INITIALIZING ES CLIENT"

          @client = ::Elasticsearch::Client.new(host:ENV['ES_HOST']||'localhost',log: false)
          @client.indices.refresh index: index
          env_sync_date = ENV['SYNC_DATES']
          dates = []
          if(env_sync_date)
            dates = env_sync_date.split(',').map{|date|DateTime.strptime(date,DATE_MASK)}
          else
            dates = [(Date.today-1)]
          end
          dates.sort!
          if(dates.length==2)
            dates = Range.new(dates.first,dates.last)
          end
          dates.each do |sync_date|
            begin
              logger.info "SYNC_DATE: #{sync_date}"
              event = Hashie::Mash.new
              event.ets = DateTime.now.strftime('%Q').to_i
              event.ver = EVENT_VERSION
              event.context = Hashie::Mash.new({
                granularity: 'DAILY',
                type: 'LEARNER_SESSION',
                pdata: {
                  id: 'AnalyticsDataPipeline',
                  mod: 'GenericSessionSummaryRollup',
                  ver: VERSION
                },
                date: {
                  day: sync_date.day,
                  month: sync_date.month,
                  year: sync_date.year
                }
              })
              event.edata = Hashie::Mash.new
              eks = Hashie::Mash.new
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

              event.length = response.took

              stats = Hashie::Mash.new
              eks.dimensions = []

              eks.count = response.hits.total
              stats.timeSpent = Hashie::Mash.new({sum:response.aggregations.totalTimeSpent.value})
              stats.timeDiff = Hashie::Mash.new({sum:response.aggregations.totalTimeDiff.value})
              eks.stats = stats

              device_dimension = Hashie::Mash.new(type: :device)
              content_dimension = Hashie::Mash.new(type: :content)
              device_dimension.unique = response.aggregations.distinctDevices.value
              content_dimension.unique = response.aggregations.distinctContent.value
              content_dimension.buckets = []

              eks.dimensions << device_dimension
              eks.dimensions << content_dimension

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
              event.edata.eks = eks
              logger.info "EVENTS SUMMARIZED - #{response.hits.total}"

            rescue => e
              event.error = e.message
              logger.error(e,{backtrace: e.backtrace[0..4]})
            end
            event.mid = Digest::SHA256.new.hexdigest eks.to_json
            begin
              @producer.produce(event.to_json, topic: ENV['KAFKA_TOPIC'], partition: 0)
              logger.info "PUBLISHING TO KAFKA #{event.mid} to #{ENV['KAFKA_TOPIC']}"
              @producer.deliver_messages
              if(ENV['DATA_FOLDER'])
                filename = "#{ENV['DATA_FOLDER']||''}/content-session-summarizer-#{event.context.date.day}-#{event.context.date.month}-#{event.context.date.year}.json"
                File.open(filename,"w") do |f|
                  f.write(event.to_json)
                end
              end
            rescue => e
              logger.error(e,{backtrace: e.backtrace[0..4]})
            end
            pp event.edata.eks["count"]
          end #end loop
          logger.end_task
        rescue => e
          logger.error(e,{backtrace: e.backtrace[0..4]})
          logger.end_task
        end
      end
    end
  end
end
