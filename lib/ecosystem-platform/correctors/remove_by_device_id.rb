require 'pry'
require 'hashie'
require 'elasticsearch'
require 'mysql2'
require 'yaml'

require_relative '../utils/ep_logging.rb'

module EcosystemPlatform
  module Correctors
    class RemoveByDeviceId

      DEVICE_ID_TO_REMOVE = %w(
      )
      PROGNAME = 'remove_by_device_id.correctors.ep'
      N = 10000

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
                  "terms": {
                    "did": DEVICE_ID_TO_REMOVE
                  }
                }
              }
            })
            response = Hashie::Mash.new response
            logger.info "PAGE: #{page} - FOUND #{response.hits.hits.count} hits. - TOTAL #{response.hits.total}"
            to_delete = []
            to_debug = []
            response.hits.hits.each do |hit|
              uid = hit._source.uid
              doc = hit._source
              to_debug << {
                  index: {
                    _index: 'debug-removed-did',
                    _type: hit._type,
                    _id: hit._id,
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
            unless(to_debug.empty?)
              result = @client.bulk(body: to_debug)
              logger.info "<- DEBUG BULK INDEXED #{result['errors']==false}"
            end
            unless(to_delete.empty?)
              logger.info "DELETING #{to_delete.length} ELEMENTS"
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
