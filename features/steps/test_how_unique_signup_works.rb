class Spinach::Features::TestHowUniqueSignupWorks < Spinach::FeatureSteps
  include CommonSteps::ElasticsearchClient
  include CommonSteps::UserSimulation

  step 'I have never played' do
    elastic_search_client
    elastic_search_client.indices.delete index: 'test*' rescue nil
    create_templates["acknowledged"].should == true
  end

  step 'I should see a unique signup' do
    q = search({q:"eid:GE_SIGNUP"})
    q.hits.total.should == 0
    Processors::SignupProcessor.perform('test*')
    refresh_index
    q = search({index:'test*',q:"eid:GE_SIGNUP"})
    q.hits.total.should == 1
  end

end
