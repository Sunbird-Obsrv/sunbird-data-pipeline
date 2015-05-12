require_relative '../../data-async-processors/signup_processor.rb'

class Spinach::Features::TestHowUniqueSignupWorks < Spinach::FeatureSteps
  include CommonSteps::ElasticsearchClient
  include CommonSteps::UserSimulation

  step 'I should see a unique signup' do
    q = search({q:"eid:GE_SIGNUP"})
    q.hits.total.should == 0
    Processors::SignupProcessor.perform('test*')
    refresh_index
    q = search({index:'test*',q:"eid:GE_SIGNUP"})
    q.hits.total.should == 1
  end

end
