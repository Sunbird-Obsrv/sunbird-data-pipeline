require 'pry'
require 'hashie'
require 'elasticsearch'
require 'mysql2'
require 'yaml'

require_relative '../utils/ep_logging.rb'

module EcosystemPlatform
  module Jobs
    class ChecksumGenerator

      PROGNAME = 'checksum_generator.jobs.ep'
      N = 10000
      KEYS_TO_REJECT = %w()

      include EcosystemPlatform::Utils::EPLogging

      def self.perform(index="ecosystem-*")
        begin
          logger.start_task
          logger.info("INITIALIZING CLIENT")
          # TODO Terrible terrible
          # will be replaced by a config module
          @client = ::Elasticsearch::Client.new(host:ENV['ES_HOST']||'localhost',log: false)
          @client.indices.refresh index: index
          logger.info("SEARCHING EVENTS TO CHECKSUM")
          page = 0
          offset = 0
          loop do
            response = @client.search({
              index: index,
              type: 'events_v1',
              body: {
                "from" => offset,
                "size" => N,
                "query"=> {
                  "constant_score": {
                     "filter": {
                      "missing": {
                         "field": "metadata.checksum"
                      }
                     }
                  }
                }
              }
            })
            response = Hashie::Mash.new response
            logger.info "PAGE: #{page} - FOUND #{response.hits.hits.count} hits. - TOTAL #{response.hits.total}"
            to_index = []
            to_delete = []
            response.hits.hits.each do |hit|
              uid = hit._source.uid
              doc = hit._source.reject{|z| KEYS_TO_REJECT.include?z }
              checksum = Digest::SHA1.hexdigest(Marshal::dump(doc))
              doc["metadata"] = {
                checksum: checksum,
                original_id: hit._id
              }
              # reducing partitions to monthly
              _index = hit._index
              _split_on_index = _index.split(".")
              if(_split_on_index.length==3)
                _split_on_index.pop
                _index = _split_on_index.join(".")
              end
              to_index << {
                  index: {
                    _index: _index,
                    _type: hit._type,
                    _id: checksum,
                    data: doc
                  }
                }
              to_delete << {
                delete: {
                  _index: hit._index,
                  _type: hit._type,
                  _id: hit._id
                }
              }
            end
            unless(to_index.empty?)
              result = @client.bulk(body: to_index)
              logger.info "<- BULK INDEXED #{result['errors']==false}"
            end
            unless(to_delete.empty?)
              result = @client.bulk(body: to_delete)
              logger.info "<- BULK DELETED #{result['errors']==false}"
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
