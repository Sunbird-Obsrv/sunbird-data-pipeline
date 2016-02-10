require 'pry'
require 'hashie'
require 'elasticsearch'
require 'base64'

require_relative '../utils/ep_logging.rb'

module EcosystemPlatform
  module Jobs
    class HandleDenormalizer

      PROGNAME = 'handle_denormalizer.jobs.ep'
      PROFILE_N = 100
      MAX_PROFILE_OFFSET = 100
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
            # break if offset>MAX_PROFILE_OFFSET
            response = @client.search({
              index: index,
              type: 'events_v1',
              sort: 'ts',
              body: {
                "from" => offset,
                "size" => PROFILE_N,
                "query"=> {
                  "terms": {
                    "eid": ["GE_CREATE_PROFILE","GE_UPDATE_PROFILE"]
                  }
                }
              }
            })
            response = Hashie::Mash.new response
            logger.info "PAGE: #{page} - FOUND #{response.hits.hits.count} hits. - TOTAL #{response.hits.total}"
            events = response.hits.hits
            response.hits.hits.each do |hit|
              uid = hit._source.uid
              eks = hit._source.edata.eks
              eid = hit._source.eid
              set = false
              if(profile_dict[uid])
                if(eid=="GE_UPDATE_PROFILE")
                  set = true
                end
              else
                set = true
              end
              profile_dict[uid] = {
                  "handle" => eks.handle,
                  "standard" => eks.standard,
                  "age_completed_years" => eks.age,
                  "gender" => eks.gender
                } if set
              uids_with_profile << uid
            end
            offset += PROFILE_N
            page += 1
            break if response.hits.hits.count == 0
          end
          uids_with_profile.flatten.uniq.each_slice(SLICE) do |batch|
            page = 0
            offset = 0
            loop do
              break if offset>=MAX_EVENTS_OFFSET
              response = @client.search({
                index: index,
                type: 'events_v1',
                sort: '_id',
                body: {
                  "from" => offset,
                  "size" => EVENTS_N,
                  "query"=> {:constant_score=>
                    {:filter=>
                      {:and=>
                        {:filters=>
                          [{:terms=>{:uid=>batch}},
                           {:missing=>{:field=>:udata}}]}}}}
                }
              })
              response = Hashie::Mash.new response
              logger.info "PAGE: #{page} - FOUND #{response.hits.hits.length} hits. - TOTAL #{response.hits.total}"
              response.hits.hits.each do |hit|
                uid = hit._source.uid
                if(profile_dict[uid])
                  logger.info "UPDATE #{hit._id} <-- #{profile_dict[uid]}"
                  to_update << {
                    update: {
                      _index: hit._index,
                      _type:  hit._type,
                      _id:    hit._id,
                      data: {
                        doc: {
                          udata: profile_dict[uid]
                        }}}}
                else
                  raise "Yikes! Nothing in profile dict!"
                end
                count += 1
              end
              offset += EVENTS_N
              page += 1
              break if response.hits.hits.count == 0
            end
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
