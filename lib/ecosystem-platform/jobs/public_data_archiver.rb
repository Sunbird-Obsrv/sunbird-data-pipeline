require 'pry'
require 'fileutils'

require_relative '../utils/ep_logging.rb'

module EcosystemPlatform
  module Jobs
    class PublicDataArchiver

      PROGNAME = 'public_data_archiver.jobs.ep'
      SRC_DIR = ENV['SRC_DIR'] || "/var/log/public_data"
      DEST_DIR = ENV['DEST_DIR'] || "/var/log/public_data_archive"

      include EcosystemPlatform::Utils::EPLogging

      def self.perform
        begin
          logger.start_task
          files_to_archive = self.find_files_to_archive

          unless files_to_archive.size > 0
            logger.info("NO FILES TO ARCHIVE, EXITING")
            logger.end_task
            return
          end

          self.archive_files(files_to_archive)
          logger.end_task
        rescue => e
          logger.error(e, {backtrace: e.backtrace[0..4]})
          logger.end_task
        end
      end

      def self.find_files_to_archive
        logger.info("FINDING FILES TO ARCHIVE")
        current_time = Time.now.utc
        # Find all files matching pattern /var/log/public_data/**/*.log
        all_log_files = Dir.glob(File.join(SRC_DIR, "**/*.log"))
        # E.g. current_hour_log_regex_pattern: /var/log/public_data/prefix/2016/01/31/14/data-exhaust-14.log
        current_hour_log_regex_pattern = /#{Regexp.escape(SRC_DIR)+"/[a-z]+" + Regexp.escape("/#{current_time.year}/#{current_time.strftime("%m")}/#{current_time.strftime("%d")}/#{current_time.strftime("%H")}/data-exhaust-#{current_time.strftime("%H")}.log")}/
        return all_log_files.reject { |f| f =~ current_hour_log_regex_pattern }
      end

      def self.archive_files(files_to_archive)
        logger.info("FOUND #{files_to_archive.size} FILES TO ARCHIVE")
        files_to_archive.each do |f|
          logger.info("ARCHIVING #{f} to #{f.gsub(/#{SRC_DIR}/, DEST_DIR)}")
          FileUtils.mkdir_p(File.dirname(f.gsub(/#{SRC_DIR}/, DEST_DIR)))
          FileUtils.mv(f, f.gsub(/#{SRC_DIR}/, DEST_DIR))
        end
      end

    end
  end
end
