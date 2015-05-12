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
          "index.refresh_interval": "5s"
        },
        mappings: Indexers::Elasticsearch::TEMPLATE_MAPPINGS,
        aliases: {}
        }
      })
    end

    def refresh_index
      elastic_search_client.indices.refresh index: 'test*'
    end

    def search(q)
      Hashie::Mash.new elastic_search_client.search({index:'test*',size:1000}.merge(q))
    end

  end
end
