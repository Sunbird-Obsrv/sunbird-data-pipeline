require 'securerandom'
require 'pry'
require_relative '../data-indexer/indexers.rb'

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
    def events
      [
        {
          ts: @start.to_i*1000,
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
          ts: @finish.to_i*1000,
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
    result = client.index('identities','first_interactions_v1',{uid:event[:uid],ts:event[:ts]})
    if(event[:eid]==Generator::Session::SESSION_START_EVENT)
      result = client.index('identities','sessions_v1',{
        sid:event[:sid],
        ts: event[:ts],
        ddata: event[:ddata]
      })
      time = event[:ts]
    elsif(event[:eid]==Generator::Session::SESSION_END_EVENT)
      duration = (((event[:ts] - time).to_i)/(1000.0*3600)).round(2)
      result = client.update('identities','sessions_v1',event[:sid],{ te: event[:ts], duration: duration })
      time = 0
    end
  end
end
