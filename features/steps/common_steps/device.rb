module CommonSteps
  module Device
    include Spinach::DSL

    step 'total device count is one' do
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

    step 'total device count is two' do
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
      q.aggregations.unique_devices.value.should == 2
    end

  end
end
