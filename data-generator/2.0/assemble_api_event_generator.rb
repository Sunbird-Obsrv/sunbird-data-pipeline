require 'digest/sha1'
require 'securerandom'
require 'pry'

API_ROOT = "http://#{ENV['API_HOST']||'localhost:8080'}"

ASSEMBLE_PAGE_API_URL="#{API_ROOT}/v1/page/assemble/org.ekstep.genie.content.explore"

module AssembleApiEventGenerator
  class Assemble
    attr_accessor :event
    def initialize
      self.event = create_event
    end
    
    def post_new_assemble_api_request
      uri = URI.parse(ASSEMBLE_PAGE_API_URL)
      http = Net::HTTP.new(uri.host, uri.port)
      if ASSEMBLE_PAGE_API_URL.start_with? "https"
        http.use_ssl = true
      end
      req = Net::HTTP::Post.new(uri.path, initheader = {'Content-Type' =>'application/json'})
      req.basic_auth("#{ENV['API_USER']}", "#{ENV['API_PASS']}")
      req.body = JSON.generate(event)
      res = http.request(req)
      res
    end

    private

    def create_event
      e=
        {
          id: "ekstep.genie.content.explore",
          ver: "1.0",
          ts: Time.now.strftime('%Y-%m-%dT%H:%M:%S%z'),
          params:{
            requesterid: "",
            did: "",
            key: "",
            msgid: SecureRandom.uuid,
          }
        }
    end
  end
end