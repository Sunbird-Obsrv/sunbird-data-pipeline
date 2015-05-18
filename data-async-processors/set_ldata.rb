require 'logger'
require 'hashie'

module Processors
  class SetLdata
    def self.perform(index="ecosystem-*",type="events_v1")
      file = File.expand_path("./logs/logfile.log", File.dirname(__FILE__))
      logger = Logger.new(file)
      logger.info "STARTING LDATA SET"
      @client = ::Elasticsearch::Client.new log: false
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
                    "exists"=> {
                      "field"=>"sid"
                    }
                  },
                  {
                    "not"=>{
                      "term"=>{
                        "sid"=>""
                      }
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
      logger.info "FOUND #{response.hits.hits.count} hits."
      ldata_cache = Hashie::Mash.new
      response.hits.hits.each do |hit|
        sid = hit._source.sid
        ldata_from_cache = ldata_cache.did
        if ldata_from_cache.nil?
          begin
            _index=index
          response = @client.search({
            index: _index,
            body: {
              query: {
                bool:{
                  must: [
                    {
                      term: {
                        eid: "GE_SESSION_START"
                      }
                    },
                    {
                      term: {
                        sid: sid
                      }
                    }
                  ]
                }
              }
            }
          })
          response = Hashie::Mash.new response
          raise Elasticsearch::Transport::Transport::Errors::NotFound if response.hits.total==0
          rescue Elasticsearch::Transport::Transport::Errors::NotFound => e
            logger.error "NOT FOUND - SKIPPING"
            next
          end
          ldata_cache[sid] = response.hits.hits.first._source rescue nil
        end
        cache = ldata_cache[sid]
        if(cache)
          edata = {
            loc: cache.edata.eks.loc,
            edata: {
              eks: {
                ldata: cache.edata.eks.ldata
              }
            }
          }
          logger.info "SID #{sid}"
          logger.info "EDATA #{edata.to_json}"
          result = @client.update({
            index: hit._index,
            type: hit._type,
            id: hit._id,
            body: {
              doc: edata
            }
          })
          logger.info "RESULT #{result.to_json}"
        else
          logger.info "NOTHING DONE FOR #{sid} SID"
        end
      end
      logger.info "ENDING LDATA SET"
    end
  end
end
