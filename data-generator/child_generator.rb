require 'digest/sha1'
require 'securerandom'
require 'pry'

ADD_CHILD_API_URL="http://localhost:#{ENV['API_PORT']||8080}/v1/children"

module ChildGenerator
  class Child
    def initialize(name, dob, gender, ekstepId)
      @name = name
      @dob = dob
      @gender = gender
      @ekstepId = ekstepId
    end

    def newchildrequest(did, token, mobile, requesterid)
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
          request: {
            name: @name,
            dob: @dob,
            gender: @gender,
            ekstepId: @ekstepId,
          }
        }
    end

    def post_newchildrequest(did, token, mobile, requesterid)
      data = newchildrequest(did, token, mobile, requesterid)
      uri = URI.parse(ADD_CHILD_API_URL)
      http = Net::HTTP.new(uri.host, uri.port)
      if ADD_CHILD_API_URL.start_with? "https"
        http.use_ssl = true
      end
      req = Net::HTTP::Post.new(uri.path, initheader = {'Content-Type' =>'application/json'})
      req.body = JSON.generate(data)
      res = http.request(req)
      res
    end

  end
end
