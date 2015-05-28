require 'logger'
require 'hashie'

module Processors
  class SetLdata
    def self.perform(index="ecosystem-*",type="events_v1")
      file = File.expand_path("./logs/logfile.log", File.dirname(__FILE__))
      logger = Logger.new(file)
      logger.info "STARTING LDATA SET"
      @client = ::Elasticsearch::Client.new(log: false)
      response = @client.search({
        index: index,
        type: type,
        size: 1000000,
        body: {
          "query"=> {
            "constant_score" => {
              "filter" =>
                {
                "and" => {
                  "filters" => [
                    {
                      "exists" => {
                        "field" => "did"
                      }
                    },
                    {
                      "not" => {
                        "filter" => {
                          "term" => {
                            "did" => ""
                          }
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
              },
            }
          }
        }
      })
      response = Hashie::Mash.new response
      logger.info "FOUND #{response.hits.hits.count} hits."
      ldata_cache = Hashie::Mash.new
      response.hits.hits.each do |hit|
        did = hit._source.did
        if(did.empty?)
          logger.error "BLANK did - SKIPPING #{hit.to_json}"
        else
          ldata_from_cache = ldata_cache[did]
          if ldata_from_cache.nil?
            begin
              _index='ecosystem-identities'
              response = @client.get({
                index: _index,
                type: 'devices_v1',
                id: did
              })
            rescue Elasticsearch::Transport::Transport::Errors::NotFound => e
              logger.error "NOT FOUND did<#{did}> - SKIPPING"
              next
            rescue => e
              logger.error "BAD ERROR: #{e} did<#{did}>"
              next
            end
            response = Hashie::Mash.new response
            ldata_cache[did] = response._source
          end
          edata = {
            loc: ldata_cache[did].loc,
            edata: {
              eks: {
                ldata: ldata_cache[did].ldata.ldata||ldata_cache[did].ldata
              }
            }
          }
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
        end
      end
      logger.info "ENDING LDATA SET"
    end
  end
end
