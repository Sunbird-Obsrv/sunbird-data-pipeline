require 'csv'
require 'pry'
require 'logger'
require 'elasticsearch'
# require 'hashie'
# require_relative '../data-indexer/indexers.rb'

module Uploaders
  module Numeracy
    class Assessment
      TOTAL_QUESTIONS = 25
      TUTORIAL_QUESTIONS = 2
      TERMINATION_THRESHOLD = 7 + TUTORIAL_QUESTIONS
      CSV_FILE = 'Numeracy_Assessment.csv'
      def self.client
        @client ||= ::Elasticsearch::Client.new log: false
      end
      def self.setup
        client.indices.delete index: 'game_data' rescue nil
        client.indices.put_template({
          name: 'events',
          body: {
            template: '*',
            settings: {
              'index.number_of_shards' => 1
            },
            mappings: {
              game_events_v1:{
                properties: {
                  ts: { type: 'date' },
                  eid: { type: 'string', index: 'not_analyzed'},
                  did: { type: 'string', index: 'not_analyzed'},
                  sid: { type: 'string', index: 'not_analyzed'},
                  gdata: {
                    type: 'nested',
                    include_in_parent: true,
                    properties: {
                      id: { type: 'string', index: 'not_analyzed'},
                      ver: { type: 'string', index: 'not_analyzed'},
                    }
                  },
                  edata: {
                    type: 'nested',
                    include_in_parent: true,
                    properties: {
                      eks: {
                        type: 'nested',
                        include_in_parent: true,
                        properties: {
                          pmode: { type: 'string', index: 'not_analyzed'},
                          age: { type: 'double' },
                          lang: { type: 'string', index: 'not_analyzed'},
                          length: { type: 'double'},
                          score: { type: 'double' },
                          correct: { type: 'long' },
                          incorrect: { type: 'long' },
                          attempted: { type: 'long' },
                          percent_correct: { type: 'double' },
                          percent_attempt: { type: 'double' },
                          result: { type: 'string', index: 'not_analyzed'},
                          total: { type: 'long' },
                          terminated: { type: 'boolean' },
                          completed: { type: 'boolean' },
                          attempt: { type: 'string', index: 'not_analyzed'},
                          qid: { type: 'string', index: 'not_analyzed'},
                          pass: { type: 'string', index: 'not_analyzed'}
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        })
        client.indices.create({ index: 'game_data' })
      end
      def self.extract_length(time_str)
        hours, minutes, seconds = time_str.split(":").map{|str| str.to_i}
        (hours * 60 + minutes + seconds / 60.0).round(2)
      end
      def self.upload
        setup
        logger = Logger.new(File.expand_path("../logs/upload.log", File.dirname(__FILE__)))
        logger.info("CSV #{CSV_FILE}")
        sessions_done = {}
        CSV.foreach("../CSV/#{CSV_FILE}", headers: true) do |row|
          logger.info "ROW #{row.to_hash}"
          # {
          #   "Device ID"=>"CD38YBIH",
          #  "Build Date"=>"2015-04-02",
          #  "Cookie Ver"=>"13",
          #  "Session ID"=>"WXDIY",
          #  "Play Mode"=>"play all",
          #  "Language"=>"en-us",
          #  "Event ID"=>"T_Tutorial",
          #  "Time Taken"=>"00:00:13",
          #  "Result"=>"Correct",
          #  "Session Total Time"=>"00:15:19",
          #  "Total Correct"=>"11",
          #  "Total Wrong"=>"14"
          # }
          # ts = row[]
          ts = DateTime.strptime(row["Build Date"],'%Y-%m-%d').strftime('%Q')
          did = row['Device ID']
          sid = row['Session ID']
          uid = row['Session ID'] #?
          pmode = row['Play Mode']
          age = row["Age"]
          lang = row['Language']
          qid = row['Event ID']
          qresult = row['Result']
          qlength = extract_length row['Time Taken']
          qscore = (qresult == 'Correct' ? 1 : 0)
          stime = ((extract_length row["Session Total Time"])/60.0).round(2)
          tcorrect = row["Total Correct"].to_i
          tincorrect = row["Total Wrong"].to_i
          # binding.pry
          if(pmode=='diagnostic map')
            terminated = tincorrect>=TERMINATION_THRESHOLD
          else
            terminated = false
          end
          tattempted = tcorrect+tincorrect
          completed = tattempted==TOTAL_QUESTIONS

          if completed
            attempt = 'Completed'
          else
            if terminated
              attempt = 'Terminated'
            else
              attempt = 'Abandoned'
            end
          end

          if sessions_done[sid].nil?
            result=client.index({
              index: 'game_data',
              type: 'game_events_v1',
              body: {
                ts: ts,
                eid: 'OE_GAME_END',
                did: did,
                sid: sid,
                uid: uid,
                gdata: {
                  id: "numeracy_screener",
                  ver: "1.0"
                },
                edata: {
                  eks: {
                    length: stime,
                    pmode: pmode,
                    age: age,
                    lang: lang,
                    correct: tcorrect,
                    incorrect: tincorrect,
                    attempted: tattempted,
                    percent_correct: tcorrect*100.0/TOTAL_QUESTIONS,
                    percent_attempt: (tattempted)*100.0/TOTAL_QUESTIONS,
                    terminated: terminated,
                    completed: completed,
                    attempt: attempt
                  }
                }
              }
            })
            logger.info("OE_GAME_END #{result}")
          end
          sessions_done[sid] = true
          result=client.index({
            index: 'game_data',
            type: 'game_events_v1',
            body: {
              ts: ts,
              eid: 'OE_ASSESS',
              did: did,
              sid: sid,
              uid: uid,
              gdata: {
                id: "numeracy_screener",
                ver: "1.0"
              },
              edata: {
                eks: {
                  pmode: pmode,
                  age: age,
                  qid: qid,
                  pass: qresult,
                  length: qlength
                }
              }
            }
          })
          logger.info("OE_ASSESS #{result}")
        end
      end
    end
  end
end
