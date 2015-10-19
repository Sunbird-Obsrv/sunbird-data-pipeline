require 'pry'
require 'hashie'
require 'elasticsearch'
require 'mysql2'
require 'yaml'

require_relative '../utils/ep_logging.rb'

module EcosystemPlatform
  module Jobs
    class DataTransferer

      PROGNAME = 'data_transferer.jobs.ep'
      N = 10000
      VALID_KEYS = Regexp.union(/ecosystem-\d\d\d\d\.\d\d/, /ecosystem-identities/)
      

      include EcosystemPlatform::Utils::EPLogging

      def self.perform(index="ecosystem-*")
        begin
          logger.start_task
          logger.info("INITIALIZING CLIENT")
          @reader = ::Elasticsearch::Client.new(host:ENV['ES_HOST_OLD'],log: false)
          @writer = ::Elasticsearch::Client.new(host:ENV['ES_HOST_NEW'],log: false)
          @reader.indices.refresh index: index
          logger.info("SEARCHING EVENTS TO TRANSFER in #{index}")
          page = 0
          offset = 0
          loop do
            response = @reader.search({
              index: index,
              body: {
                "from" => offset,
                "size" => N,
                "query"=> {
                  "match_all" => {}
                }
              }
            })
            response = Hashie::Mash.new response
            logger.info "PAGE: #{page} - FOUND #{response.hits.hits.count} hits. - TOTAL #{response.hits.total}"
            to_index = []
            response.hits.hits.each do |hit|
              uid = hit._source.uid
              doc = hit._source
              # reducing partitions to monthly
              _index = hit._index
              next unless VALID_KEYS.match(_index).to_s.eql?(_index) 
              _split_on_index = _index.split(".")
              if(_split_on_index.length==3)
                _split_on_index.pop
                _index = _split_on_index.join(".")
              end
              to_index << {
                  index: {
                    _index: _index,
                    _type: hit._type,
                    _id: hit._id,
                    data: doc
                  }
                }
            end
            unless(to_index.empty?)
              result = @writer.bulk(body: to_index)
              logger.info "<- BULK INDEXED #{result['errors']==false}"
            end
            offset += N
            page += 1
            break if response.hits.hits.count == 0
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
