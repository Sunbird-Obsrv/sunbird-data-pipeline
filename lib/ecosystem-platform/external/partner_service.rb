require 'pry'
require 'uri'
require 'net/http'
require 'securerandom'
require 'json'

class PartnerService
  def initialize(endpoint, logger)
    @endpoint = endpoint
    @http = Net::HTTP.new(URI(endpoint).host, URI(endpoint).port)
    @http.use_ssl = true if endpoint.start_with?("https")
    @logger = logger
  end

  def get_all_resources()
    url = URI(@endpoint)
    request = Net::HTTP::Post.new(url)
    request['content-type'] = 'application/json'

    request.body = {
      :id => 'ekstep.partners.get.all',
      :ver => '1.0',
      :ets => Time.now.utc.strftime('%FT%T%:z'),
      :params => {
        :msgid => SecureRandom.uuid
      },
      :request => {}
    }.to_json

    @logger.info("GETTING LIST OF RESOURCES. URL: #{url}")
    response = @http.request(request)

    case response
    when Net::HTTPSuccess
      responseObject = JSON.parse(response.read_body)
      @logger.info("RESPONSE BODY, #{responseObject}")
      resources = responseObject['result']
      .map{|resource| Resource.new(resource['partnerId'], resource['name'])}
      @logger.info("LIST OF RESOURCES, #{resources}")
      return resources
    when Net::HTTPServerError
      @logger.error("SERVER ERROR WHEN GETTING LIST OF RESOURCES, error: #{response.read_body}")
      raise 'SERVER ERROR WHEN WHEN GETTING LIST OF RESOURCES'
    else
      @logger.error("GETTING LIST OF RESOURCES, error: #{response.read_body}")
      raise 'ERROR WHEN GETTING LIST OF RESOURCES'
    end
  end
end
