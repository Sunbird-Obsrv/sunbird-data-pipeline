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
                match: "id|current|res|exres|max|mc|mmc|category|env|type|stageid|objecttype|objectid|err|action|data|severity|channel",
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
              "contentdata": {
                "properties": {
                  "ageGroup": {
                    "type": "string"
                  },
                  "author": {
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
    BACKEND_EVENT_RAW_MAPPINGS = {
      _default_:{
        dynamic: false,
        properties:{
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
                      
        }
      }
    }
    BACKEND_EVENT_MAPPINGS = {
        _default_: {
            dynamic: false,
            properties: {
              "@timestamp": {
                "format": "strict_date_optional_time||epoch_millis",
                "type": "date"
              },
              "@version": {
                "type": "string"
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
              "context": {
                "properties": {
                  "content_id": {
                    "type": "string"
                  },
                  "granularity": {
                    "type": "string"
                  },
                  "pdata": {
                    "properties": {
                      "id": {
                        "type": "string"
                      },
                      "model": {
                        "type": "string"
                      },
                      "ver": {
                        "type": "string"
                      }
                    }
                  },
                  "sid": {
                    "type": "string"
                  }
                }
              },
              "cdata": {
                  "type": "nested",
                  "properties": {
                      "type": { "type": "string" },
                      "id": { "type": "string" }
                  },
              },
              "edata": {
                "properties": {
                  "eks": {
                    "properties": {
                        "action": {
                          "type": "string"
                        },
                        "authprovider": {
                          "type": "string"
                        },
                        "category": {
                          "type": "string"
                        },
                        "cid": {
                          "type": "string"
                        },
                        "class": {
                          "type": "string"
                        },
                        "client": {
                          "properties": {
                            "browser": {
                              "type": "string"
                            },
                            "browserver": {
                              "type": "string"
                            },
                            "os": {
                              "type": "string"
                            }
                          }
                        },
                        "containerid": {
                          "type": "string"
                        },
                        "containerplugin": {
                          "type": "string"
                        },
                        "contentType": {
                          "type": "string"
                        },
                        "context": {
                          "type": "string"
                        },
                        "correlationid": {
                          "type": "string"
                        },
                        "data": {
                          "properties": {
                            "config": {
                              "properties": {
                                "appName": {
                                  "type": "string"
                                },
                                "deviceMapping": {
                                  "type": "boolean"
                                },
                                "model": {
                                  "type": "string"
                                },
                                "output": {
                                  "properties": {
                                    "params": {
                                      "properties": {
                                        "printEvent": {
                                          "type": "boolean"
                                        }
                                      }
                                    },
                                    "to": {
                                      "type": "string"
                                    }
                                  }
                                },
                                "parallelization": {
                                  "type": "long"
                                },
                                "search": {
                                  "properties": {
                                    "queries": {
                                      "properties": {
                                        "bucket": {
                                          "type": "string"
                                        },
                                        "delta": {
                                          "type": "long"
                                        },
                                        "endDate": {
                                          "format": "strict_date_optional_time||epoch_millis",
                                          "type": "date"
                                        },
                                        "prefix": {
                                          "type": "string"
                                        }
                                      }
                                    },
                                    "type": {
                                      "type": "string"
                                    }
                                  }
                                }
                              }
                            }
                          }
                        },
                        "defaultPlugins": {
                          "type": "string"
                        },
                        "downloadUrl": {
                          "type": "string"
                        },
                        "duration": {
                          "type": "long"
                        },
                        "env": {
                          "type": "string"
                        },
                        "filters": {
                          "type": "object"
                        },
                        "id": {
                          "type": "string"
                        },
                        "level": {
                          "type": "string"
                        },
                        "loadtimes": {
                                    "properties": {
                                      "contentLoad": {
                                        "type": "long"
                                      },
                                      "plugins": {
                                        "type": "long"
                                      }
                                    }
                        },
                        "mediaType": {
                          "type": "string"
                        },
                        "message": {
                          "type": "string"
                        },
                        "method": {
                          "type": "string"
                        },
                        "name": {
                          "type": "string"
                        },
                        "objectid": {
                          "type": "string"
                        },
                        "pluginid": {
                          "type": "string"
                        },
                        "pluginver": {
                          "type": "string"
                        },
                        "prevstate": {
                          "type": "string"
                        },
                        "parentid": {
                          "type": "string"
                        },
                        "protocol": {
                          "type": "string"
                        },
                        "query": {
                          "type": "string"
                        },
                        "raw": {
                          "type": "string"
                        },
                        "rid": {
                          "type": "string"
                        },
                        "size": {
                          "type": "long"
                        },
                        "stage": {
                          "type": "string"
                        },
                        "state": {
                          "type": "string"
                        },
                        "status": {
                          "type": "string"
                        },
                        "subtype": {
                          "type": "string"
                        },
                        "target": {
                          "type": "string"
                        },
                        "title": {
                          "type": "string"
                        },
                        "type": {
                          "type": "string"
                        },
                        "uaspec": {
                          "properties": {
                            "agent": {
                              "type": "string"
                            },
                            "platform": {
                              "type": "string"
                            },
                            "system": {
                              "type": "string"
                            },
                            "ver": {
                              "type": "string"
                            }
                          }
                        },
                        "uip": {
                          "type": "string"
                        },
                        "url": {
                          "type": "string"
                        },
                        "value": {
                          "type": "long"
                        },
                        "code": {
                          "type": "string"
                        },
                        "email": {
                          "type": "string"
                        },
                        "partners": {
                          "type": "nested",
                           "properties": {
                                  "id": { "type": "string" },
                                  "value": { "type": "string" }
                              },
                        },
                        "access": {
                          "type": "nested",
                           "properties": {
                                  "id": { "type": "string" },
                                  "value": { "type": "string" }
                              },
                        },
                        "profile": {
                          "type": "nested",
                           "properties": {
                                  "id": { "type": "string" },
                                  "value": { "type": "string" }
                              },
                        }
                    }
                  }
                }
              },
              "eid": {
                "type": "string"
              },
              "ets": {
                "format": "strict_date_optional_time||epoch_millis",
                "type": "date"
              },
              "host": {
                "type": "string"
              },
              "metadata": {
                "properties": {
                  "checksum": {
                    "type": "string",
                  },
                  "hour": {
                    "type": "string"
                  },
                  "month": {
                    "type": "string"
                  },
                  "monthday": {
                    "type": "string"
                  },
                  "sync_timestamp": {
                    "format": "strict_date_optional_time||epoch_millis",
                    "type": "date"
                  },
                  "ts_month": {
                    "type": "string"
                  },
                  "ts_year": {
                    "type": "string"
                  },
                  "year": {
                    "type": "string"
                  }
                }
              },
              "mid": {
                "type": "string"
              },
              "path": {
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
              "rid": {
                "type": "string"
              },
              "syncts": {
                "type": "long"
              },
              "tags": {
                "type": "string"
              },
              "ts": {
                "format": "strict_date_optional_time||epoch_millis",
                "type": "date"
              },
              "type": {
                "type": "string"
              },
              "uid": {
                "type": "string"
              },
              "ver": {
                "type": "string"
              },
              "channel": {
                "type": "string"
              },
              "portaluserdata": {
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
                  "code": {
                    "type": "string"
                  },
                  "name": {
                    "type": "string"
                  },
                  "details": {
                    "type": "string"
                  }
                }
              },
              "etags": {
                "properties": {
                  "app": {
                    "type": "string"
                  },
                  "partner": {
                    "type": "string"
                  },
                  "dims": {
                    "type": "string"
                  }
                }
              }
            }  
        }
    }
    INFRA_EVENT_MAPPINGS = {
        _default_: {
            dynamic: false,
            properties: {
              "@timestamp": {
                "format": "strict_date_optional_time||epoch_millis",
                "type": "date"
              },
              "@version": {
                "type": "string"
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
              "context": {
                "properties": {
                  "content_id": {
                    "type": "string"
                  },
                  "granularity": {
                    "type": "string"
                  },
                  "pdata": {
                    "properties": {
                      "id": {
                        "type": "string"
                      },
                      "model": {
                        "type": "string"
                      },
                      "ver": {
                        "type": "string"
                      }
                    }
                  },
                  "sid": {
                    "type": "string"
                  }
                }
              },
              "cdata": {
                  "type": "nested",
                  "properties": {
                      "type": { "type": "string" },
                      "id": { "type": "string" }
                  },
              },
              "edata": {
                "properties": {
                  "eks": {
                    "dynamic": true,
                    "properties": {
                      "rid": {
                        "type": "string"
                      },
                      "uip": {
                        "type": "string"
                      },
                      "type": {
                        "type": "string"
                      },
                      "title": {
                        "type": "string"
                      },
                      "category": {
                        "type": "string"
                      },
                      "size": {
                        "type": "long"
                      },
                      "duration": {
                        "type": "long"
                      },
                      "status": {
                        "type": "string"
                      },
                      "protocol": {
                        "type": "string"
                      },
                      "method": {
                        "type": "string"
                      },
                      "action": {
                        "type": "string"
                      },
                      "params": {
                        "properties": {
                          "key": {
                            "type": "string"
                          }
                        }
                      },
                      "message": {
                        "type": "string"
                      },
                      "class": {
                        "type": "string"
                      },
                      "level": {
                        "type": "string"
                      },
                      "data": {
                        "properties":{
                          "config": {
                            "dynamic": true,
                            "type": "object"
                          },
                          "count":{
                            "type": "long"
                          },
                          "date": {
                            "type": "string"
                          },
                          "inputEvents": {
                            "type": "long"
                          },
                          "outputEvents": {
                            "type": "long"
                          },
                          "timeTaken": {
                            "type": "long"
                          }
                        } 
                      }
                    }
                  }
                }
              },
              "eid": {
                "type": "string"
              },
              "ets": {
                "format": "strict_date_optional_time||epoch_millis",
                "type": "date"
              },
              "host": {
                "type": "string"
              },
              "metadata": {
                "properties": {
                  "checksum": {
                    "type": "string",
                  },
                  "hour": {
                    "type": "string"
                  },
                  "month": {
                    "type": "string"
                  },
                  "monthday": {
                    "type": "string"
                  },
                  "sync_timestamp": {
                    "format": "strict_date_optional_time||epoch_millis",
                    "type": "date"
                  },
                  "ts_month": {
                    "type": "string"
                  },
                  "ts_year": {
                    "type": "string"
                  },
                  "year": {
                    "type": "string"
                  }
                }
              },
              "mid": {
                "type": "string"
              },
              "path": {
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
              "rid": {
                "type": "string"
              },
              "syncts": {
                "type": "long"
              },
              "tags": {
                "type": "string"
              },
              "ts": {
                "format": "strict_date_optional_time||epoch_millis",
                "type": "date"
              },
              "type": {
                "type": "string"
              },
              "uid": {
                "type": "string"
              },
              "ver": {
                "type": "string"
              },
              "channel": {
                "type": "string"
              },
              "portaluserdata": {
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
                  "code": {
                    "type": "string"
                  },
                  "name": {
                    "type": "string"
                  },
                  "details": {
                    "type": "string"
                  }
                }
              },
              "etags": {
                "properties": {
                  "app": {
                    "type": "string"
                  },
                  "partner": {
                    "type": "string"
                  },
                  "dims": {
                    "type": "string"
                  }
                }
              }
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
          "mid": {
            "type": "string"
          },
          "ts": {
            "format": "strict_date_optional_time||epoch_millis",
            "type": "date"
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
      puts client.indices.put_template({
        name: "infra",
        body: {
        order: 10,
        template: "infra-*",
        settings: {
          "index.refresh_interval": "5s"
        },
        mappings: INFRA_EVENT_MAPPINGS,
        aliases: {}
        }
      })
      puts client.indices.put_template({
        name: "backend_raw",
        body: {
        order: 10,
        template: "backend_raw_*",
        settings: {
          "index.refresh_interval": "5s"
        },
        mappings: BACKEND_EVENT_RAW_MAPPINGS,
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
