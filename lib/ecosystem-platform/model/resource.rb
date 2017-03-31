require 'pry'
require 'date'
require 'hashie'
require 'yaml'
require 'fileutils'

class Resource
  attr_accessor :partnerId, :name

  def initialize(partnerId, name)
    @partnerId = partnerId
    @name = name
  end

  def to_s
    "partnerId: #{@partnerId}, name: #{@name}"
  end
end
