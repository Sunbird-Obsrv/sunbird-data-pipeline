require 'logger'
require 'hashie'

module Processors
  class OeSummaryGenerator
    def self.perform(index="ecosystem-*",type="events_v1")
      begin
        file = File.expand_path("./logs/logfile.log", File.dirname(__FILE__))
        logger = Logger.new(file)
        logger.info "STARTING OE SUMMARIZER"
        @client = ::Elasticsearch::Client.new(host: '52.74.22.23:9200',log: false)
        response = @client.search({
          index: index,
          type: type,
          size: 1000,
          body: {
            query: {
              match: {
                eid: "OE_ASSESS"
              }
            }
          }
        })
        response = Hashie::Mash.new response
        summary = {}
        logger.info "FOUND #{response.hits.hits.count} hits."
        response.hits.hits.each do |event|
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
          summary[event._source.sid].length+=(event._source.edata.eks["length"]).round(2)
          summary[event._source.sid].attempted+=1
          if(event._source.edata.eks.pass=="YES")
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
            id: "#{sid}-#{data.gdata.id}-#{data.gdata.ver}-#{data['length']}}",
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
          logger.info "RESULT #{result.to_json}"
        end
      end
    end
  end
end

