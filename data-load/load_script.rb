require 'CSV'
require 'elasticsearch'
require 'pry'
require 'hashie'

class DataLoader
  CSV_FILE = 'Genie data in sheet 1 - Sheet1.csv'
  def initialize
    @client = Elasticsearch::Client.new log: false
    delete_indices
    create_indices
    @results = Hashie::Mash.new(overall:{})
  end
  def delete_indices
    @client.indices.delete index: 'identities'
  end
  def create_indices
    # creating all types in the same index as of now to avoid
    # unnecessary sharding
    @client.indices.create({
      index: 'identities',
      body: {
        #settings: {}
        mappings: {
          identities_v1: {
            _id: {
              path: 'users_id'
            },
            properties: {
              users_id: { type: 'string' }
            }
          },
          devices_v1: {
            _id: {
              path: 'device_id'
            },
            properties: {
              device_id: { type: 'string' }
              # device_name: { type: 'string' }
            }
          },
          sessions_v1: {
            # _parent: { type: "identities_v1" }, ?
            _id: {
              path: 'session_id'
            },
            properties: {
              session_id: { type: 'string' },
              users_id: { type: 'string' },
              device_id: { type: 'string' },
              device_name: { type: 'string' },
              android_version: { type: 'string' },
              location: { type: 'geo_point'},
              start_interaction_time: { type: 'date' },
              end_interaction_time: { type: 'date' },
              interaction_time: { type: 'double' }
            }
        }
        }
      }
    })
  end
  def index(id,index,type,body)
    result = nil
    begin
      @results.send("#{type}=",{unique:0,missing:0,error:0}) unless(@results.send("#{type}"))
      if(id)
        result = @client.index({
          index: index,
          type: type,
          body: body
          }
        )
        # binding.pry
        @results.send(type).send('unique=',@results.send(type).send('unique')+1) if result['created']
      else
        @results.send(type).send('missing=',@results.send(type).send('missing')+1)
      end
    rescue => e
      puts e
      @results.send(type).send('error=',@results.send(type).send('error')+1)
    end
    result
  end
  def publish
    puts @results.to_hash
  end
  def run!
    @interaction_time = 0
    CSV.foreach("./CSV/#{CSV_FILE}", headers: true) do |row|
      id = row['STUDENT ID']
      result = index(
        id,
        'identities',
        'identities_v1',
        {
          users_id: id
        }
      )
      id = row['DEVICE ID']
        result = index(
          id,
          'identities',
          'devices_v1',
          {
            device_id: id
          }
        )
      id = row['SESSION ID']
      user_id = row['STUDENT ID']
      device_id = row['DEVICE ID']
      device_name,android_version,
      location,start_interaction_time,
      end_interaction_time = nil
      interaction_time = row['TOTAL TIME OF INTERACTION (HH:MM:SS)'].match(/(\d+):(\d+):(\d+)/)[1..-1].map(&:to_i)
      interaction_time = interaction_time.inject { |sum, n| i||=0;d=(60.0**(i+=1));sum+n/d }.round(2)
      @interaction_time+=interaction_time
      interaction_time_raw = row['TOTAL TIME OF INTERACTION (HH:MM:SS)']
      result = index(
        id,
        'identities',
        'sessions_v1',
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
    end
    @results.overall.interaction_time = @interaction_time
  end
end

loader = DataLoader.new
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
