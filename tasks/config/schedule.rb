# Use this file to easily define all of your cron jobs.
#
# It's helpful, but not entirely necessary to understand cron before proceeding.
# http://en.wikipedia.org/wiki/Cron

# Example:
#
set :output, {:error => 'error.log', :standard => 'cron.log'}

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
set :environment_variable, 'EP_LOG_DIR'
set :environment, ENV['EP_LOG_DIR']

# every 1.minute do
#   rake "scheduled:reverse_search"
# end
# every 1.minute do
#   rake "scheduled:set_ldata"
# end
# DISABLED because devices are generating signup events
# every 1.minute do
#   rake "scheduled:generate_signup"
# end
# every 1.minute do
#   rake "scheduled:oe_summarize"
# end
# every 1.minute do
#   rake "scheduled:child_data_populator"
# end

# every 1.hour do
#   rake "scheduled:decrypt_partner"
# end

# every 2.hour do
#   rake "scheduled:denormalize_partner"
# end
