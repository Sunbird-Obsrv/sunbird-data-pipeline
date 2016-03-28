require_relative '../data-generator/session_generator.rb'

require 'digest/sha1'
require 'securerandom'
require 'pry'

API_ROOT = "http://#{ENV['API_HOST']||'localhost:8080'}"
API_USER="#{ENV['API_USER']||'ekstep'}"
API_PASS="#{ENV['API_PASS']||'i_dont_know'}"
TELEMETRY_SYNC_URL="#{API_ROOT}/v1/telemetry"

module LearnerGenerator
  class Learner
    include CommonSteps::ElasticsearchClient
    include CommonSteps::ProfileData

    attr_accessor :uid

    def initialize()
      @uid = SecureRandom.uuid
    end

    def create_event(deviceId)
      e=
      {
        edata: {
          eks: {
            uid: @uid
          }
        },
        eid: "GE_CREATE_USER",
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

    def udata
      {"handle" => nil, "standard" => 0, "age_completed_years" => 0, "gender" => "Not known", "is_group_user" => false}
    end

  end
end


