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

    step 'I play for the first time' do
      t1 = Time.now
      session = ::Generator::Session.new(user,device,t1)
      write_events(session,t1)
      q = search({q:"eid:GE_SESSION_START"})
      q.hits.total.should == 1
    end

    step 'I play multiple times on the same day' do
      t1 = Time.now
      session = ::Generator::Session.new(user,device,t1)
      write_events(session,t1)
      t2 = t1+10000
      session = ::Generator::Session.new(user,device,t2)
      write_events(session,t2)
      q = search({q:"eid:GE_SESSION_START"})
      q.hits.total.should == 2
    end

    step 'I play multiple times on multiple days' do
      t1 = Time.now
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

    def device
      @device ||= ::Generator::Device.new
    end

    def write_events(session,time)
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
