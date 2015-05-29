class Spinach::Features::TestHowDeviceCountWorks < Spinach::FeatureSteps
  include CommonSteps::ElasticsearchClient
  include CommonSteps::UserSimulation
  include CommonSteps::Device
  include CommonSteps::GamePlaySimulation

  THAT_TIME = Time.now

  step 'the device has never been used' do
    elastic_search_client
    elastic_search_client.indices.delete index: 'test*' rescue nil
    create_templates["acknowledged"].should == true
  end

  step 'a new device will be added' do
    total_device_count_is_one
  end

  step 'the device has already been used' do
    the_device_has_never_been_used
    i_play_on_this_device
  end

  step 'I play on this device on the same day' do
    t1 = THAT_TIME
    session = ::Generator::Session.new(user,device,t1)
    write_events(session,t1)
    q = search({q:"eid:GE_SESSION_START"})
    q.hits.total.should == 2
  end

  step 'the number of devices does not change' do
    total_device_count_is_one
  end

  step 'I play on this device from a different location' do
    t1 = THAT_TIME
    loc = "#{rand(12.0..20.0)},#{rand(74.0..78.0)}"
    session = ::Generator::Session.new(user,device(loc),t1)
    write_events(session,t1)
    q = search({q:"eid:GE_SESSION_START"})
    q.hits.total.should == 2
  end

  step 'I play on this device on a different day' do
    t1 = THAT_TIME+86400
    session = ::Generator::Session.new(user,device,t1)
    write_events(session,t1)
    q = search({q:"eid:GE_SESSION_START"})
    q.hits.total.should == 2
  end

  step 'I play on this device on a different day at a different location' do
    t1 = THAT_TIME+86400
    loc = "#{rand(12.0..20.0)},#{rand(74.0..78.0)}"
    session = ::Generator::Session.new(user,device(loc),t1)
    write_events(session,t1)
    q = search({q:"eid:GE_SESSION_START"})
    q.hits.total.should == 2
  end

  step 'all of us play on this device on different days and at different locations' do
    t1 = THAT_TIME+86400
    session = ::Generator::Session.new(user,device,t1)
    write_events(session,t1)
    t1 = THAT_TIME
    loc = "#{rand(12.0..20.0)},#{rand(74.0..78.0)}"
    session = ::Generator::Session.new(user,device,t1)
    write_events(session,t1)
    t1 = THAT_TIME+190000
    loc = "#{rand(12.0..20.0)},#{rand(74.0..78.0)}"
    session = ::Generator::Session.new(user,device,t1)
    write_events(session,t1)
    q = search({q:"eid:GE_SESSION_START"})
    q.hits.total.should == 4
  end

  step 'all of us including new children play on this device' do
    t1 = THAT_TIME+86400
    session = ::Generator::Session.new(user,device,t1)
    write_events(session,t1)
    t1 = THAT_TIME
    loc = "#{rand(12.0..20.0)},#{rand(74.0..78.0)}"
    session = ::Generator::Session.new(user,device,t1)
    write_events(session,t1)
    @user ||= ::Generator::User.new
    loc = "#{rand(12.0..20.0)},#{rand(74.0..78.0)}"
    session = ::Generator::Session.new(user,device,t1)
    write_events(session,t1)
    t1 = THAT_TIME+190000
    @user ||= ::Generator::User.new
    loc = "#{rand(12.0..20.0)},#{rand(74.0..78.0)}"
    session = ::Generator::Session.new(user,device,t1)
    write_events(session,t1)
    q = search({q:"eid:GE_SESSION_START"})
    q.hits.total.should == 5

  end

  step 'I play on another device' do
    i_play_on_this_device_on_the_same_day
    t1 = THAT_TIME
    loc = "#{rand(12.0..20.0)},#{rand(74.0..78.0)}"
    device = ::Generator::Device.new(loc)
    session = ::Generator::Session.new(user,device,t1)
    write_events(session,t1)
    q = search({q:"eid:GE_SESSION_START"})
    q.hits.total.should == 3
  end

  step 'the number of devices will change' do
    total_device_count_is_two
  end

end
