require 'elasticsearch'
require_relative '../../../data-indexer/indexers.rb'

module CommonSteps
  module ElasticsearchClient
    include Spinach::DSL

    def elastic_search_client
      @elasticsearch_client ||= ::Elasticsearch::Client.new log: false
    end

    def create_templates
      elastic_search_client.indices.put_template({
        name: "test",
        body: {
        order: 10,
        template: "test*",
        settings: {
          "index.refresh_interval": "1s"
        },
        mappings: Indexers::Elasticsearch::TEMPLATE_MAPPINGS,
        aliases: {}
        }
      })
    end

    def refresh_index
      puts elastic_search_client.indices.refresh index: 'test*'
    end

    def search(q)
      Hashie::Mash.new elastic_search_client.search({index:'test*',size:1000}.merge(q))
    end

    def get_specific_type_of_event_for_user(event_type, user)
      q = search({index:'test*',body: {
        "query"=> {
          "term"=>{
            "eid"=> event_type
          }
        },
        "filter": {
            "bool": {
              "must": [
                {
                  "query": {
                    "match": {
                      "uid": {
                        "query": user.uid,
                        "type": "phrase"
                      }
                    }
                  }
                },
              ]
              }
            }
          }})
    end

    def get_all_signup_events_for_user(event_type, user)
      q = search({index:'test*',body: {
        "query"=> {
          "term"=>{
            "eid"=> event_type
          }
        },
        "filter": {
            "bool": {
              "must": [
                {
                  "query": {
                    "match": {
                      "edata.eks.uid": {
                        "query": user.uid,
                        "type": "phrase"
                      }
                    }
                  }
                },
              ]
              }
            }
          }})
    end
  end
end
