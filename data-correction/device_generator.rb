require 'logger'
require 'elasticsearch'
require 'pry'
require 'hashie'

module Correctors
  class DevicesCorrection
    def self.perform(index="ecosystem-*",type="events_v1")
      begin
        file = "#{ENV['EP_LOG_DIR']}/#{self.name.gsub('::','')}.log"
        logger = Logger.new(file)
        @client = ::Elasticsearch::Client.new(log: false)
        @client.indices.refresh index: index
        logger.info "Starting search"
        response = @client.search({
          index: index,
          type: type,
          size: 0,
          body: {
              "size" => 0,
              "aggs" => {
                  "did" => {
                      "terms" => {
                        "field" => "did",
                        "size" => 100000
                      }
                  }
              }
          }
        })
        response = ::Hashie::Mash.new response
        response.aggregations.did.buckets.each do |bucket|
          did = bucket["key"]
          response = @client.search({
            index: "ecosystem-identities",
            type: "devices_v1",
            size: 1,
            body: {
              "query" => {
                "ids" => {
                 "values" => [did]
                }
              }
            }
          })
          response = ::Hashie::Mash.new response
          logger.info "#{did} => #{response.hits.total}"
          if(response.hits.total==0)
            logger.info('creating devices_v1')
            response = @client.search({
              index: "ecosystem-*",
              type: "events_v1",
              size: 1,
              sort: 'ts:desc',
              body: {
                "query" => {
                  "constant_score" => {
                    "filter" => {
                      "and" => [
                        {
                          "term"=> {
                            "did"=> did
                          }
                        },
                        {
                            "not"=>{
                              "missing"=> {
                                "field"=> "edata.eks.ldata.country",
                                "existence"=> true,
                                "null_value"=> true
                              }
                          }
                        }
                      ]
                    }
                  }
                  }
              }
            })
            response = ::Hashie::Mash.new response
            if(response.hits.total==0)
              logger.info "No event found for device #{did}"
            else
              hit = response.hits.hits.first
              logger.info "indexing device #{did}"
              # binding.pry if(hit._source.edata.eks.nil?)
              begin
                result = @client.index(
                  index: 'ecosystem-identities',
                  type: 'devices_v1',
                  id: did,
                  body:{
                    ts: hit._source.ts,
                    did: did,
                    loc: hit._source.loc,
                    ldata: hit._source.edata.eks.ldata,
                    # dspec: hit._source.edata.eks.dspec
                  }
                )
                logger.info "device indexed #{result.to_json}"
              rescue => e
                logger.info "Error when creating device #{e.to_json}"
                binding.pry
              end
            end
          end
        end
      rescue => e
        logger.error e
      end
    end
  end
end

Correctors::DevicesCorrection.perform
