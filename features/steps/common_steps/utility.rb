module CommonSteps
  module Utility
    include Spinach::DSL

    def location_string(o)
      o.extend Hashie::Extensions::DeepFind
      ldata = o.deep_find 'ldata'
      "#{ldata.locality}" rescue nil
    end

  end
end
