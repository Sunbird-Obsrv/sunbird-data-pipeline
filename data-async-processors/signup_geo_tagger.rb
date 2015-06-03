require 'logger'
require 'elasticsearch'
require 'hashie'

module Processors
  class SignupGeoTagger
    def self.perform(index="ecosystem-*",type="events_v1")
      begin
        file = "#{ENV['EP_LOG_DIR']}/#{self.name.gsub('::','')}.log"
        logger = Logger.new(file)
        logger.info "STARTING GE SIGNUP WITH LOCATION SEARCH"
        @client = ::Elasticsearch::Client.new(host:ENV['ES_HOST']||'localhost:9200',log: false)
        @client.indices.refresh index: index
        response = @client.search({
          index: index,
          type: type,
          size: 1000,
          body: {
            "query"=> {
              "constant_score" => {
                "filter" => {
                  "and"=> [
                    {
                      "term"=> {
                        "eid"=>"GE_SIGNUP"
                      }
                    },
                    {
                      "missing" => {
                        "field" => "edata.eks.ldata.country",
                        "existence" => true,
                        "null_value" => true
                      }
                    }
                  ]
                }
              }
            }
          }
        })
        response = Hashie::Mash.new response
        logger.info "GE SIGNUP LOCATION TAG: FOUND #{response.hits.hits.count} hits."
        device_cache = {}
        response.hits.hits.each do |hit|
          did = hit._source.did
          cached_device = self.get_device(index,device_cache,did)
          if cached_device
            logger.info "DEVICE #{did} FOUND"
            ldata = cached_device._source.ldata
            loc = cached_device._source.loc
            logger.info "LOC #{loc.to_json}"
            logger.info "LDATA #{ldata.to_json}"
            logger.info "---#{(loc && ldata.locality)}---"
            if(loc && ldata.locality)
              result = @client.update({
                index: hit._index,
                type: hit._type,
                id: hit._id,
                body: {
                  doc: {
                    loc: loc,
                    edata: {
                      eks: {
                        ldata: ldata
                      }
                    }
                  }
                }
              })
              logger.info "GE SIGNUP LOCATION TAG: GE_SIGNUP #{result.to_json}"
            end
          else
            logger.info "GE SIGNUP LOCATION TAG: DEVICE #{did} NOT FOUND!"
          end
        end
        logger.info "ENDING GE SIGNUP WITH LOCATION SEARCH"
      rescue => e
        logger.error e
      end
    end
    def self.get_device(index,device_cache,did)
      cached_device = device_cache[did]
      if(cached_device.nil?)
        device = Hashie::Mash.new @client.search({
          index: index,
          type: "devices_v1",
          size: 1,
          body: {query:{term:{did:did}}}
        })
        cached_device = device.hits.hits.first
        device_cache[did] = cached_device
      end
      cached_device
    end
  end
end

