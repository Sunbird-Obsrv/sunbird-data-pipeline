require_relative '../../data-async-processors/geo_reverse_search2.rb'
require_relative '../../data-async-processors/set_ldata2.rb'
require_relative '../../data-async-processors/signup_processor.rb'
require_relative '../../data-async-processors/signup_geo_tagger.rb'


class Spinach::Features::TestHowSignupLocationTaggingWorks < Spinach::FeatureSteps
  include CommonSteps::ElasticsearchClient
  include CommonSteps::UserSimulation
  include CommonSteps::Utility

  step 'my signup should not be tagged to any location' do
    run_jobs
    sessions = search({q:"eid:GE_SIGNUP"})
    ldata = sessions.hits.hits.map {|session| session._source.edata.eks.ldata }
    ldata.compact.should be_empty
  end

  step 'my signup should be tagged to my device location' do
    run_jobs
    my_device_should_also_be_tagged_to_that_location
    # devices = search({body:{query:{constant_score:{filter:{and:[{term:{_type:"devices_v1"}},{term:{did:device.id}}]}}}}})
    signup = search({q:"eid:GE_SIGNUP"})
    location_string(signup.hits.hits.first).should == THAT_LOCALITY
  end

  def run_jobs
    Processors::SignupProcessor.perform('test*')
    Processors::ReverseSearch2.perform('test*')
    Processors::SetLdata2.perform('test*')
    Processors::SignupGeoTagger.perform('test*')
    refresh_index
  end

end
