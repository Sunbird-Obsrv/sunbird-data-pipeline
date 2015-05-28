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

  step 'I play for the first time on a new device' do
    t1 = THAT_TIME
    loc = FIRST_LOCATION
    session = ::Generator::Session.new(user,device(loc),t1,::Generator::Session::OLD_MODE)
    write_events(session,t1)
    q = search({q:"eid:GE_SESSION_START"})
    q.hits.total.should == 1
  end

  step 'my events should be tagged with my current location' do
    run_old_jobs
    q = search({q:"eid:GE_SESSION_START"})
    location_string(q.hits.hits.first).should == FIRST_LOCALITY
  end

  step 'I play for the first time on an existing device from an existing location' do
    pending 'step not implemented'
  end

  step 'I play for the first time on an existing device from a new location' do
    pending 'step not implemented'
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
