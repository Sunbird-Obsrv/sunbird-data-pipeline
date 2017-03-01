require 'elasticsearch'
require 'pry'

module Indexers
  class Elasticsearch
    TEMPLATE_MAPPINGS = {
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
              string_fields_force: {
                mapping: {
                  index: "not_analyzed",
                  omit_norms: false,
                  type: "string",
                  doc_values: true
                },
                match: "id|current|res|exres|max|mc|mmc|category",
                match_pattern: "regex"
              }
            },
            {
              double_fields: {
                  match: "mem|idisk|edisk|scrn|length|exlength|age|percent_correct|percent_attempt|size|score|maxscore|osize|isize",
                  match_pattern: "regex",
                  mapping: {
                      type: "double",
                      index: "not_analyzed",
                      doc_values: true
                  }
              }
            },
            {
             boolean_fields:{
                  match: "signup_processed",
                  match_pattern: "regex",
                  mapping: {
                      type: "boolean",
                      index: "not_analyzed",
                      doc_values: true
                  }
              }
            },
            {
              integer_fields: {
                  match: "sims|atmpts|failedatmpts|correct|incorrect|total|age_completed_years|pkgVersion",
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
                  match: "ts|te|time|timestamp|ets|lastUpdatedOn",
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
            },
            {
              parsed_object_fields:{
                path_match: "*.searchCriteria",
                mapping: {
                    type: "object",
                    index: "not_analyzed",
                    doc_values: true
                }
              }
            },
            {
              unparsed_object_fields:{
                path_match: "*.resvalues",
                mapping: {
                    type: "object",
                    index: "no",
                    doc_values: true,
                    enabled: false
                }
              }
            }
            ],
            properties: {
              geoip: {
                dynamic: true,
                properties: {
                  location: {
                    type: "geo_point"
                  }
                },
                type: "object"
              },
              edata: {
                properties: {
                  eks: {
                    properties: {
                      resvalues: {
                        type: "object",
                        enabled: false,
                        include_in_all: true
                        }
                      }
                   }
                }
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
        }
        LEARNING_MAPPINGS = {
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
              string_fields_force: {
                mapping: {
                  index: "not_analyzed",
                  omit_norms: false,
                  type: "string",
                  doc_values: true
                },
                match: "id|current|res|exres|max|mc|mmc|category",
                match_pattern: "regex"
              }
            },
            {
              double_fields: {
                  match: "mem|idisk|edisk|scrn|length|exlength|age|percent_correct|percent_attempt|size|score|maxscore|osize|isize|timeSpent|exTimeSpent",
                  match_pattern: "regex",
                  mapping: {
                      type: "double",
                      index: "not_analyzed",
                      doc_values: true
                  }
              }
            },
            {
             boolean_fields:{
                  match: "signup_processed",
                  match_pattern: "regex",
                  mapping: {
                      type: "boolean",
                      index: "not_analyzed",
                      doc_values: true
                  }
              }
            },
            {
              integer_fields: {
                  match: "sims|atmpts|failedatmpts|correct|incorrect|total|age_completed_years|standard|count|noOfAttempts|noOfLevelTransitions",
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
                  match: "ts|te|time|timestamp|end_time|start_time|end_ts|last_visit_ts|start_ts|syncDate|from|to|ets|syncts",
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
            },
            {
              unparsed_object_fields:{
                match: "resValues|incorrect_res",
                match_pattern: "regex",
                mapping: {
                    type: "object",
                    index: "no",
                    doc_values: true,
                    enabled: false
                }
              }
            }
            ],
            properties: {
              geoip: {
                dynamic: true,
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
        }
    DUMP_MAPPINGS = {
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
              string_fields_force: {
                mapping: {
                  index: "not_analyzed",
                  omit_norms: false,
                  type: "string",
                  doc_values: true
                },
                match: "current|res|exres|max|mc|mmc|category",
                match_pattern: "regex"
              }
            },
            {
              date_fields: {
                  match: "ts|te|time|reset-time",
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
        }
    LOG_MAPPINGS = {
          _default_: {
            dynamic_templates: [
            {
              date_fields: {
                  match: "ts",
                  match_pattern: "regex",
                  mapping: {
                      type: "date",
                      index: "not_analyzed",
                      doc_values: true
                  }
              }
            }
            ],
            _all: {
              enabled: true
            }
          }
        }
    BACKEND_EVENT_MAPPINGS = {
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
              string_fields_force: {
                mapping: {
                  index: "not_analyzed",
                  omit_norms: false,
                  type: "string",
                  doc_values: true
                },
                match: "id|uip|status|cid",
                match_pattern: "regex"
              }
            },
            {
              double_fields: {
                  match: "mem|idisk|edisk|scrn|length|exlength|age|percent_correct|percent_attempt|size|score|maxscore|osize|isize|timeSpent|exTimeSpent",
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
                  match: "size|pkgVersion|assets|count|inputEvents|outputEvents|timeTaken|responseTime",
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
                  match: "ts|te|time|timestamp|ets|date",
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
            },
            {
              unparsed_object_fields:{
                match: "config|params",
                match_pattern: "regex",
                mapping: {
                    type: "object",
                    index: "no",
                    doc_values: true,
                    enabled: false
                }
              }
            }
            ],
            properties: {
              geoip: {
                dynamic: true,
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
        }
    attr_reader :client
    def initialize(refresh=true)
      @client = ::Elasticsearch::Client.new log: false
      if refresh
        delete_templates
        create_templates
      end
    end
    def delete_templates
      client.indices.delete_template name: 'dump' rescue nil
      client.indices.delete_template name: 'ecosystem' rescue nil
    end
    def create_templates
      puts client.indices.put_template({
        name: "dump",
        body: {
        order: 20,
        template: "dump",
        settings: {
          # "index.refresh_interval": "5s"
        },
        mappings: DUMP_MAPPINGS,
        aliases: {}
        }
      })
      puts client.indices.put_template({
        name: "ecosystem",
        body: {
        order: 10,
        template: "ecosystem-*",
        settings: {
          "index.refresh_interval": "5s"
        },
        mappings: TEMPLATE_MAPPINGS,
        aliases: {}
        }
      })
      puts client.indices.put_template({
        name: "logs",
        body: {
        order: 10,
        template: "logs-*",
        settings: {
          "index.refresh_interval": "5s"
        },
        mappings: LOG_MAPPINGS,
        aliases: {}
        }
      })
      puts client.indices.put_template({
        name: "learning",
        body: {
        order: 10,
        template: "learning-*",
        settings: {
          "index.refresh_interval": "5s"
        },
        mappings: LEARNING_MAPPINGS,
        aliases: {}
        }
      })
      puts client.indices.put_template({
        name: "backend",
        body: {
        order: 10,
        template: "backend-*",
        settings: {
          "index.refresh_interval": "5s"
        },
        mappings: BACKEND_EVENT_MAPPINGS,
        aliases: {}
        }
      })
    end
    def get(index,type,id)
      begin
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
