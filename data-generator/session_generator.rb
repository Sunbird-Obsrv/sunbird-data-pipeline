# sample_json = {
#    ts: 1415532976000, # timestamp of event capture in millis
#    ver: "1.0", # version of the event data structure, this allows future changes to the event structure
#    gdata: { # game data, all session events are handled by Genie
#       "id": "genie.android", # genie id
#       "ver": "1.0" # genie version number
#    },
#    sid: "de305d54-75b4-431b-adb2-eb6b9e546013", # uuid of the session
#    ddata: { # device data
#       did: "ff305d54-85b4-341b-da2f-eb6b9e5460fa", # uuid of the device, created during app registration
#       dos: "Android 4.5",
#       make: "Micromax A80", # device make and model
#       loc: "12.931249,77.623368", # Location in lat,long format
#       spec: "v1,1,.01,16,1,2,1,1,75,0" # device specs in a pre sepcified format
#    },
#    uid: "123e4567-e89b-12d3-a456-426655440000", # uuid of the user account
#    eid: "SE_GENIE_START", # unique event ID
#    edata: { # free style event specific data structure
#    }
# }

require 'securerandom'
require 'pry'


module Generator
  DEVICES = 10
  USERS = 100
  SESSIONS = 1000
  class User
    attr_reader :uid
    def initialize
      @uid = SecureRandom.uuid
    end
  end
  class Device
    ANDROID_VERS = ['Android 5.0','Android 4.4','Android 4.1','Android 4.0']
    MAKES = ['Make A','Make B','Make C','Make D','Make E']
    attr_reader :did,:dos,:make,:spec
    def initialize
      @did = SecureRandom.uuid
      @dos = ANDROID_VERS.sample
      @make = MAKES.sample
      @loc = "#{rand(16.0..28.0)},#{rand(74.0..82.0)}"
      @spec = "v1,1,.01,16,1,2,1,1,75,0"
    end
    def to_json
      {
        did: @did,
        dos: @dos,
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
    SESSION_START_EVENT = 'SE_SESSION_START'
    SESSION_END_EVENT = 'SE_SESSION_END'
    MIN_SESSION_TIME = 120
    MAX_SESSION_TIME = 300
    def initialize(user,device)
      @sid = SecureRandom.uuid
      @start  = rand(START_TIME..END_TIME)
      @finish = rand(@start..(@start+rand(120..300)))
      puts "#{SESSION_START_EVENT} : #{@start}\n"
      puts "#{SESSION_END_EVENT}   : #{@finish}\n"
      @uid = user.uid
      @device = device
    end
    def to_json
      [
        {
          ts: @start.to_i,
          ver: "1.0",
          gdata: {
            id: "genie.android",
            ver: "1.0"
          },
          sid: @sid,
          ddata: @device.to_json,
          uid: @uid,
          eid: SESSION_START_EVENT,
          edata: {}
        },
        {
          ts: @finish.to_i,
          ver: "1.0",
          gdata: {
            id: "genie.android",
            ver: "1.0"
          },
          sid: @sid,
          ddata: @device.to_json,
          uid: @uid,
          eid: SESSION_END_EVENT,
          edata: {}
        }
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
        s=Session.new(@user_pool.sample,@device_pool.sample)
        # puts s.to_json
      end
    end
  end
end

r=Generator::Runner.new
r.run
