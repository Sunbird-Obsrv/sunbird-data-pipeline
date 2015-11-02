require 'pry'
require 'hashie'
require 'elasticsearch'
require 'base64'
require 'yaml'

require_relative '../utils/ep_logging.rb'

module EcosystemPlatform
  module Jobs
    class PartnerDecryptor

      PROGNAME = 'program_decryptor.jobs.ep'
      N = 100
      CIPHER = 'AES-128-CBC'

      include EcosystemPlatform::Utils::EPLogging

      def self.perform(index="ecosystem-*")
        begin
          logger.start_task
          logger.info("INITIALIZING CLIENT")
          # TODO Terrible terrible
          # will be replaced by a config module
          @key = YAML.load_file(ENV['EP_PRIVATE_KEYS'])
          @client = ::Elasticsearch::Client.new(host:ENV['ES_HOST']||'localhost',log: false)
          @client.indices.refresh index: index
          logger.info("SEARCHING EVENTS TO DECRYPT")
          page = 0
          offset = 0
          loop do
            response = @client.search({
              index: index,
              type: 'events_v1',
              body: {
                "from" => offset,
                "size" => N,
                "query"=> {
                      "constant_score": {
                         "filter": {
                              "and": {
                                 "filters": [
                                    {
                                     "missing": {
                                        "field": "partner"
                                        }
                                     },
                                    {
                                        "term": {
                                           "eid": "GE_PARTNER_DATA"
                                        }
                                    }
                                 ]
                              }
                         },
                         "boost": 1.2
                      }
                  }
              }
            })
            response = Hashie::Mash.new response
            # binding.pry
            logger.info "PAGE: #{page} - FOUND #{response.hits.hits.count} hits. - TOTAL #{response.hits.total}"
            to_update = []
            response.hits.hits.each do |hit|
              uid = hit._source.uid
              doc = hit._source
              # reducing partitions to monthly
              _index = hit._index
              begin
                private_key_file = @key[doc.edata.eks.partnerid]
                raise "No Private Key file for #{doc.edata.eks.partnerid}" unless private_key_file
                private_key = OpenSSL::PKey::RSA.new(File.read(private_key_file))
                encrypted_string = doc.edata.eks['key']
                key = private_key.private_decrypt(Base64.decode64(encrypted_string))
                data = doc.edata.eks.data
                data = Base64.decode64(data)
                iv = doc.edata.eks.iv
                iv = Base64.decode64(iv)
                aes  = OpenSSL::Cipher.new(CIPHER)
                aes.decrypt
                aes.key = key
                aes.iv = iv
                data = aes.update(data) + aes.final
                data = JSON.parse data
                to_update << {
                    update: {
                      _index: _index,
                      _type: hit._type,
                      _id: hit._id,
                      data: {
                        doc: {
                          partner: data
                        }
                      }
                    }
                  }
              rescue => e
                error = e.message
                doc.errors||={}
                doc.errors[error]||=0
                doc.errors[error]+=1
                to_update << {
                  update: {
                    _index: _index,
                    _type: hit._type,
                    _id: hit._id,
                    data: {
                      doc: doc
                    }
                  }
                }
                logger.error(e,{backtrace: e.backtrace[0..4]})
              end
            end
            unless(to_update.empty?)
              result = @client.bulk(body: to_update)
              logger.info "<- BULK INDEXED #{result['errors']==false}"
            end
            offset += N
            page += 1
            break if response.hits.hits.count == 0
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
