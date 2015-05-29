require_relative '../../data-async-processors/geo_reverse_search.rb'
require_relative '../../data-async-processors/set_ldata.rb'
require_relative '../../data-async-processors/signup_processor.rb'
require_relative '../../data-async-processors/signup_geo_tagger.rb'

class Spinach::Features::TestHowLocationTaggingOfEventsWork < Spinach::FeatureSteps
  include CommonSteps::ElasticsearchClient
  include CommonSteps::UserSimulation
  include CommonSteps::Utility

  THAT_TIME = Time.now
  FIRST_LOCATION = "12.912422, 77.645512"
  FIRST_STATE = "karnataka"
  FIRST_DISTRICT = "bangalore urban"
  FIRST_LOCALITY = "Bengaluru"

  SECOND_LOCATION = "26.222125, 78.191586"
  SECOND_STATE = "Madhya Pradesh"
  SECOND_DISTRICT = "Gwalior"
  SECOND_LOCALITY = "Gwalior"

  step 'I play for the first time on a new device' do
    t1 = THAT_TIME
    loc = FIRST_LOCATION
    @user = ::Generator::User.new
    session = ::Generator::Session.new(user,device(loc),t1,::Generator::Session::OLD_MODE)
    write_events(session,t1)
    q = search({q:"eid:GE_SESSION_START"})
    q.hits.total.should == 1
  end

  step 'my events should be tagged with my existing location' do
    run_old_jobs
    event_type = "GE_SESSION_START"
    q = get_specific_type_of_event_for_user(event_type, @user)
    q.hits.total.should == 1
    location_string(q.hits.hits.first).should == FIRST_LOCALITY
    event_type = "GE_LAUNCH_GAME"
    q = get_specific_type_of_event_for_user(event_type, @user)
    q.hits.total.should == 1
    location_string(q.hits.hits.first).should == FIRST_LOCALITY
    event_type = "GE_SIGNUP"
    q = get_all_signup_events_for_user(event_type, @user)
    q.hits.total.should == 1
    location_string(q.hits.hits.first).should == FIRST_LOCALITY
  end

  step 'my events should be tagged with my new location' do
    run_old_jobs
    event_type = "GE_SESSION_START"
    q = get_specific_type_of_event_for_user(event_type, @user)
    q.hits.total.should == 1
    location_string(q.hits.hits.first).should == SECOND_LOCALITY
    event_type = "GE_LAUNCH_GAME"
    q = get_specific_type_of_event_for_user(event_type, @user)
    q.hits.total.should == 1
    location_string(q.hits.hits.first).should == SECOND_LOCALITY
    event_type = "GE_SIGNUP"
    q = get_all_signup_events_for_user(event_type, @user)
    q.hits.total.should == 1
    location_string(q.hits.hits.first).should == SECOND_LOCALITY
  end

  step 'I play for the first time on an existing device from an existing location' do
    i_play_for_the_first_time_on_a_new_device
    run_old_jobs
    t1 = THAT_TIME
    loc = FIRST_LOCATION
    @user = ::Generator::User.new
    session = ::Generator::Session.new(user,device(loc),t1,::Generator::Session::OLD_MODE)
    write_events(session,t1)
    q = search({q:"eid:GE_SESSION_START"})
    q.hits.total.should == 2
  end

  step 'I play for the first time on an existing device from a new location' do
    i_play_for_the_first_time_on_a_new_device
    run_old_jobs
    t1 = THAT_TIME
    loc = SECOND_LOCATION
    @user = ::Generator::User.new
    session = ::Generator::Session.new(user,device(loc),t1,::Generator::Session::OLD_MODE)
    write_events(session,t1)
    q = search({q:"eid:GE_SESSION_START"})
    q.hits.total.should == 2
  end

  step 'I have already played' do
    pending 'step not implemented'
  end

  step 'I play on an existing device from an existing location' do
    pending 'step not implemented'
  end

  step 'my new events should be tagged with my current location' do
    pending 'step not implemented'
  end

  step 'I play on a new device from a new location' do
    pending 'step not implemented'
  end

  step 'I play on an existing device from a new location' do
    pending 'step not implemented'
  end

  step 'we have never played' do
    pending 'step not implemented'
  end

  step 'we play on an existing device from a single location' do
    pending 'step not implemented'
  end

  step 'our events should be tagged with our current location' do
    pending 'step not implemented'
  end

  step 'we have already played' do
    pending 'step not implemented'
  end

  step 'we play on an existing device from different locations' do
    pending 'step not implemented'
  end

  step 'our events should be tagged with our respective locations' do
    pending 'step not implemented'
  end
end

def run_old_jobs
  Processors::SignupProcessor.perform('test*')
  Processors::ReverseSearch.perform('test*')
  Processors::SetLdata.perform('test*')
  Processors::SignupGeoTagger.perform('test*')
  refresh_index
end
