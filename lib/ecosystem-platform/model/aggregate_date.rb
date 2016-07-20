require 'pry'
require 'date'
require 'hashie'
require 'yaml'
require 'fileutils'

class AggregateDate

  def initialize(data_dir, store_file_name, initial_aggregate_date, logger)
    @logger = logger
    @initial_aggregate_date = initial_aggregate_date
    @store_dir = File.join(ENV['HOME'], data_dir)
    @store_file_name = File.join(@store_dir, store_file_name)
  end

  def start_date
    return store.aggregate_date + 1 if store.aggregate_date.instance_of?(Date)
    @initial_aggregate_date
  end

  def end_date
    start_date()
  end

  def update(new_aggregate_date)
    @logger.info("UPDATING AGGREGATE DATE TO: #{new_aggregate_date}")
    store.aggregate_date = new_aggregate_date
    store_file = File.open(@store_file_name, "w")
    @logger.info("UPDATED STORE: #{YAML.dump(@store.to_hash)}")
    store_file.write(YAML.dump(@store.to_hash))
    store_file.flush
  end

  private
  def store
    unless @store.nil?
      return @store
    end
    @store = read_store_file()
    @logger.info("STORE: #{@store.to_hash}")
    return @store
  end

  def read_store_file
    @logger.info("LOAD STORE FILE: #{@store_dir}")
    unless Dir.exists?(@store_dir) && File.exists?(@store_file_name)
      @logger.info('NO PREVIOUS STORE FILE FOUND')
      @logger.info('CREATING NEW STORE FILE')
      FileUtils::mkdir_p @store_dir
      File.new(@store_file_name, 'w')
    end
    Hashie::Mash.new YAML.load_file(@store_file_name)
  end
end
