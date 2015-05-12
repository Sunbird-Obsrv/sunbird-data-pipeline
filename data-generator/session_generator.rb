require 'securerandom'
require 'pry'
require 'geocoder'
# require 'ruby-progressbar'
require_relative '../data-indexer/indexers.rb'

module Generator
  DEVICES = 25
  USERS = 200
  SESSIONS = 600
  class Location
    SLEEP_INTERVAL=0.2
    ADDRESS_COMPONENTS_MAPPINGS={
      city: :locality,
      district: :administrative_area_level_2,
      state: :administrative_area_level_1,
      country: :country
    }
    attr_reader :loc,:city,:district,:state,:country
    def initialize(loc)
      @loc=loc
      # @results=reverse_search
      # set_identity
    end
    private
    def reverse_search
      sleep(SLEEP_INTERVAL)
      Geocoder.search(loc)
    end
    def set_identity
      @city = get_name(:city)
      @district = get_name(:district)
      @state = get_name(:state)
      @country = get_name(:country)
      raise 'Location not set!' if(@city&&@district&&@state&&@country).nil?
    end
    def get_name(type)
      begin
        result = @results.find{|r|!r.address_components_of_type(ADDRESS_COMPONENTS_MAPPINGS[type]).empty?}
        result.address_components_of_type(ADDRESS_COMPONENTS_MAPPINGS[type]).first['long_name']
      rescue => e
        ""
      end
    end
  end
  class User
    attr_reader :uid
    def initialize
      @uid = SecureRandom.uuid
    end
  end
  class Device
    ANDROID_VERS = ['Android 5.0','Android 4.4','Android 4.1','Android 4.0']
    MAKES = ['Make A','Make B','Make C','Make D','Make E']
    attr_reader :id,:os,:make,:spec,:location
    def initialize(loc="#{rand(12.0..20.0)},#{rand(74.0..78.0)}")
      @id = SecureRandom.uuid
      @os = ANDROID_VERS.sample
      @make = MAKES.sample
      @location = Location.new(loc)
      @spec = "v1,1,.01,16,1,2,1,1,75,0"
    end
    def loc=(loc)
      @location = Location.new(loc)
    end
    def to_json
      {
        id: @id,
        os: @os,
        make: @make,
        loc: @location.loc,
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
    attr_reader :sid
    def initialize(user,device,start_time=rand(START_TIME..END_TIME))
      @sid = SecureRandom.uuid
      @start  = start_time
      @finish = rand(@start..(@start+rand(120..300)))
      # puts "#{SESSION_START_EVENT} : #{@start}\n"
      # puts "#{SESSION_END_EVENT}   : #{@finish}\n"
      @user = user
      @device = device
      @signup = (@start-rand(1..24)*3600)
      @startup = (@signup-rand(1..24)*3600)
      @shutdown = (@finish+rand(1..24)*3600)
      @gamestart = @start+5
      @gameend  = @finish-5
    end
    def to_json
      events.to_json
    end
    def events
      [
        {
          eid: GE_GENIE_START, # unique event ID
          ts: @startup.strftime('%Y-%m-%dT%H:%M:%S%z'),
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
                loc: @device.location.loc, # Location in lat,long format
                ldata: {
                  locality: @device.location.city,
                  district: @device.location.district,
                  state: @device.location.state,
                  country: @device.location.country
                }
             }
          }
        },
        # {
        #   ts: @signup.strftime('%Y-%m-%dT%H:%M:%S%z'), #how early id she register
        #   ver: "1.0",
        #   gdata: {
        #     id: "genie.android",
        #     ver: "1.0"
        #   },
        #   sid: @sid,
        #   did: @device.id,
        #   uid: "",
        #   eid: GE_SIGNUP,
        #   edata: {
        #     eks: {
        #       uid: @user.uid,
        #       err: ""
        #     }
        #   }
        # },
        {
          ts: @start.strftime('%Y-%m-%dT%H:%M:%S%z'),
          ver: "1.0",
          gdata: {
            id: "genie.android",
            ver: "1.0"
          },
          sid: @sid,
          did: @device.id,
          uid: @user.uid,
          eid: SESSION_START_EVENT,
          edata: {
            eks:{
               ueksid: "",
               loc: @device.location.loc
            }
          }
        },
        {
          eid: GE_LAUNCH_GAME,
          ts: (@gamestart).strftime('%Y-%m-%dT%H:%M:%S%z'),
          ver: "1.0",
          gdata: {
            id: "genie.android",
            ver: "1.0"
          },
          sid: @sid,
          uid: @user.uid,
          did: @device.id,
          edata: {
            eks:{
              gid: "lit.scrnr.kan.android",
              err: ""
            }
          }
        },
        {
          eid: GE_GAME_END,
          ts: (@gameend).strftime('%Y-%m-%dT%H:%M:%S%z'),
          ver: "1.0",
          gdata: {
            id: "genie.android",
            ver: "1.0"
          },
          sid: @sid,
          uid: @user.uid,
          did: @device.id,
          edata: {
            eks:{
              gid: "lit.scrnr.kan.android",
              length: ((@gameend - @gamestart).to_i/3600.0).round(2)
            }
          }
        },
        {
          ts: @finish.strftime('%Y-%m-%dT%H:%M:%S%z'),
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
          ts: @shutdown.strftime('%Y-%m-%dT%H:%M:%S%z'),
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
      @logger = Logger.new(File.expand_path("./logs/logfile.log", File.dirname(__FILE__)))
    end
    def run
      SESSIONS.times do
        session = Session.new(@user_pool.sample,@device_pool.sample)
        # @logger.info "SESSION #{session.to_json}"
        yield session,@logger
      end
    end
  end
end
