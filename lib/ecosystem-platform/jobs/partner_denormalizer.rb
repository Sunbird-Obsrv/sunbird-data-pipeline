require 'pry'
require 'hashie'
require 'elasticsearch'
require 'base64'

require_relative '../utils/ep_logging.rb'

module EcosystemPlatform
  module Jobs
    class PartnerDenormalizer

      PROGNAME = 'program_denormalizer.jobs.ep'
      N = 1000
      SLICE = 100

      include EcosystemPlatform::Utils::EPLogging

      def self.perform(index="ecosystem-*")
        begin
          logger.start_task
          logger.info("INITIALIZING CLIENT")
          # TODO Terrible terrible
          # will be replaced by a config module
          @client = ::Elasticsearch::Client.new(host:ENV['ES_HOST']||'localhost',log: false)
          @client.indices.refresh index: index
          logger.info("SEARCHING PARTNER EVENTS TO DENORMALIZE")
          page = 0
          offset = 0
          uids_dictionary = {}
          uids_to_denormalize = []
          to_update = []
          loop do
            response = @client.search({
              index: index,
              type: 'events_v1',
              body: {
                "from" => offset,
                "size" => N,
                "fields"=>["uid"],
                "query"=> {
                  "constant_score"=>
                  {
                    "filter"=>
                      {
                        "and"=>
                        {
                          "filters"=>
                          [
                            {"not"=>{"filter"=>{"missing"=>{"field"=>"tags.partnerid"}}}},
                            {"missing"=>{"field"=>"partner"}},
                            {"exists"=>{"field"=>"uid"}},
                            {"not"=>{"filter"=>{"term"=>{"uid"=>""}}}}]}}}}
              }
            })
            response = Hashie::Mash.new response
            logger.info "PAGE: #{page} - FOUND #{response.hits.hits.count} hits. - TOTAL #{response.hits.total}"
            response.hits.hits.each do |hit|
              uid = hit.fields.uid[0]
              uids_dictionary[uid] ||= []
              uids_dictionary[uid] << { id: hit._id, type: hit._type, index: hit._index }
              uids_to_denormalize << uid
            end
            offset += N
            page += 1
            break if response.hits.hits.count == 0
          end
          data_dictionary = {}
          uids_to_denormalize.flatten.uniq.each_slice(SLICE) do |batch|
            page = 0
            offset = 0
            loop do
              response = @client.search({
                index: index,
                type: 'events_v1',
                body: {
                  "from" => offset,
                  "size" => N,
                  "query"=> {:constant_score=>
                    {:filter=>
                      {:and=>
                        {:filters=>
                          [{:terms=>{:"partner.uid"=>batch}},
                           {:term=>{:eid=>"GE_PARTNER_DATA"}}]}}}}
                }
              })
              response = Hashie::Mash.new response
              logger.info "PAGE: #{page} - FOUND #{response.hits.hits.length} hits. - TOTAL #{response.hits.total}"
              response.hits.hits.each do |hit|
                partner_data = hit._source.partner
                uid = partner_data.uid
                data_dictionary[uid] = partner_data
              end
              offset += N
              page += 1
              break if response.hits.hits.count == 0
            end
          end
          data_dictionary.each do |uid,partner_data|
            docs_to_update = uids_dictionary[uid]
            docs_to_update.each |doc|
              to_update << {
                update: {
                  _index: doc[:index],
                  _type:  doc[:type],
                  _id:    doc[:id],
                  data: {
                    doc: {
                      partner: partner_data.to_hash
                    }
                  }
                }
              }
            end
          end
          unless(to_update.empty?)
            result = @client.bulk(body: to_update)
            logger.info "<- BULK INDEXED #{to_update.length} #{result['errors']==false}"
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
