

class Spinach::Features::TestHowDeviceCountWorks < Spinach::FeatureSteps
  include CommonSteps::ElasticsearchClient
  include CommonSteps::UserSimulation

  THAT_TIME = Time.now

  step 'the device has never been used' do
    elastic_search_client
    elastic_search_client.indices.delete index: 'test*' rescue nil
    create_templates["acknowledged"].should == true
  end

  step 'I play for the first time on this device' do
    t1 = THAT_TIME
    session = ::Generator::Session.new(user,device,t1)
    write_events(session,t1)
    q = search({q:"eid:GE_SESSION_START"})
    q.hits.total.should == 1
  end

  step 'a new device will be added' do
    q = search({index:'test*',body: {
      "query"=> {
        "term"=>{
          "eid"=> "GE_SESSION_START"
        }
      },
      "size"=> 0,
      "aggs"=> {
        "unique_devices"=> {
          "cardinality"=> {
            "field"=> "did"
          }
        }
      }
    }})
    q.aggregations.unique_devices.value.should == 1
  end

end
