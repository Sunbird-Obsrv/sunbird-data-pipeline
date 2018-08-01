require 'elasticsearch'
require 'pry'

module Indexers
  class Elasticsearch
    SUMMARY_MAPPINGS = {
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
                match: "id|current|res|exres|max|mc|mmc|category|channel",
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
              "contentdata": {
                "properties": {
                  "ageGroup": {
                    "type": "string"
                  },
                  "author": {
                    "type": "string"
                  },
                  "audience": {
                    "type": "string"
                  },
                  "code": {
                    "type": "string"
                  },
                  "collaborators": {
                    "type": "string"
                  },
                  "collections": {
                    "type": "string"
                  },
                  "concepts": {
                    "type": "string"
                  },
                  "contentType": {
                    "type": "string"
                  },
                  "curriculum": {
                    "type": "string"
                  },
                  "developer": {
                    "type": "string"
                  },
                  "domain": {
                    "type": "string"
                  },
                  "downloadUrl": {
                    "type": "string"
                  },
                  "downloads": {
                    "type": "long"
                  },
                  "edition": {
                    "type": "string"
                  },
                  "genre": {
                    "type": "string"
                  },
                  "gradeLevel": {
                    "type": "string"
                  },
                  "keywords": {
                    "type": "string"
                  },
                  "me_totalDevices": {
                    "type": "long"
                  },
                  "me_totalDownloads": {
                    "type": "long"
                  },
                  "me_totalInteractions": {
                    "type": "long"
                  },
                  "me_totalRatings": {
                    "type": "long"
                  },
                  "me_totalSessionsCount": {
                    "type": "long"
                  },
                  "me_totalSideloads": {
                    "type": "long"
                  },
                  "me_totalTimespent": {
                    "type": "long"
                  },
                  "me_totalUsage": {
                    "type": "long"
                  },
                  "medium": {
                    "type": "string"
                  },
                  "methods": {
                    "type": "string"
                  },
                  "name": {
                    "type": "string"
                  },
                  "owner": {
                    "type": "string"
                  },
                  "popularity": {
                    "type": "long"
                  },
                  "portalOwner": {
                    "type": "string"
                  },
                  "publication": {
                    "type": "string"
                  },
                  "publisher": {
                    "type": "string"
                  },
                  "rating": {
                    "type": "long"
                  },
                  "size": {
                    "type": "long"
                  },
                  "source": {
                    "type": "string"
                  },
                  "status": {
                    "type": "string"
                  },
                  "subject": {
                    "type": "string"
                  },
                  "templateType": {
                    "type": "string"
                  },
                  "theme": {
                    "type": "string"
                  },
                  "words": {
                    "type": "string"
                  }
                }
              },
              "itemdata": {
                "properties": {
                  "concepts": {
                    "type": "string"
                  },
                  "createdBy": {
                    "type": "string"
                  },
                  "createdOn": {
                    "format": "strict_date_optional_time||epoch_millis",
                    "type": "date"
                  },
                  "keywords": {
                    "type": "string"
                  },
                  "language": {
                    "type": "string"
                  },
                  "lastUpdatedBy": {
                    "type": "string"
                  },
                  "lastUpdatedOn": {
                    "format": "strict_date_optional_time||epoch_millis",
                    "type": "date"
                  },
                  "media": {
                    "type": "string"
                  },
                  "name": {
                    "type": "string"
                  },
                  "num_answers": {
                    "type": "long"
                  },
                  "owner": {
                    "type": "string"
                  },
                  "qlevel": {
                    "type": "string"
                  },
                  "question": {
                    "type": "string"
                  },
                  "source": {
                    "type": "string"
                  },
                  "status": {
                    "type": "string"
                  },
                  "template": {
                    "type": "string"
                  },
                  "title": {
                    "type": "string"
                  },
                  "type": {
                    "type": "string"
                  },
                  "version": {
                    "type": "long"
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
    BASIC_TELEMETRY_MAPPINGS = {
      _default_:{
        dynamic: false,
        properties: {
          "@timestamp": {
            "format": "strict_date_optional_time||epoch_millis",
            "type": "date"
          },
          "@version": {
            "type": "string"
          },
          "channel": {
            "type": "string"
          },
          "eid": {
            "type": "string"
          },
          "ets": {
            "format": "strict_date_optional_time||epoch_millis",
            "type": "date"
          },
          "syncts": {
            "format": "strict_date_optional_time||epoch_millis",
            "type": "date"
          },
          "mid": {
            "type": "string"
          },
          "ts": {
            "format": "strict_date_optional_time||epoch_millis",
            "type": "date"
          },
          "context": {
            "properties": {
              "channel": {
                "type": "string"
              },
              "env": {
                "type": "string"
              },
              "sid": {
                "type": "string"
              },
              "did": {
                "type": "string"
              },
              "pdata": {
                "properties": {
                  "id": {
                    "type": "string"
                  },
                  "pid": {
                    "type": "string"
                  },
                  "ver": {
                    "type": "string"
                  }
                }
              }
            }
          },
          "dimensions": {
            "properties": {
              "channel": {
                "type": "string"
              },
              "sid": {
                "type": "string"
              },
              "did": {
                "type": "string"
              },
              "pdata": {
                "properties": {
                  "id": {
                    "type": "string"
                  },
                  "pid": {
                    "type": "string"
                  },
                  "ver": {
                    "type": "string"
                  }
                }
              }
            }
          },
          "metadata": {
            "properties": {
              "source": {
                "type": "string"
              },
              "src": {
                "type": "string"
              },
              "index_name": {
                "type": "string"
              },
              "index_type": {
                "type": "string"
              }
            }
          }
        }
      }
    }
    TELEMETRY_MAPPINGS = {
      _default_:{
        dynamic: false,
        properties: {
          "@timestamp": {
            "format": "strict_date_optional_time||epoch_millis",
            "type": "date"
          },
          "@version": {
            "type": "string"
          },
          "eid": {
            "type": "string"
          },
          "ets": {
            "format": "strict_date_optional_time||epoch_millis",
            "type": "date"
          },
          "mid": {
            "type": "string"
          },
          "ts": {
            "format": "strict_date_optional_time||epoch_millis",
            "type": "date"
          },
          "ver": {
            "type": "string"
          },
          "actor": {
             "properties": {
                "id": {
                  "type": "string"
                },
                "type": {
                  "type": "string"
                }
             }
          },
          "edata": {
            "properties": {
              "comments": {
                "type": "string"
              },
              "correlationid": {
                "type": "string"
              },
              "duration": {
                "type": "double"
              },
              "data": {
                "type": "string"
              },
              "dir": {
                "type": "string"
              },
              "err": {
                "type": "string"
              },
              "errtype": {
                "type": "string"
              },
              "id": {
                "type": "string"
              },
              "level": {
                "type": "string"
              },
              "loc": {
                "type": "string"
              },
              "message": {
                "type": "string"
              },
              "mode": {
                "type": "string"
              },
              "pass": {
                "type": "string"
              },
              "prevstate": {
                "type": "string"
              },
              "pageid": {
                "type": "string"
              },
              "query": {
                "type": "string"
              },
              "rating": {
                "type": "double"
              },
              "score": {
                "type": "double"
              },
              "size": {
                "type": "double"
              },
              "stacktrace": {
                "type": "string"
              },
              "state": {
                "type": "string"
              },
              "subtype": {
                "type": "string"
              },
              "type": {
                "type": "string"
              },
              "uri": {
                "type": "string"
              },
              "items": {
                "type": "nested",
                "properties": {
                  "id": {
                    "type": "string"
                  },
                  "ver": {
                    "type": "string"
                  },
                  "type": {
                    "type": "string"
                  },
                  "origin": {
                    "properties": {
                      "id": {
                        "type": "string"
                      },
                      "type": {
                        "type": "string"
                      }
                    }
                  }
                }
              },
              "item": {
                "properties": {
                  "id": {
                    "type": "string"
                  },
                  "maxscore": {
                    "type": "long"
                  },
                  "exlength": {
                    "type": "long"
                  },
                  "uri": {
                    "type": "string"
                  },
                  "desc": {
                    "type": "string"
                  },
                  "title": {
                    "type": "string"
                  }
                }
              },
              "target": {
                "properties": {
                  "id": {
                    "type": "string"
                  },
                  "ver": {
                    "type": "string"
                  },
                  "type": {
                    "type": "string"
                  },
                  "category": {
                    "type": "string"
                  },
                  "parent": {
                    "properties": {
                      "id": {
                        "type": "string"
                      },
                      "type": {
                        "type": "string"
                      }
                    }
                  }
                }
              },
              "visits": {
                "type": "nested",
                "properties": {
                  "objid": {
                    "type": "string"
                  },
                  "objtype": {
                    "type": "string"
                  }
                }
              },
              "plugin": {
                "properties": {
                  "id": {
                    "type": "string"
                  },
                  "ver": {
                    "type": "string"
                  },
                  "category": {
                    "type": "string"
                  }
                }
              },
              "object": {
                "properties": {
                  "id": {
                    "type": "string"
                  },
                  "type": {
                    "type": "string"
                  },
                  "subtype": {
                    "type": "string"
                  },
                  "ver": {
                    "type": "string"
                  }
                }
              },
              "uaspec": {
                "properties": {
                  "agent": {
                    "type": "string"
                  },
                  "ver": {
                    "type": "string"
                  },
                  "system": {
                    "type": "string"
                  },
                  "platform": {
                    "type": "string"
                  },
                  "raw": {
                    "type": "string"
                  }
                }
              },
              "dspec": {
                "properties": {
                   "camera": {
                      "type": "string"
                    },
                    "cpu": {
                      "type": "string"
                    },
                    "edisk": {
                      "type": "double"
                    },
                    "id": {
                      "type": "string"
                    },
                    "idisk": {
                      "type": "double"
                    },
                    "make": {
                      "type": "string"
                    },
                    "mem": {
                      "type": "double"
                    },
                    "os": {
                      "type": "string"
                    },
                    "scrn": {
                      "type": "double"
                    },
                    "sims": {
                      "type": "double"
                    }
                }
              }
            }
          },
          "context": {
            "properties": {
              "channel": {
                "type": "string"
              },
              "env": {
                "type": "string"
              },
              "sid": {
                "type": "string"
              },
              "did": {
                "type": "string"
              },
              "pdata": {
                "properties": {
                  "id": {
                    "type": "string"
                  },
                  "pid": {
                    "type": "string"
                  },
                  "ver": {
                    "type": "string"
                  }
                }
              },
              "rollup": {
                "properties": {
                  "l1": {
                    "type": "string"
                  },
                  "l2": {
                    "type": "string"
                  },
                  "l3": {
                    "type": "string"
                  },
                  "l4": {
                    "type": "string"
                  }
                }
              },
              "cdata": {
                "type": "nested",
                "properties": {
                  "type": {
                    "type": "string"
                  },
                  "id": {
                    "type": "string"
                  }
                }
              }
            }
          },
          "object": {
            "properties": {
              "id": {
                "type": "string"
              },
              "type": {
                "type": "string"
              },
              "subtype": {
                "type": "string"
              },
              "parentid": {
                "type": "string"
              },
              "parenttype": {
                "type": "string"
              },
              "ver": {
                "type": "string"
              },
              "rollup": {
                "properties": {
                  "l1": {
                    "type": "string"
                  },
                  "l2": {
                    "type": "string"
                  },
                  "l3": {
                    "type": "string"
                  },
                  "l4": {
                    "type": "string"
                  }
                }
              }
            }
          },
          "metadata": {
            "properties": {
              "source": {
                "type": "string"
              },
              "index_name": {
                "type": "string"
              },
              "index_type": {
                "type": "string"
              },
              "source_eid": {
                "type": "string"
              },
              "source_mid": {
                "type": "string"
              },
              "pump": {
                "type": "string"
              }
            }
          },
          "flags": {
            "properties": {
              "v2_converted": {
                "type": "boolean"
              },
              "dd_processed": {
                "type": "boolean"
              },
              "tv_processed": {
                "type": "boolean"
              }
            }
          }
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
        name: "summary",
        body: {
        order: 10,
        template: "summary-*",
        settings: {
          "index.refresh_interval": "5s"
        },
        mappings: SUMMARY_MAPPINGS,
        aliases: {}
        }
      })
      puts client.indices.put_template({
        name: "summary-cumulative",
        body: {
        order: 10,
        template: "summary-cumulative-*",
        settings: {
          "index.refresh_interval": "5s"
        },
        mappings: SUMMARY_MAPPINGS,
        aliases: {}
        }
      })
      puts client.indices.put_template({
        name: "backend",
        body: {
        order: 10,
        template: "backend_*",
        settings: {
          "index.refresh_interval": "5s"
        },
        mappings: TELEMETRY_MAPPINGS,
        aliases: {}
        }
      })
      puts client.indices.put_template({
        name: "failed-telemetry",
        body: {
        order: 10,
        template: "failed-telemetry-*",
        settings: {
          "index.refresh_interval": "5s"
        },
        mappings: BASIC_TELEMETRY_MAPPINGS,
        aliases: {}
        }
      })
      puts client.indices.put_template({
        name: "telemetry",
        body: {
        order: 10,
        template: "telemetry-*",
        settings: {
          "index.refresh_interval": "5s"
        },
        mappings: TELEMETRY_MAPPINGS,
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
