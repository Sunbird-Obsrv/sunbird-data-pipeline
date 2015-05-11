require 'pry'
require 'rspec'

Spinach.config[:failure_exceptions] << RSpec::Expectations::ExpectationNotMetError
Spinach::FeatureSteps.include RSpec::Matchers

ENV['ENV'] = 'test'
