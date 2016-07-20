require 'hashie'

class Config
  def self.load
    config = YAML.load_file(File.join(__dir__, 'config.yml'))
    return Hashie::Mash.new config
  end
end
