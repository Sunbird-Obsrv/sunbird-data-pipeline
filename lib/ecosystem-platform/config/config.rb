require 'hashie'

class Config
  def self.load
    config = YAML.load_file(File.join(__dir__, 'public.yml'))
    return Hashie::Mash.new config
  end
  def self.load_partner_spec(partner)
    config = YAML.load_file(File.join(__dir__, "#{partner}.yml"))
    return Hashie::Mash.new config
  end
end
