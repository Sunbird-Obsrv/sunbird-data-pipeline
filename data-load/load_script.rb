require 'CSV'
require 'pry'
require 'hashie'
require_relative '../data-indexer/indexers.rb'

class DataLoader
  CSV_FILE = 'Genie data in sheet 1 - Sheet1.csv'
  def initialize(client)
    @client = client
    @client.delete_indices
    @client.create_indices
    @results = Hashie::Mash.new(overall:{})
  end
  def publish
    puts @results.to_hash
  end
  def log_result(id,type,result,error=nil)
    unless error
      if(id)
        @results.send(type).send('unique=',@results.send(type).send('unique')+1) if result['created']
      else
        @results.send(type).send('missing=',@results.send(type).send('missing')+1)
      end
    else
      @results.send(type).send('error=',@results.send(type).send('error')+1)
    end
  end
  def run!
    @interaction_time = 0
    @results.identities_v1 = {unique:0,missing:0,error:0}
    @results.devices_v1 = {unique:0,missing:0,error:0}
    @results.sessions_v1 = {unique:0,missing:0,error:0}
    CSV.foreach("./CSV/#{CSV_FILE}", headers: true) do |row|
      begin
        id = row['STUDENT ID']
        type = 'identities_v1'
        result = @client.index(
          'identities',
          type,
          {
            users_id: id
          }
        )
        log_result(id,type,result)
      rescue => e
        puts e
        log_result(id,type,nil,e)
      end
      begin
        id = row['DEVICE ID']
        type = 'devices_v1'
        result = @client.index(
          'identities',
          type,
          {
            device_id: id
          }
        )
        log_result(id,type,result)
      rescue => e
        log_result(id,type,nil,e)
      end
      begin
        id = row['SESSION ID']
        type = 'sessions_v1'
        user_id = row['STUDENT ID']
        device_id = row['DEVICE ID']
        device_name,android_version,
        location,start_interaction_time,
        end_interaction_time = nil
        interaction_time = row['TOTAL TIME OF INTERACTION (HH:MM:SS)'].match(/(\d+):(\d+):(\d+)/)[1..-1].map(&:to_i)
        interaction_time = interaction_time.inject { |sum, n| i||=0;d=(60.0**(i+=1));sum+n/d }.round(2)
        @interaction_time+=interaction_time
        interaction_time_raw = row['TOTAL TIME OF INTERACTION (HH:MM:SS)']
        result = @client.index(
          'identities',
          type,
          {
            session_id: id,
            users_id: user_id,
            device_id: device_id,
            device_name: device_name,
            android_version: android_version,
            location: location,
            start_interaction_time: start_interaction_time,
            end_interaction_time: end_interaction_time,
            interaction_time: interaction_time,
            interaction_time_raw: interaction_time_raw
          }
        )
        log_result(id,type,result)
      rescue => e
        log_result(id,type,nil,e)
      end
    end
    @results.overall.interaction_time = @interaction_time
  end
end

client = Indexers::Elasticsearch.new
loader = DataLoader.new(client)
loader.run!
loader.publish

#
# LATER
#
# client.indices.create
# index: 'identities_metadata_v1',
# body: {
#   #settings: {}
#   mappings: {
#     document: {
#       # _parent: { type: "identities_v1" },
#       properties: {
#         users_id: { type: 'string' },
#         users_name: { type: 'string' }
#       }
#     }
#   }
# }
# client.indices.create
# index: 'identity_interactions_v1',
# body: {
#   #settings: {}
#   mappings: {
#     document: {
#       # _parent: { type: "identities_v1" },
#       properties: {
#         session_id: { type: 'string' },
#         users_id: { type: 'string' },
#         games_id: { type: 'string'},
#         starttime: { type: 'date' },
#         endtime: { type: 'date' }
#       }
#     }
#   }
# }
