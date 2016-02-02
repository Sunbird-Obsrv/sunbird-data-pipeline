require 'pry'
require 'hashie'
require 'elasticsearch'
require 'base64'

require_relative '../utils/ep_logging.rb'

module EcosystemPlatform
	module Correctors
		class GenderCorrection

			PROGNAME = 'gender_correction.jobs.ep'
			EVENTS_N = 10000
			SLICE = 10000
			MAX_EVENTS_OFFSET = 1000000
			UPDATE_SLICE = 1000

			include EcosystemPlatform::Utils::EPLogging

			def self.perform(index="ecosystem-*")
				begin
					logger.start_task
					logger.info("INITIALIZING CLIENT")
					@client = ::Elasticsearch::Client.new(host:ENV['ES_HOST']||'localhost',log: false)
					@client.indices.refresh index: index
					logger.info("SEARCHING EVENTS TO HANDELIZE")
					page = 0
					offset = 0
					uids_with_profile = []
					profile_dict = {}
					to_update = []
					events = []
					count = 0
					loop do
						break if offset>MAX_EVENTS_OFFSET
						response = @client.search({
							index: index,
							type: 'events_v1',
							sort: 'ts',
							body: {
								"from" => offset,
								"size" => EVENTS_N,
								"query" => {"filtered":{"filter":{"bool":{"must":[{"exists":{"field":"udata"}}],"must_not":[{"query":{"terms":{"udata.gender":["male","female","Not known"]}}}]}}}}
							}
						})
						response = Hashie::Mash.new response
						logger.info "PAGE: #{page} - FOUND #{response.hits.hits.count} hits. - TOTAL #{response.hits.total}"
						response.hits.hits.each do |hit|
							gender = hit._source.udata.gender
							if(gender == nil)
								logger.info "UPDATE #{hit._id}"
								to_update << {
									update: {
										_index: hit._index,
										_type:  hit._type,
										_id:    hit._id,
										data: {
											doc: {
												udata: {
													gender: "Not known"
												}
											}}}}
							else
								raise "Yikes! Nothing to update!"
							end
						end
						offset += EVENTS_N
						page += 1
						break if response.hits.hits.count == 0
					end
					logger.info "COUNT: #{count}"
					unless(to_update.empty?)
            threads = []
            to_update.each_slice(UPDATE_SLICE) do |slice|
              threads << Thread.new do
                result = @client.bulk(body: slice)
                logger.info "<- BULK INDEXED #{slice.length} #{result['errors']==false}"
              end
            end
            threads.map(&:join)
          end
					logger.end_task
				rescue => e
					logger.error(e,{backtrace: e.backtrace[0..4]})
					logger.end_task
				end
			end

		end
	end
end
