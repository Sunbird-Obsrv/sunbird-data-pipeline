require 'elasticsearch'
require 'pry'

before_suite do
   # @elasticsearch_client = ::Elasticsearch::Client.new log: false
   # @elasticsearch_client.transport.reload_connections!
   # puts @elasticsearch_client.cluster.health
end

step 'I have played only once' do
  binding.pry
end

step 'There should be <num> GE_SESSION_START event' do |x|
end

step 'There should be <num> GE_SIGNUP event' do |x|
end

step 'I play <num> times on the same day' do |x|
end

step 'I play <num> times the next day' do |x|
end
