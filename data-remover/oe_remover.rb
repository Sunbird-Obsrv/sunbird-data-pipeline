require 'logger'
require 'elasticsearch'
require 'pry'
require 'hashie'

class OERemover
	# ES_URL='http://52.74.22.23:9200'
	def self.remove(value="OE_.*",index="ecosystem-*",type="events_v1")
		begin
			file = "#{ENV['EP_LOG_DIR']}/#{self.name.gsub('::','')}.log"
			logger = Logger.new(file)

			@client = ::Elasticsearch::Client.new log: false
			logger.info "Starting search"
			response = @client.search({
				index: index,
				type: type,
				size: 1,
				body: {
					"query" =>  {
						"filtered" =>  {
							"query" =>  {
								"match_all"=> {}
								},
								"filter" => {
									"regexp" => {
										"eid" => value
									}
								}
							}
						}
					}
					})
			response = ::Hashie::Mash.new response
			logger.info "FOUND #{response.hits.total} hits."
			if response.hits.total == 0
				logger.info "No more events to remove"
				return
			end
			res= @client.delete_by_query({
				index: index,
				type: type,
				body: {
					"query" =>  {
						"filtered" =>  {
							"query" =>  {
								"match_all"=> {}
								},
								"filter" => {
									"regexp" => {
										"eid" => value
									}
								}
							}
						}
					}
				})
			logger.info "Deleted all events with #{value}"
		rescue => e
			logger.error e
		end
	end
end

