require 'logger'
require 'elasticsearch'
require 'pry'
require 'hashie'

module Correctors
  class DevicesLdataCorrection
    def self.perform(index="ecosystem-*",type="devices_v1")
      begin
        file = "#{ENV['EP_LOG_DIR']}/#{self.name.gsub('::','')}.log"
        logger = Logger.new(file)
        @client = ::Elasticsearch::Client.new(log: false)
        @client.indices.refresh index: index
        logger.info "Starting search"
        response = @client.search({
          index: index,
          type: type,
          size: 100000,
          body: {
             "query" => {
                "constant_score" => {
                   "filter" => {
                      "exists" => {
                         "field" => "ldata.ldata"
                      }
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
          ldata = hit._source.ldata.ldata
          result = @client.update({
              index: hit._index,
              type: hit._type,
              id: hit._id,
              body: {
                doc: {
                  ldata: nil
                }
              }
            })
          logger.info "DEVICE #{result.to_json}"
          result = @client.update({
              index: hit._index,
              type: hit._type,
              id: hit._id,
              body: {
                doc: {
                  ldata: ldata
                }
              }
            })
          logger.info "DEVICE #{result.to_json}"
        end
        logger.info "Updated DEVICE"
      rescue => e
        logger.error e
      end
    end
  end
end

binding.pry
