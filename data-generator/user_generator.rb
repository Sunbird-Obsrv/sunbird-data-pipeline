require_relative '../data-generator/session_generator.rb'

require 'digest/sha1'
require 'securerandom'
require 'pry'

FACILITATOR_SIGNUP_API_URL="http://localhost:8080/v1/facilitators/signup"
FACILITATOR_LOGIN_API_URL="http://localhost:8080/v1/facilitators/login"


module UserGenerator
  class User
    def initialize(name, mobile, password, confirm_password, device)
      @name = name
      @mobile = mobile
      @password = password
      @confirm_password = confirm_password
      @device = device
    end

    def newuserrequest
      e=
        {
          id: "ekstep.facilitator.signup",
          ver: "1.0",
          ts: Time.now.strftime('%Y-%m-%dT%H:%M:%S%z'),
          params:{
            requesterid: @mobile,
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

    def sha_of_password(text_password)
      if text_password == ""
        return text_password
      else
        return ::Digest::SHA1.hexdigest (text_password)
      end
    end

  end
end
