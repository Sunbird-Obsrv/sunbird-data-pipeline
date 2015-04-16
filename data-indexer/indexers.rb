require 'elasticsearch'

module Indexers
  class Elasticsearch
    attr_reader :client
    def initialize
      @client = ::Elasticsearch::Client.new log: false
      delete_indices
      create_indices
    end
    def delete_indices
      @client.indices.delete index: 'identities' rescue nil
    end
    def create_indices
      # creating all types in the same index as of now to avoid
      # unnecessary sharding
      @client.indices.create({
        index: 'identities',
        body: {
          #settings: {}
          mappings: {
            identities_v1: {
              _id: {
                path: 'users_id'
              },
              properties: {
                users_id: { type: 'string' }
              }
            },
            first_interactions_v1: {
              _id: {
                path: 'uid'
              },
              properties: {
                ts: { type: 'date' },
                uid: { type: 'string',index: 'not_analyzed' }
              }
            },
            devices_v1: {
              _id: {
                path: 'device_id'
              },
              properties: {
                device_id: { type: 'string' }
                # device_name: { type: 'string' }
              }
            },
            events_v1: {
              _id: {
                path: 'sid'
              },
              properties: {
                ts: { type: 'date' },
                ver: { type: 'string' },
                gdata: {
                  type: 'nested',
                  include_in_parent: true,
                  properties: {
                    id: { type: 'string' },
                    ver: { type: 'string' }
                  }
                },
                sid: { type: 'string',index: 'not_analyzed' },
                ddata: {
                  type: 'nested',
                  include_in_parent: true,
                  properties: {
                    #consider saving this in another doc
                    did: { type: 'string', index: 'not_analyzed' },
                    dos: { type: 'string', index: 'not_analyzed'  },
                    make: { type: 'string', index: 'not_analyzed'  },
                    loc: { type: 'geo_point' },
                    spec: { type: 'string', index: 'not_analyzed'  }
                  }
                },
                uid: { type: 'string', index: 'not_analyzed' },
                eid: { type: 'string' }
                # edata: { type: 'object' }
              }
            },
            sessions_v1: {
              # _parent: { type: "identities_v1" }, ?
              _id: {
                path: 'sid'
              },
              properties: {
                sid: { type: 'string', index: 'not_analyzed' },
                ts: { type: 'date' },
                te: { type: 'date' },
                duration: { type: 'double' },
                ddata: {
                  type: 'nested',
                  include_in_parent: true,
                  properties: {
                    #consider saving this in another doc
                    did: { type: 'string', index: 'not_analyzed' },
                    dos: { type: 'string', index: 'not_analyzed'  },
                    make: { type: 'string', index: 'not_analyzed'  },
                    loc: { type: 'geo_point' },
                    spec: { type: 'string', index: 'not_analyzed'  }
                  }
                }
              }
            }
          }
        }
      })
    end
    def index(index,type,body)
      begin
        @client.index({
              index: index,
              type: type,
              body: body
              }
            )
      rescue => e
        raise e
      end
    end
    def update(index,type,id,body)
      begin
        @client.update({
                index: index,
                type: type,
                id: id,
                body: { doc: body }
        })
      rescue => e
        raise e
      end
    end
  end
end
