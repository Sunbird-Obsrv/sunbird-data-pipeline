require 'logger'
require 'elasticsearch'
require 'pry'
require 'hashie'

module Processors
  class SignupProcessor
    def self.perform
      begin
      file = File.expand_path("./logs/logfile.log", File.dirname(__FILE__))
      logger = Logger.new(file)
      logger.info "STARTING SIGNUP SEARCH"
      @client = ::Elasticsearch::Client.new log: false
      response = @client.search({
        index: "_all",
        type: "events_v1",
        size: 1000,
        body: {
          "query"=> {
            "constant_score" => {
              "filter" => {
                "and"=> [
                  {
                    "term"=> {
                      "eid"=>"GE_SESSION_START"
                    }
                  },
                  {
                    "missing" => {
                      "field" => "flags.signup_processed"
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
      response.hits.hits.each do |hit|
        begin
          result = @client.create({
            index: hit._index,
            type: hit._type,
            id: hit._source.uid,
              body: {
              ts: hit._source.ts, #how early did she register
              ver: hit._source.ver,
              gdata: hit._source.gdata,
              sid: "",
              did: hit._source.did,
              uid: "",
              eid: "GE_SIGNUP",
              edata: {
                eks: {
                  uid: hit._source.uid,
                  err: ""
                }
              }
            }
          })
          logger.info "GE_SIGNUP #{result.to_json}"
        rescue Elasticsearch::Transport::Transport::Errors::Conflict => e
          logger.info "SKIPPING GE_SIGNUP! ALREADY SIGNED UP"
        end
        result = @client.update({
          index: hit._index,
          type: hit._type,
          id: hit._id,
          body: {
            doc: {
              flags:{
                signup_processed: true
              }
            }
          }
        })
        logger.info "GE_SESSION_START #{result.to_json}"
      end
      logger.info "ENDING SIGNUP SEARCH"
     rescue => e
      logger.error e
     end
    end
  end
end

