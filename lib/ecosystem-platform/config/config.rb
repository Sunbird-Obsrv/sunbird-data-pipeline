require 'hashie'

class Config
  def self.load
    config = YAML.load_file(File.join(__dir__, 'public.yml'))
    return Hashie::Mash.new config
  end
  def self.load_partner_spec()
    config = YAML.load_file(File.join(__dir__, "private.yml"))
    return Hashie::Mash.new config
  end
end
