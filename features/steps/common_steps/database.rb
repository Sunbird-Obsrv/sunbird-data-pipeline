require 'mysql2'
require "pry"

module CommonSteps
  module Database
    include Spinach::DSL

    def connect
      @db_client ||= Mysql2::Client.new(:host => "localhost", :username => "root", :database => "ecosystem")

    end

    def get_all_users
      connect
      @db_client.query("SELECT * FROM users")
    end

    def get_user_count
      connect
      @db_client.query("SELECT COUNT(*) FROM users")
    end

    def remove_all_users
      connect
      @db_client.query("DELETE FROM users")
    end

    def get_users(user_mobile)
      connect
      @db_client.query("SELECT * FROM users where mobile = '#{user_mobile}'")
    end

  end
end
