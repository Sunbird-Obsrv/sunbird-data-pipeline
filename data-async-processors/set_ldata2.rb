require 'logger'
require 'hashie'

module Processors
  class SetLdata2
    def self.perform(index="ecosystem-*",type="events_v1")
      file = "#{ENV['EP_LOG_DIR']}/#{self.name.gsub('::','')}.log"
      logger = Logger.new(file)
      logger.info "STARTING LDATA SET"
      @client = ::Elasticsearch::Client.new(host:ENV['ES_HOST']||'localhost:9200',log: false)
      response = @client.search({
        index: index,
        type: type,
        size: 100000,
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
                  },
                  {
                    "missing" => {
                      "field" => "flags.ldata_processed"
                    }
                  }
                ]
              }
            }
          }
        }
      })
      response = Hashie::Mash.new response
      logger.info "LDATA SET: FOUND #{response.hits.hits.count} hits."
      ldata_cache = Hashie::Mash.new
      response.hits.hits.each do |hit|
        sid = hit._source.sid
        ldata_from_cache = ldata_cache[sid]
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
            logger.error "LDATA SET: NOT FOUND - SKIPPING"
            result = @client.update({
              index: hit._index,
              type: hit._type,
              id: hit._id,
              body: {
                doc: {
                  flags: {
                    ldata_processed: true
                  }
                }
              }
            })
            next
          end
          ldata_cache[sid] = response.hits.hits.first._source rescue nil
        end
        cache = ldata_cache[sid]
        loc = cache.edata.eks.loc rescue nil
        if(cache&&loc)
          edata = {
            loc: loc,
            edata: {
              eks: {
                ldata: cache.edata.eks.ldata
              }
            },
            flags: {
              ldata_processed: true
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
          logger.info "LDATA SET: RESULT #{result.to_json}"
        else
          result = @client.update({
            index: hit._index,
            type: hit._type,
            id: hit._id,
            body: {
              doc: {
                flags: {
                  ldata_processed: true
                }
              }
            }
          })
          logger.info "LDATA SET: NOTHING DONE FOR #{sid} SID: cache #{cache} loc #{loc}"
        end
      end
      logger.info "ENDING LDATA SET"
    end
  end
end
