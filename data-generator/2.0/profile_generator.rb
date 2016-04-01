require_relative '../2.0/session_generator.rb'

require 'digest/sha1'
require 'securerandom'
require 'pry'

API_ROOT = "http://#{ENV['API_HOST']||'localhost:8080'}"
API_USER="#{ENV['API_USER']||'ekstep'}"
API_PASS="#{ENV['API_PASS']||'i_dont_know'}"
TELEMETRY_SYNC_URL="#{API_ROOT}/v1/telemetry"

module ProfileGenerator
  class Profile
    include CommonSteps::ElasticsearchClient
    attr_reader :uid, :handle, :gender, :age, :standard, :language, :day, :month

    def initialize(handle, gender, age, standard, day, month, language="en", uid=SecureRandom.uuid)
      @uid = uid
      @handle = handle
      @gender = gender
      @age = age
      @standard = standard
      @day = day
      @month = month
      @language = language
    end

    def update_attributes(attributes = {})
      @handle = attributes["handle"] if attributes["handle"]
      @age = attributes["age"] if attributes["age"] 
      @gender = attributes["gender"] if attributes["gender"]
      @standard = attributes["standard"] if attributes["standard"]
      @day = attributes["day"] if attributes["day"]
      @month = attributes["month"] if attributes["month"]
      @language = attributes["language"] if attributes["language"]
    end

    def create_event(deviceId,newVer)
      e=
      {
        edata: {
          eks: {
            loc: "",
            uid: @uid,
            age: @age,
            handle: @handle,
            standard: @standard,
            language: @language,
            gender: @gender,
            day: @day,
            month: @month
          }
        },
        eid: "GE_CREATE_PROFILE",
        mid: SecureRandom.uuid,
        did: deviceId,
        gdata: {
          id: "genieservice.android",
          ver: "1.0.local-qa-debug"
        },
        sid: "",
        tags: [],
        uid: @uid,
        ver: "2.0",
        ets: DateTime.now.strftime("%Q").to_i
      }
      return e
    end

    def update_event(deviceId,newVer)
      e=
      {
        edata: {
          eks: {
            uid: @uid,
            age: @age,
            handle: @handle,
            standard: @standard,
            language: @language,
            gender: @gender,
            day: @day,
            month: @month
          }
        },
        eid: "GE_UPDATE_PROFILE",
        mid: SecureRandom.uuid,
        did: deviceId,
        gdata: {
          id: "genieservice.android",
          ver: "1.0.local-qa-debug"
        },
        sid: "",
        tags: [],
        uid: @uid,
        ver: "2.0",
        ets: DateTime.now.strftime("%Q").to_i
      }
      return e
    end

    def post_event(deviceId, newVer = false)
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
      e = create_event(deviceId,newVer)
      wrapper[:events] << e
      post_request(wrapper)
    end

    def post_update_event(deviceId,newVer = false)
      wrapper={
            id: "ekstep.telemetry",
            ver: newVer ? "2.0" : "1.0",
            ts: Time.now.strftime('%Y-%m-%dT%H:%M:%S%z'),
            params: {
              did: deviceId,
              key: "",
              msgid: Digest::SHA1.hexdigest(Time.now.strftime('%Y-%m-%dT%H:%M:%S%z')).upcase
            },
            events: []
          }
      e = update_event(deviceId,newVer)
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
      {"handle" => @handle, "standard" => @standard, "age_completed_years" => @age, "gender" => @gender, "is_group_user" => false}
    end

  end
end


