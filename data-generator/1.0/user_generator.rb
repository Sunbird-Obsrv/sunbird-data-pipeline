require_relative '../1.0/session_generator.rb'

require 'digest/sha1'
require 'securerandom'
require 'pry'

API_ROOT = "http://#{ENV['API_HOST']||'localhost:8080'}"

FACILITATOR_SIGNUP_API_URL="#{API_ROOT}/v1/facilitators/signup"
FACILITATOR_LOGIN_API_URL="#{API_ROOT}/v1/facilitators/login"
FACILITATOR_CHILDREN_LIST_API = "#{API_ROOT}/v1/facilitators/enrolments"

module UserGenerator
  class User
    attr_accessor :token, :mobile, :requesterid
    def initialize(name, mobile, password, confirm_password, device)
      @name = name
      @mobile = mobile
      @password = password
      @confirm_password = confirm_password
      @device = device
      @token = SecureRandom.uuid
      @requesterid = SecureRandom.uuid
    end

    def newuserrequest
      e=
        {
          id: "ekstep.facilitator.signup",
          ver: "1.0",
          ts: Time.now.strftime('%Y-%m-%dT%H:%M:%S%z'),
          params:{
            requesterid: @requesterid,
            did: @device.id,
            key: "",
            msgid: SecureRandom.uuid,
          },
          request: {
            name: @name,
            mobile: @mobile,
            password: sha_of_password(@password),
            confirmPassword: sha_of_password(@confirm_password),
          }
        }
    end

    def post_newuserrequest
      data = newuserrequest
      uri = URI.parse(FACILITATOR_SIGNUP_API_URL)
      http = Net::HTTP.new(uri.host, uri.port)
      if FACILITATOR_SIGNUP_API_URL.start_with? "https"
        http.use_ssl = true
      end
      req = Net::HTTP::Post.new(uri.path, initheader = {'Content-Type' =>'application/json'})
      req.body = JSON.generate(data)
      res = http.request(req)
      res
    end

    def loginrequest
      ts = Time.now.strftime('%Y-%m-%dT%H:%M:%S%z')
      password = sha_of_password(@password)
      password = sha_of_password(password+ts)
      e=
        {
          id: "ekstep.facilitator.signup",
          ver: "1.0",
          ts: ts,
          params:{
            requesterid: @mobile,
            did: @device.id,
            key: "",
            msgid: SecureRandom.uuid,
          },
          request: {
            mobile: @mobile,
            password: password,
          }
        }
    end

    def post_loginrequest
      data = loginrequest
      uri = URI.parse(FACILITATOR_LOGIN_API_URL)
      http = Net::HTTP.new(uri.host, uri.port)
      if FACILITATOR_LOGIN_API_URL.start_with? "https"
        http.use_ssl = true
      end
      req = Net::HTTP::Post.new(uri.path, initheader = {'Content-Type' =>'application/json'})
      req.body = JSON.generate(data)
      res = http.request(req)
      res
    end

    def get_all_children(did, token, requesterid)
      data = get_all_children_request(did, token, requesterid)
      uri = URI.parse(FACILITATOR_CHILDREN_LIST_API)
      http = Net::HTTP.new(uri.host, uri.port)
      if FACILITATOR_CHILDREN_LIST_API.start_with? "https"
        http.use_ssl = true
      end
      req = Net::HTTP::Post.new(uri.path, initheader = {'Content-Type' =>'application/json'})
      req.body = JSON.generate(data)
      res = http.request(req)
      res
    end

    def get_all_children_request(did, token, requesterid)
      ts = Time.now.strftime('%Y-%m-%dT%H:%M:%S%z')
      e=
        {
          id: "ekstep.child.add",
          ver: "1.0",
          ts: ts,
          params:{
            requesterid: requesterid,
            did: did,
            key: ::Digest::SHA1.hexdigest(token+ts+did),
            msgid: SecureRandom.uuid,
          },
        }
    end

    def sha_of_password(text_password)
      if text_password == ""
        return text_password
      else
        return ::Digest::SHA1.hexdigest (text_password)
      end
    end

  end
end
