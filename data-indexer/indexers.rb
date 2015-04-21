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
            events_v1: {
              properties: {
                eid: { type: 'string', index: 'not_analyzed'},
                ts: { type: 'date' },
                ver: { type: 'string', index: 'not_analyzed'},
                gdata: {
                  type: 'nested',
                  include_in_parent: true,
                  properties: {
                    id: { type: 'string' },
                    ver: { type: 'string' }
                  }
                },
                sid: { type: 'string', index: 'not_analyzed'},
                uid: { type: 'string', index: 'not_analyzed'},
                did: { type: 'string', index: 'not_analyzed'},
                edata: {
                  type: 'nested',
                  include_in_parent: true,
                  properties: {
                    eks: {
                      type: 'nested',
                      include_in_parent: true,
                      properties: {
                        length: { type: 'double'},
                        loc: { type: 'geo_point'},
                        uid: { type: 'string', index: 'not_analyzed' },
                        err: { type: 'string' },
                        gid: { type: 'string', index: 'not_analyzed' },
                        attrs: { type: 'string' },
                        mode: { type: 'string', index: 'not_analyzed' },
                        api: { type: 'string', index: 'not_analyzed' },
                        msgid: { type: 'string', index: 'not_analyzed' },
                        ver: { type: 'string', index: 'not_analyzed' },
                        size: { type: 'double' },
                        os: { type: 'string', index: 'not_analyzed' },
                        make: { type: 'string', index: 'not_analyzed' },
                        mem: { type: 'long' },
                        idisk: { type: 'long' },
                        edisk: { type: 'long' },
                        scrn: { type: 'double' },
                        camera: { type: 'string', index: 'not_analyzed' },
                        cpu: { type: 'string', index: 'not_analyzed' },
                        sims: { type: 'long' },
                        cap: { type: 'string', index: 'not_analyzed' },
                        subj: { type: 'string', index: 'not_analyzed' },
                        mc: { type: 'string', index: 'not_analyzed' },
                        skill: { type: 'string', index: 'not_analyzed' },
                        pass: { type: 'string', index: 'not_analyzed' },
                        res: { type: 'string', index: 'not_analyzed' },
                        exres: { type: 'string', index: 'not_analyzed' },
                        # exlength: {},
                        uri: { type: 'string', index: 'not_analyzed' },
                        # current: {},
                        # max: {},
                        topics: {
                          type: 'nested',
                          include_in_parent: true,
                          properties: {
                            mc: { type: 'string', index: 'not_analyzed' },
                            skill: { type: 'string', index: 'not_analyzed' },
                            methods: { type: 'string', index: 'not_analyzed' }
                          }
                        },
                        type: {type: 'string', index: 'not_analyzed'},
                        extype: {type: 'string', index: 'not_analyzed'},
                      }
                    },
                    ext: { type: 'nested' }
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
