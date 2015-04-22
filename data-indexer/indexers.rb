require 'elasticsearch'

module Indexers
  class Elasticsearch
    attr_reader :client
    def initialize(refresh=true)
      @client = ::Elasticsearch::Client.new log: false
      if refresh
        delete_indices
        create_indices
      end
    end
    def delete_indices
      @client.indices.delete index: 'identities' rescue nil
    end
    def create_indices
      client.indices.put_template({
        name: 'events',
        body: {
          template: '*',
          settings: {
            'index.number_of_shards' => 1
          },
          mappings: {
            events_v1: {
              properties: {
                eid: { type: 'string', index: 'not_analyzed', fielddata: { format: "doc_values" }},
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
                        ldata: {
                          type: 'nested',
                          include_in_parent: true,
                          properties: {
                            locality: { type: 'string', index: 'not_analyzed' },
                            district: { type: 'string', index: 'not_analyzed' },
                            state: { type: 'string', index: 'not_analyzed' },
                            country: { type: 'string', index: 'not_analyzed' }
                          }
                        },
                        uid: { type: 'string', index: 'not_analyzed' },
                        err: { type: 'string' },
                        gid: { type: 'string', index: 'not_analyzed' },
                        attrs: { type: 'string' },
                        mode: { type: 'string', index: 'not_analyzed' },
                        api: { type: 'string', index: 'not_analyzed' },
                        msgid: { type: 'string', index: 'not_analyzed' },
                        ver: { type: 'string', index: 'not_analyzed' },
                        size: { type: 'double' },
                        dspec: {
                          type: 'nested',
                          include_in_parent: true,
                          properties: {
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
                          }
                        },
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
      @client.indices.create({
        index: 'identities'
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
