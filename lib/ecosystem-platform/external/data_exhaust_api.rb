require 'pry'
require 'uri'
require 'net/http'
require 'securerandom'
require 'json'

class DataExhaustApi
  def initialize(endpoint, logger)
    @endpoint = endpoint
    @http = Net::HTTP.new(URI(endpoint).host, URI(endpoint).port)
    @http.use_ssl = true if endpoint.start_with?("https")
    @logger = logger
  end

  def aggregate(dataset_id, resource_id,
               from_date, to_date)
    url = URI("#{@endpoint}/#{dataset_id}/#{resource_id}/#{from_date}/#{to_date}")
    request = Net::HTTP::Post.new(url)
    request['content-type'] = 'application/json'
    request.body = {
        :id => 'ekstep.data_exhaust_authorize',
        :ver => '1.0',
        :ts => Time.now.utc.strftime('%FT%T%:z'),
        :params => {
            :msgid => SecureRandom.uuid
        },
        :request => {}
    }.to_json

    @logger.info("AGGREGATING DATA USING DATA EXHAUST API. URL: #{url}")
    response = @http.request(request)

    case response
    when Net::HTTPSuccess
      @logger.info("AGGREGATE SUCCESSFUL")
    when Net::HTTPServerError
       @logger.error("AGGREGATE FAILED, error: #{response.read_body}")
      raise 'SERVER ERROR WHEN CALLING DATA EXHAUST AGGREGATE API'
    else
      @logger.error("AGGREGATE FAILED, error: #{response.read_body}")
      raise 'ERROR WHEN CALLING DATA EXHAUST AGGREGATE API'
    end
  end
end
