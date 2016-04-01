require_relative '../1.0/session_generator.rb'

require 'digest/sha1'
require 'securerandom'
require 'pry'

API_ROOT = "http://#{ENV['API_HOST']||'localhost:8080'}"

DATA_EXHAUST_USER_SIGNUP_API_URL="#{API_ROOT}/v1/user"
DATA_EXHAUST_USER_VALIDATE_API_URL="#{API_ROOT}/v1/user/authenticate"

module DataExhaustUserGenerator
  class User
    attr_accessor :username, :licensekey
    def initialize(username)
      @username = username
      @licensekey = SecureRandom.uuid
    end

    def new_user_request
      e=
        {
          id: "ekstep.data_exhaust_user",
          ver: "1.0",
          ts: Time.now.strftime('%Y-%m-%dT%H:%M:%S%z'),
          params:{
            requesterid: "",
            did: "",
            key: "",
            msgid: SecureRandom.uuid,
          },
          request: {
            username: @username,
          }
        }
    end

    def post_new_user_request
      data = new_user_request
      uri = URI.parse(DATA_EXHAUST_USER_SIGNUP_API_URL)
      http = Net::HTTP.new(uri.host, uri.port)
      if DATA_EXHAUST_USER_SIGNUP_API_URL.start_with? "https"
        http.use_ssl = true
      end
      req = Net::HTTP::Post.new(uri.path, initheader = {'Content-Type' =>'application/json'})
      req.body = JSON.generate(data)
      res = http.request(req)
      res
    end

    def validate_request
      ts = Time.now.strftime('%Y-%m-%dT%H:%M:%S%z')
      e=
        {
          id: "ekstep.data_exhaust_user_validate",
          ver: "1.0",
          ts: ts,
          params:{
            requesterid: "",
            did: "",
            key: "",
            msgid: SecureRandom.uuid,
          },
          request: {
            licensekey: @licensekey,
          }
        }
    end

    def post_validate_request
      data = validate_request
      uri = URI.parse(DATA_EXHAUST_USER_VALIDATE_API_URL)
      http = Net::HTTP.new(uri.host, uri.port)
      if DATA_EXHAUST_USER_VALIDATE_API_URL.start_with? "https"
        http.use_ssl = true
      end
      req = Net::HTTP::Post.new(uri.path, initheader = {'Content-Type' =>'application/json'})
      req.body = JSON.generate(data)
      res = http.request(req)
      res
    end

  end
end
