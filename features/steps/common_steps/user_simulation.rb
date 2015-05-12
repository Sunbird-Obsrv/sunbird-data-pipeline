require_relative '../../../data-generator/session_generator.rb'

module CommonSteps
  module UserSimulation
    include Spinach::DSL

    GE_JSON_VER = '1.0'
    GE_GENIE_START = 'GE_GENIE_START'
    GE_GAME_ID = 'genie.android'
    GE_GAME_VER = '1.0'

    ANDROID_VERS = ['Android 5.0','Android 4.4','Android 4.1','Android 4.0']
    MAKES = ['Make A','Make B','Make C','Make D','Make E']

    THAT_LOCATION = "12.9715987,77.5945627"
    THAT_LOCALITY = "Bengaluru"
    THAT_TIME = Time.now

    TIME_STEPS = 10000

    step 'I have never played' do
      elastic_search_client
      elastic_search_client.indices.delete index: 'test*' rescue nil
      create_templates["acknowledged"].should == true
    end

    step 'I play for the first time' do
      t1 = THAT_TIME
      session = ::Generator::Session.new(user,device,t1)
      write_events(session,t1)
      q = search({q:"eid:GE_SESSION_START"})
      q.hits.total.should == 1
    end

    step 'I play from a location' do
      t1  = THAT_TIME
      loc = THAT_LOCATION
      session = ::Generator::Session.new(user,device(loc),t1)
      write_events(session,t1)
      q = search({q:"eid:GE_SESSION_START"})
      q.hits.total.should == 1
    end

    step 'I play from another location' do
      @count||=1
      t  = THAT_TIME+@count*TIME_STEPS
      loc = "#{rand(12.0..20.0)},#{rand(74.0..78.0)}"
      session = ::Generator::Session.new(user,device(loc),t)
      write_events(session,t)
      @count+=1
      q = search({q:"eid:GE_SESSION_START"})
      q.hits.total.should == @count
    end

    step 'I play from different locations with defective GPS reading' do
      10.times do |i|
        choice = rand 0..1
        case(choice)
        when 0
          loc = ""
        when 1
          loc = "#{rand(12.0..20.0)},#{rand(74.0..78.0)}"
        end
        t  = THAT_TIME+i*TIME_STEPS
        session = ::Generator::Session.new(user,device(loc),t)
        write_events(session,t)
      end
      q = search({q:"eid:GE_SESSION_START"})
      q.hits.total.should == 10
    end

    step 'I play multiple times on the same day' do
      t1 = THAT_TIME
      session = ::Generator::Session.new(user,device,t1)
      write_events(session,t1)
      t2 = t1+10000
      session = ::Generator::Session.new(user,device,t2)
      write_events(session,t2)
      q = search({q:"eid:GE_SESSION_START"})
      q.hits.total.should == 2
    end

    step 'I play multiple times on multiple days' do
      t1 = THAT_TIME
      session = ::Generator::Session.new(user,device,t1)
      write_events(session,t1)
      t2 = t1+10000
      session = ::Generator::Session.new(user,device,t2) #same day
      write_events(session,t2)
      t3 = t1+86400
      session = ::Generator::Session.new(user,device,t3) #next day
      write_events(session,t3)
      t4 = t1+86400*2+10000
      session = ::Generator::Session.new(user,device,t4) #after the next day
      write_events(session,t4)
      q = search({q:"eid:GE_SESSION_START"})
      q.hits.total.should == 4
    end

    private

    def user
      @user ||= ::Generator::User.new
    end

    def device(loc=nil)
      if(@device)
        @device.loc=loc
      else
        @device = ::Generator::Device.new(loc)
      end
        @device
    end

    def log_session(session)
      @sessions||={}
      @sessions[session.sid]=session
    end

    def get_session(sid)
      @sessions[sid]
    end

    def write_events(session,time)
      log_session(session)
      index_name = "test-#{time.strftime('%Y-%m-%d')}"
      session.events.each do |event|
        r=elastic_search_client.index({
          index: index_name,
          type: 'events_v1',
          refresh: true,
          body: event
        })
      end
    end

  end
end
