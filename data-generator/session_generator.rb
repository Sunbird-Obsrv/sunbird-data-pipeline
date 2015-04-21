require 'securerandom'
require 'pry'
require_relative '../data-indexer/indexers.rb'

module Generator
  DEVICES = 10
  USERS = 100
  SESSIONS = 10
  class User
    attr_reader :uid
    def initialize
      @uid = SecureRandom.uuid
    end
  end
  class Device
    ANDROID_VERS = ['Android 5.0','Android 4.4','Android 4.1','Android 4.0']
    MAKES = ['Make A','Make B','Make C','Make D','Make E']
    attr_reader :id,:os,:make,:spec,:loc
    def initialize
      @id = SecureRandom.uuid
      @os = ANDROID_VERS.sample
      @make = MAKES.sample
      @loc = "#{rand(16.0..28.0)},#{rand(74.0..82.0)}"
      @spec = "v1,1,.01,16,1,2,1,1,75,0"
    end
    def to_json
      {
        id: @id,
        os: @os,
        make: @make,
        loc: @loc,
        spec: @spec
      }
    end
  end
  class Session
    SECONDS_IN_A_DAY = 86400
    DAYS = 10
    END_TIME = Time.now
    START_TIME = END_TIME - DAYS*SECONDS_IN_A_DAY
    GE_GENIE_START = 'GE_GENIE_START'
    GE_GENIE_END = 'GE_GENIE_END'
    GE_SIGNUP = 'GE_SIGNUP'
    SESSION_START_EVENT = 'GE_SESSION_START'
    SESSION_END_EVENT = 'GE_SESSION_END'
    GE_LAUNCH_GAME = 'GE_LAUNCH_GAME'
    GE_GAME_END = 'GE_GAME_END'
    MIN_SESSION_TIME = 120
    MAX_SESSION_TIME = 300
    def initialize(user,device)
      @sid = SecureRandom.uuid
      @start  = rand(START_TIME..END_TIME)
      @finish = rand(@start..(@start+rand(120..300)))
      puts "#{SESSION_START_EVENT} : #{@start}\n"
      puts "#{SESSION_END_EVENT}   : #{@finish}\n"
      @user = user
      @device = device
      @signup = (@start-rand(1..24)*3600)
      @startup = (@signup-rand(1..24)*3600)
      @shutdown = (@finish+rand(1..24)*3600)
    end
    def events
      [
        {
          eid: GE_GENIE_START, # unique event ID
          ts: @startup.to_i*1000,
          ver: 1.0,
          gdata: {
             id: "genie.android",
             ver: "1.0"
          },
          sid: "",
          uid: "",
          did: @device.id,
          edata: {
             eks: {
                dspec: {
                   os: @device.os,
                   make: @device.make, # device make and model
                   mem: 1000, # total mem in MB
                   idisk: 8, # total internal disk in GB
                   edisk: 32, # total external disk (card) in GB
                   scrn: 4.5, # in inches
                   camera: "13,1.3", # primary and secondary camera
                   cpu: "2.7 GHz Qualcomm Snapdragon 805 Quad Core",
                   sims: 2, # number of sim cards
                   cap: ["GPS","BT","WIFI","3G","ACCEL"] # capabilities enums
                },
                loc: @device.loc # Location in lat,long format
             }
          }
        },
        {
          ts: @signup.to_i*1000, #how early id she register
          ver: "1.0",
          gdata: {
            id: "genie.android",
            ver: "1.0"
          },
          sid: @sid,
          did: @device.id,
          uid: "",
          eid: GE_SIGNUP,
          edata: {
            eks: {
              uid: @user.uid,
              err: ""
            }
          }
        },
        {
          ts: @start.to_i*1000,
          ver: "1.0",
          gdata: {
            id: "genie.android",
            ver: "1.0"
          },
          sid: @sid,
          did: @device.id,
          uid: @user.uid,
          eid: SESSION_START_EVENT,
          edata: {}
        },
        {
          eid: GE_LAUNCH_GAME,
          ts: (@start+5).to_i*1000,
          ver: "1.0",
          gdata: {
            id: "genie.android",
            ver: "1.0"
          },
          sid: @sid,
          uid: @user.uid,
          did: @id,
          edata: {
            eks:{
              gid: "lit.scrnr.kan.android",
              err: ""
            }
          }
        },
        {
          eid: GE_GAME_END,
          ts: (@finish-5).to_i*1000,
          ver: "1.0",
          gdata: {
            id: "genie.android",
            ver: "1.0"
          },
          sid: @sid,
          uid: @user.uid,
          did: @id,
          edata: {
            eks:{
              gid: "lit.scrnr.kan.android",
              err: ""
            }
          }
        },
        {
          ts: @finish.to_i*1000,
          ver: "1.0",
          gdata: {
            id: "genie.android",
            ver: "1.0"
          },
          sid: @sid,
          did: @device.id,
          uid: @user.uid,
          eid: SESSION_END_EVENT,
          edata: {
            eks: {
              length: ((@finish - @start).to_i/3600.0).round(2)
            }
          }
        },
        {
          eid: GE_GENIE_END, # unique event ID
          ts: @shutdown.to_i*1000,
          ver: 1.0,
          gdata: {
             id: "genie.android",
             ver: "1.0"
          },
          sid: "",
          uid: "",
          did: @device.id,
          edata: {
             eks: {
                length: ((@shutdown-@startup).to_i/3600.0).round(2)
             }
          }
        },
      ]
    end
  end
  class Runner
    def initialize
      @user_pool = Array.new(USERS) {User.new}
      @device_pool = Array.new(DEVICES) {Device.new}
    end
    def run
      SESSIONS.times do
        yield Session.new(@user_pool.sample,@device_pool.sample)
      end
    end
  end
end

client = ::Indexers::Elasticsearch.new
r = Generator::Runner.new
time = 0

r.run do |session|
  session.events.each do |event|
    result = client.index('identities','events_v1',event)
    # result = client.index('identities','first_interactions_v1',{uid:event[:uid],ts:event[:ts]})
    # if(event[:eid]==Generator::Session::SESSION_START_EVENT)
    #   result = client.index('identities','sessions_v1',{
    #     sid:event[:sid],
    #     ts: event[:ts],
    #     ddata: event[:ddata]
    #   })
    #   time = event[:ts]
    # elsif(event[:eid]==Generator::Session::SESSION_END_EVENT)
    #   duration = (((event[:ts] - time).to_i)/(1000.0*3600)).round(2)
    #   result = client.update('identities','sessions_v1',event[:sid],{ te: event[:ts], duration: duration })
    #   time = 0
    # end
  end
end
