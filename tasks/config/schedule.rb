# Use this file to easily define all of your cron jobs.
#
# It's helpful, but not entirely necessary to understand cron before proceeding.
# http://en.wikipedia.org/wiki/Cron

# Example:
#
require 'json'
set :output, {:error => 'error.log', :standard => 'cron.log'}

# Learn more: http://github.com/javan/whenever
set :environment_variable, 'EP_LOG_DIR'
set :environment, ENV['EP_LOG_DIR']
# set :environment_variable, 'KAFKA_TOPIC'
# set :environment, ENV['KAFKA_TOPIC']
# set :environment_variable, 'KAFKA_BROKERS'
# set :environment, ENV['KAFKA_BROKERS']
# set :environment_variable, 'ES_HOST'
# set :environment, ENV['ES_HOST']
# set :environment_variable, 'SYNC_DATES'
# set :environment, ENV['SYNC_DATES']

# every 6.hour do
#   rake "scheduled:handle_denormalizer"
# end

every 1.day, :at => '4:30 am' do
  rake "scheduled:session_summarizer[#{@kafka_brokers},#{@kafka_topic},#{@es_host}]"
end
