require_relative '../data-generator/session_generator.rb'

require 'digest/sha1'
require 'securerandom'
require 'pry'

API_ROOT = "http://#{ENV['API_HOST']||'localhost:8080'}"
API_USER="#{ENV['API_USER']||'ekstep'}"
API_PASS="#{ENV['API_PASS']||'i_dont_know'}"
TELEMETRY_SYNC_URL="#{API_ROOT}/v1/telemetry"

module EventGenerator
  class Event
    include CommonSteps::ElasticsearchClient
    
    attr_accessor :uid

    def initialize
      @uid = SecureRandom.uuid
    end
    
    def create_event(deviceId)
      e=
        {
          edata: {
            eks: 
            {
              exres: [],
              length: 4.483398,
              maxscore: 1,
              mc: [
                "LO10"
              ],
              mmc: [],
              pass: "No",
              qid: "EK.L.KAN.LT12.Q7",
              qlevel: "Easy",
              qtype: "FTB",
              res: [],
              score: 0,
              subj: "LIT",
              uri: " "
            } 
          },
          eid: "OE_ASSESS",
          did: deviceId,
          gdata: {
            id: "genieservice.android",
            ver: "1.0.local-qa-debug"
          },
          sid: "",
          tags: [],
          ts: Time.now.strftime('%Y-%m-%dT%H:%M:%S%z'),
          uid: @uid,
          ver: "1.0"
       
      }
      return e
    end

    def post_event(deviceId)
      wrapper={
        id: "ekstep.telemetry",
        ver: "1.0",
        ts: Time.now.strftime('%Y-%m-%dT%H:%M:%S%z'),
        params: {
          did: deviceId,
          key: "",
          msgid: Digest::SHA1.hexdigest(Time.now.strftime('%Y-%m-%dT%H:%M:%S%z')).upcase
        },
        events: []
      }

      e = create_event(deviceId)
      wrapper[:events] << e
      post_request(wrapper)
    end

    def post_request(data)
      uri = URI.parse(TELEMETRY_SYNC_URL)
      http = Net::HTTP.new(uri.host, uri.port)
      if TELEMETRY_SYNC_URL.start_with? "https"
        http.use_ssl = true
      end
      req = Net::HTTP::Post.new(uri.path, initheader = {'Content-Type' =>'application/json'})
      req.basic_auth API_USER, API_PASS
      req.body = JSON.generate(data)
      res = http.request(req)
      res
    end

    def self.post_set_of_events(deviceId, events)
      wrapper={
        id: "ekstep.telemetry",
        ver: "1.0",
        ts: Time.now.strftime('%Y-%m-%dT%H:%M:%S%z'),
        params: {
          did: deviceId,
          key: "",
          msgid: Digest::SHA1.hexdigest(Time.now.strftime('%Y-%m-%dT%H:%M:%S%z')).upcase
        },
        events: events
      }
     
      uri = URI.parse(TELEMETRY_SYNC_URL)
      http = Net::HTTP.new(uri.host, uri.port)
      if TELEMETRY_SYNC_URL.start_with? "https"
        http.use_ssl = true
      end
      req = Net::HTTP::Post.new(uri.path, initheader = {'Content-Type' =>'application/json'})
      req.basic_auth API_USER, API_PASS
      req.body = JSON.generate(wrapper)
      res = http.request(req)
      res
    end
  end
end