require 'digest/sha1'
require 'securerandom'
require 'pry'

API_ROOT = "http://#{ENV['API_HOST']||'localhost:8080'}"

ASSEMBLE_PAGE_API_URL="#{API_ROOT}/v1/page/assemble/org.ekstep.genie.content.explore"

module AssembleApiEventGenerator
  class Assemble
    attr_accessor :event
    attr_accessor :event_with_context
    def initialize
      self.event = create_event
      self.event_with_context = create_event_with_context
    end

    def post_new_assemble_api_request
      post(event)
    end

    def post_new_assemble_api_request_with_context
      post(event_with_context)
    end

    private

    def post(post_event)
      uri = URI.parse(ASSEMBLE_PAGE_API_URL)
      http = Net::HTTP.new(uri.host, uri.port)
      if ASSEMBLE_PAGE_API_URL.start_with? "https"
        http.use_ssl = true
      end
      req = Net::HTTP::Post.new(uri.path, initheader = {'Content-Type' =>'application/json'})
      req.basic_auth("#{ENV['API_USER']}", "#{ENV['API_PASS']}")
      req.body = JSON.generate(post_event)
      res = http.request(req)
      res
    end

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

    def create_event_with_context
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
          },
          request: {
            context: {
              did: "5edf49c4-313c-4f57-fd52-9bfe35e3b9v7",
              dlang: "en"
            }
          }
        }
    end

  end
end
