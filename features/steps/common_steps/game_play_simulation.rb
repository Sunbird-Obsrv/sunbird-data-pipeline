module CommonSteps
  module GamePlaySimulation
    include Spinach::DSL

    THAT_TIME = Time.now

    step 'I play on this device' do
      t1 = THAT_TIME
      session = ::Generator::Session.new(user,device,t1)
      write_events(session,t1)
      q = search({q:"eid:GE_SESSION_START"})
      q.hits.total.should == 1
    end
  end
end
