# Use this file to easily define all of your cron jobs.
#
# It's helpful, but not entirely necessary to understand cron before proceeding.
# http://en.wikipedia.org/wiki/Cron

# Example:
#
file = File.expand_path("../logs/cron.log", File.dirname(__FILE__))
set :output, file
#
# every 2.hours do
#   command "/usr/bin/some_great_command"
#   runner "MyModel.some_method"
#   rake "some:great:rake:task"
# end
#
# every 4.days do
#   runner "AnotherModel.prune_old_records"
# end

# Learn more: http://github.com/javan/whenever

every 1.minute do
  rake "scheduled:reverse_search"
end
every 1.minute do
  rake "scheduled:set_ldata"
end
every 1.minute do
  rake "scheduled:generate_signup"
end
