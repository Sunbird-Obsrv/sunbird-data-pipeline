require_relative '../1.0/session_generator.rb'

require 'digest/sha1'
require 'securerandom'
require 'pry'

API_ROOT = "http://#{ENV['API_HOST']||'localhost:8080'}"

DATA_EXHAUST_REGISTER_CLIENT_API_URL="#{API_ROOT}/v1/client"
DATA_EXHAUST_AUTHENTICATE_CLIENT_API_URL="#{API_ROOT}/v1/client/authenticate"

module DataExhaustClientGenerator
  class Client
    attr_accessor :name, :licensekey, :licensekeyname
    def initialize(name,license_key_name)
      @name = name
      @licensekey = SecureRandom.uuid
      @licensekeyname =  license_key_name
    end

    def new_client_request
      e=
        {
          id: "ekstep.data_exhaust_user",
          ver: "1.0",
          ts: Time.now.strftime('%Y-%m-%dT%H:%M:%S%z'),
          params:{
            requesterid: "",
            did: "",
            key: "",
            msgid: SecureRandom.uuid
          },
          request: {
            clientName: name,
            licenseKeyName: licensekeyname
          }
        }
    end

    def post_new_client_request
      data = new_client_request
      uri = URI.parse(DATA_EXHAUST_REGISTER_CLIENT_API_URL)
      http = Net::HTTP.new(uri.host, uri.port)
      if DATA_EXHAUST_REGISTER_CLIENT_API_URL.start_with? "https"
        http.use_ssl = true
      end
      req = Net::HTTP::Post.new(uri.path, initheader = {'Content-Type' =>'application/json'})
      req.body = JSON.generate(data)
      res = http.request(req)
      res
    end

    def authenticate_request
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

    def post_authenticate_request
      data = authenticate_request
      uri = URI.parse(DATA_EXHAUST_AUTHENTICATE_CLIENT_API_URL)
      http = Net::HTTP.new(uri.host, uri.port)
      if DATA_EXHAUST_AUTHENTICATE_CLIENT_API_URL.start_with? "https"
        http.use_ssl = true
      end
      req = Net::HTTP::Post.new(uri.path, initheader = {'Content-Type' =>'application/json'})
      req.body = JSON.generate(data)
      res = http.request(req)
      res
    end

  end
end
