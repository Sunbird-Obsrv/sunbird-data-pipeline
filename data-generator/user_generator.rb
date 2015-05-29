require_relative '../data-generator/session_generator.rb'

require 'securerandom'
require 'pry'

API_URL="http://localhost:8080/v1/facilitators/signup"


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
            password: @password,
            confirmPassword: @confirm_password,
          }
        }
    end

    def post_newuserrequest
      data = newuserrequest
      uri = URI.parse(API_URL)
      http = Net::HTTP.new(uri.host, uri.port)
      if API_URL.start_with? "https"
        http.use_ssl = true
      end
      req = Net::HTTP::Post.new(uri.path, initheader = {'Content-Type' =>'application/json'})
      req.body = JSON.generate(data)
      res = http.request(req)
      res
    end

  end
end
