require 'logger'
require 'hashie'

module Processors
  class OeSummaryGenerator
    def self.perform(index="ecosystem-*",type="events_v1")
      begin
        file = File.expand_path("./logs/logfile.log", File.dirname(__FILE__))
        #TODO refac logging
        logger = Logger.new(file)
        logger.info "STARTING OE SUMMARIZER"
        @client = ::Elasticsearch::Client.new(host:ENV['ES_HOST']||'localhost:9200',log: false)
        #TODO remove this bad code
        @client.indices.refresh index: index
        response = @client.search({
          index: index,
          type: type,
          size: 1000000,
          body: {
            "query"=> {
              "constant_score" => {
                "filter" => {
                  "and"=> [
                    {
                      "term"=> {
                        "eid"=>"OE_ASSESS"
                      }
                    },
                    {
                      "missing" => {
                        "field" => "flags.summary_processed"
                      }
                    }
                  ]
                }
              }
            }
          }
        })
        response = Hashie::Mash.new response
        summary = {}
        logger.info "OE SUMMARIZER: FOUND #{response.hits.hits.count} hits."
        events = response.hits.hits
        events.each do |event|
          summary[event._source.sid] ||= Hashie::Mash.new({
            index: event._index,
            ts: event._source.ts,
            did: event._source.did,
            uid: event._source.uid,
            gdata: event._source.gdata,
            length: 0.0,
            correct: 0,
            incorrect: 0,
            attempted: 0,
            percent_correct: 0.0
          })
          summary[event._source.sid]['length']+=(event._source.edata.eks["length"]).to_f.round(2)
          summary[event._source.sid].attempted+=1
          if(event._source.edata.eks.pass.downcase=="yes")
            correct = summary[event._source.sid].correct+=1
          else
            incorrect = summary[event._source.sid].incorrect+=1
          end
          summary[event._source.sid].percent_correct=((summary[event._source.sid].correct)*100.0/summary[event._source.sid].attempted).round(2)
        end
        summary.each do |sid,data|
          payload = {
            index: data['index'],
            type: 'events_v1',
            id: "#{sid}",
            body: {
              ts: data.ts,
              "@timestamp" => Time.now.strftime('%Y-%m-%dT%H:%M:%S%z'),
              eid: 'OE_SUMMARY',
              did: data.did,
              sid: sid,
              uid: data.uid,
              gdata: data.gdata,
              edata: {
                eks: {
                  length: data['length'],
                  correct: data.correct,
                  incorrect: data.incorrect,
                  attempted: data.attempted,
                  percent_correct: data.percent_correct
                }
              }
            }
          }
          result = @client.index(payload)
          logger.info "OE SUMMARIZER: OE_SUMMARY #{result.to_json}"
        end
        logger.info "OE SUMMARIZER: OE_SUMMARY #{summary.keys.length}"
        events.each do |event|
          result = @client.update({
            index: event._index,
            type: event._type,
            id: event._id,
            body: {
              doc: {
                flags: {
                  summary_processed: true
                }
              }
            }
          })
          logger.info "OE SUMMARIZER: RESULT #{result.to_json}"
        end
      rescue => e
        logger.error "OE SUMMARIZER: ERROR in OE_SUMMARY GEN"
        logger.error e
      end
    end
  end
end

