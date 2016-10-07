require 'pry'
require 'fileutils'

require_relative '../utils/ep_logging.rb'

module EcosystemPlatform
  module Jobs
    class FileArchiver

      PROGNAME = ENV['PROGNAME'] || 'file_archiver.jobs.ep'

      include EcosystemPlatform::Utils::EPLogging

      def self.perform(src_dir,dest_dir)
        begin
          logger.start_task
          files_to_archive = find_files_to_archive(src_dir)

          unless files_to_archive.size > 0
            logger.info("NO FILES TO ARCHIVE, EXITING")
            logger.end_task
            return
          end

          self.archive_files(files_to_archive,src_dir,dest_dir)
          logger.end_task
        rescue => e
          logger.error(e, {backtrace: e.backtrace[0..4]})
          logger.end_task
        end
      end

      def self.find_files_to_archive(src_dir)
        logger.info("FINDING FILES TO ARCHIVE")
        current_time = Time.now.utc
        # Find all files matching pattern /var/log/partners/**/*.log
        all_log_files = Dir.glob(File.join(src_dir, "**/*.log"))
        # E.g. current_hour_log_regex_pattern: /var/log/partners/partner-name/2016/01/31/14/data-exhaust-2016-01-31-14.log
        current_hour_log_regex_pattern = /#{Regexp.escape(src_dir)+"[0-9a-z]+" + Regexp.escape("/#{current_time.year}/#{current_time.strftime("%m")}/#{current_time.strftime("%d")}/#{current_time.strftime("%H")}/data-exhaust-#{current_time.year}-#{current_time.strftime("%m")}-#{current_time.strftime("%d")}-#{current_time.strftime("%H")}.log")}/
        return all_log_files.reject { |f| current_hour_log_regex_pattern.match(f) }
      end

      def self.archive_files(files_to_archive,src_dir,dest_dir)
        logger.info("FOUND #{files_to_archive.size} FILES TO ARCHIVE")
        files_to_archive.each do |f|
          logger.info("ARCHIVING #{f} to #{f.gsub(/#{src_dir}/, dest_dir)}")
          FileUtils.mkdir_p(File.dirname(f.gsub(/#{src_dir}/, dest_dir)))
          FileUtils.mv(f, f.gsub(/#{src_dir}/, dest_dir))
        end
      end

    end
  end
end
