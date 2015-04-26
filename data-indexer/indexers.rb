require 'elasticsearch'
require 'pry'

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
      client.indices.delete_template name: 'ecosystem' rescue nil
      # @client.indices.delete index: 'identities' rescue nil
    end
    def create_indices
      client.indices.put_template({
        name: "ecosystem",
        body: {
        order: 20,
        template: "dump",
        settings: {
          # "index.refresh_interval": "5s"
        },
        mappings: {
          _default_: {
            dynamic_templates: [
            {
              string_fields: {
                mapping: {
                  index: "not_analyzed",
                  omit_norms: false,
                  type: "string",
                  doc_values: true
                },
                match_mapping_type: "string",
                match: "*"
              }
            },
            {
              date_fields: {
                  match: "ts|te",
                  match_pattern: "regex",
                  mapping: {
                      type: "date",
                      index: "not_analyzed",
                      doc_values: true
                  }
              }
            },
            {
              geo_location: {
                  mapping: {
                      type: "geo_point",
                      doc_values: true
                  },
                  match: "loc"
              }
            }
            ],
            _all: {
              enabled: true
            }
          }
        },
        aliases: {}
        }
      })
      client.indices.put_template({
        name: "ecosystem",
        body: {
        order: 10,
        template: "ecosystem-*",
        settings: {
          "index.refresh_interval": "5s"
        },
        mappings: {
          devices_v1: {
            _id: {
              path: "did"
            },
            properties: {
              did: { type: 'string', index: 'not_analyzed'}
            }
          },
          _default_: {
            dynamic_templates: [
            {
              string_fields: {
                mapping: {
                  index: "not_analyzed",
                  omit_norms: false,
                  type: "string",
                  doc_values: true
                },
                match_mapping_type: "string",
                match: "*"
              }
            },
            {
              double_fields: {
                  match: "mem|idisk|edisk|scrn|length",
                  match_pattern: "regex",
                  mapping: {
                      type: "double",
                      index: "not_analyzed",
                      doc_values: true
                  }
              }
            },
            {
              integer_fields: {
                  match: "sims",
                  match_pattern: "regex",
                  mapping: {
                      type: "integer",
                      index: "not_analyzed",
                      doc_values: true
                  }
              }
            },
            {
              date_fields: {
                  match: "ts|te",
                  match_pattern: "regex",
                  mapping: {
                      type: "date",
                      index: "not_analyzed",
                      doc_values: true
                  }
              }
            },
            {
              geo_location: {
                  mapping: {
                      type: "geo_point",
                      doc_values: true
                  },
                  match: "loc"
              }
            }
            ],
            properties: {
              geoip: {
                dynamic: true,
                path: "full",
                properties: {
                  location: {
                    type: "geo_point"
                  }
                },
                type: "object"
              },
              "@version" => {
                index: "not_analyzed",
                type: "string"
              }
            },
            _all: {
              enabled: true
            }
          }
        },
        aliases: {}
        }
      })
      # client.indices.put_template({
      #   name: 'events',
      #   body: {
      #     template: '*',
      #     settings: {
      #       'index.number_of_shards' => 1
      #     },
      #     mappings: {
      #       game_events_v1:{
      #         properties: {
      #           eid: { type: 'string', index: 'not_analyzed'},
      #           did: { type: 'string', index: 'not_analyzed'},
      #           sid: { type: 'string', index: 'not_analyzed'},
      #           lang: { type: 'string', index: 'not_analyzed'},
      #           pmode: { type: 'string', index: 'not_analyzed'},
      #           gdata: {
      #             type: 'nested',
      #             include_in_parent: true,
      #             properties: {
      #               id: { type: 'string' },
      #               ver: { type: 'string' }
      #             }
      #           },
      #           edata: {
      #             type: 'nested',
      #             include_in_parent: true,
      #             properties: {
      #               eks: {
      #                 type: 'nested',
      #                 include_in_parent: true,
      #                 properties: {
      #                   length: { type: 'double'},
      #                   score: { type: 'double' },
      #                   correct: { type: 'long' },
      #                   incorrect: { type: 'long' },
      #                   percent_correct: { type: 'double' },
      #                   result: { type: 'string', index: 'not_analyzed'},
      #                   total: { type: 'long' }
      #                 }
      #               }
      #             }
      #           }
      #         }
      #       },
      #       devices_v1: {
      #         _id: {
      #           path: "did"
      #         },
      #         properties: {
      #           ts: { type: 'date' },
      #           tu: { type: 'date' },
      #           did: { type: 'string', index: 'not_analyzed'},
      #           loc: { type: 'geo_point'},
      #           ldata: {
      #             type: 'nested',
      #             include_in_parent: true,
      #             properties: {
      #               locality: { type: 'string', index: 'not_analyzed' },
      #               district: { type: 'string', index: 'not_analyzed' },
      #               state: { type: 'string', index: 'not_analyzed' },
      #               country: { type: 'string', index: 'not_analyzed' }
      #             }
      #           },
      #           dspec: {
      #             type: 'nested',
      #             include_in_parent: true,
      #             properties: {
      #               id: { type: 'string', index: 'not_analyzed' },
      #               os: { type: 'string', index: 'not_analyzed' },
      #               make: { type: 'string', index: 'not_analyzed' },
      #               mem: { type: 'double' , index: 'not_analyzed' },
      #               idisk: { type: 'double' },
      #               edisk: { type: 'double' },
      #               scrn: { type: 'double' },
      #               camera: { type: 'string', index: 'not_analyzed' },
      #               cpu: { type: 'string', index: 'not_analyzed' },
      #               sims: { type: 'double' },
      #               cap: { type: 'string', index: 'not_analyzed' },
      #             }
      #           }
      #         }
      #       },
      #       events_v1: {
      #         # dynamic: "false",
      #         properties: {
      #           eid: { type: 'string', index: 'not_analyzed', fielddata: { format: "doc_values" }},
      #           ts: { type: 'date' },
      #           ver: { type: 'string', index: 'not_analyzed'},
      #           gdata: {
      #             type: 'nested',
      #             include_in_parent: true,
      #             properties: {
      #               id: { type: 'string' },
      #               ver: { type: 'string' }
      #             }
      #           },
      #           sid: { type: 'string', index: 'not_analyzed'},
      #           uid: { type: 'string', index: 'not_analyzed'},
      #           did: { type: 'string', index: 'not_analyzed'},
      #           edata: {
      #             type: 'nested',
      #             include_in_parent: true,
      #             properties: {
      #               eks: {
      #                 type: 'nested',
      #                 include_in_parent: true,
      #                 properties: {
      #                   length: { type: 'double'},
      #                   loc: { type: 'geo_point'},
      #                   ldata: {
      #                     type: 'nested',
      #                     include_in_parent: true,
      #                     properties: {
      #                       locality: { type: 'string', index: 'not_analyzed' },
      #                       district: { type: 'string', index: 'not_analyzed' },
      #                       state: { type: 'string', index: 'not_analyzed' },
      #                       country: { type: 'string', index: 'not_analyzed' }
      #                     }
      #                   },
      #                   uid: { type: 'string', index: 'not_analyzed' },
      #                   err: { type: 'string' },
      #                   gid: { type: 'string', index: 'not_analyzed' },
      #                   attrs: { type: 'string' },
      #                   mode: { type: 'string', index: 'not_analyzed' },
      #                   api: { type: 'string', index: 'not_analyzed' },
      #                   msgid: { type: 'string', index: 'not_analyzed' },
      #                   ver: { type: 'string', index: 'not_analyzed' },
      #                   size: { type: 'double' },
      #                   dspec: {
      #                     type: 'nested',
      #                     include_in_parent: true,
      #                     properties: {
      #                       os: { type: 'string', index: 'not_analyzed' },
      #                       make: { type: 'string', index: 'not_analyzed' },
      #                       mem: { type: 'double' , index: 'not_analyzed' },
      #                       idisk: { type: 'double' },
      #                       edisk: { type: 'double' },
      #                       scrn: { type: 'double' },
      #                       camera: { type: 'string', index: 'not_analyzed' },
      #                       cpu: { type: 'string', index: 'not_analyzed' },
      #                       sims: { type: 'double' },
      #                       cap: { type: 'string', index: 'not_analyzed' },
      #                     }
      #                   },
      #                   subj: { type: 'string', index: 'not_analyzed' },
      #                   mc: { type: 'string', index: 'not_analyzed' },
      #                   skill: { type: 'string', index: 'not_analyzed' },
      #                   pass: { type: 'string', index: 'not_analyzed' },
      #                   res: { type: 'string', index: 'not_analyzed' },
      #                   exres: { type: 'string', index: 'not_analyzed' },
      #                   uri: { type: 'string', index: 'not_analyzed' },
      #                   topics: {
      #                     type: 'nested',
      #                     include_in_parent: true,
      #                     properties: {
      #                       mc: { type: 'string', index: 'not_analyzed' },
      #                       skill: { type: 'string', index: 'not_analyzed' },
      #                       methods: { type: 'string', index: 'not_analyzed' }
      #                     }
      #                   },
      #                   type: {type: 'string', index: 'not_analyzed'},
      #                   extype: {type: 'string', index: 'not_analyzed'},
      #                 }
      #               },
      #               ext: { type: 'nested' }
      #             }
      #           }
      #         }
      #       }
      #     }
      #   }
      # })
      # @client.indices.create({
      #   index: 'ecosystem-identities'
      # })
    end
    def get(index,type,id)
      begin
        binding.pry
        @client.get({
              index: index,
              type: type,
              id: id
              }
            )
      rescue => e
        raise e
      end
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
