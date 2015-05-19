require 'json'
require 'net/http'
require 'digest/sha1'
require 'csv'
require 'set'
require 'fileutils'
require 'logger'

module Uploaders
  module Numeracy
    class Screener
      API_USER="ekstep"
      API_PASS="--password-here--"
      API_URL= "https://api.ekstep.org/v1/telemetry"

      LOG_FILE_PATH="/tmp/Sprint1_LiteracyScreener_LOGS/**/logs/*.json"

      def self.process_file_data(file_path)
        file = File.read(file_path)
        data = JSON.parse(file)
        wrapper={
          id: "ekstep.telemetry",
          ver: "1.0",
          ts: Time.new.strftime("%Y-%m-%dT%H:%M:%S")+"+0530",
          params: {
            did: "ff305d54-85b4-341b-da2f-eb6b9e5460fa", 
            key: "13405d54-85b4-341b-da2f-eb6b9e546fff", 
            msgid: Digest::SHA1.hexdigest(file_path).upcase
          },
          events: []
        }
        data["events"].each do |e|
          child_id = find_child_id(e["sid"])
          if child_id == ""
            @@unknown << e["sid"]
            break
          else
            e["uid"] = Digest::SHA1.hexdigest(child_id.downcase).upcase
            e["sid"] = Digest::SHA1.hexdigest(e["sid"]).upcase
            if e["eid"]=="OE_ASSESS"
              e["edata"]["eks"]["score"]= e["edata"]["eks"]["pass"].downcase == "yes" ? "1" : "0"
              e["edata"]["eks"]["maxscore"]= "1"
            end
            unless e["ts"].end_with?("+0530")
              e["ts"] = e["ts"]+"+0530"
            end
            wrapper[:events] << e
          end
        end
        if wrapper[:events].count == 0 
          return nil
        else
          wrapper
        end
      end

      def self.post_data(data)
        uri = URI.parse(API_URL)
        http = Net::HTTP.new(uri.host, uri.port)
        if API_URL.start_with? "https"
          http.use_ssl = true
        end
        req = Net::HTTP::Post.new(uri.path, initheader = {'Content-Type' =>'application/json'})
        req.body = JSON.generate(data)
        req.basic_auth API_USER, API_PASS
        res = http.request(req)
        res
      end

      def self.find_child_id(sid)
        @@mapping_data.each do |row|
          return row["Child ID"] if row["Sid"] == sid
        end
        return ""
      end

      def self.read_processed_files
        @@processed = Set.new
        f = CSV.parse(File.read('./processed.csv'), headers: true) 
        f.each do |row|
          @@processed << row["filename"]
        end
      end

      def self.upload
        file = File.expand_path("./logs/logfile.log", File.dirname(__FILE__))
        @logger = Logger.new(file)
        error_count = 0
        skipped = 0
        read_processed_files
        @@unknown=Set.new
        @@mapping_data = CSV.parse(File.read('mapping.csv'), headers: true) 
        log_files = Dir.glob(LOG_FILE_PATH)
        @logger.info "Found #{log_files.size} log file(s)"
        @logger.info "Uploading...."
        begin
          log_files.each do |f|
            filename = File.basename(f) 
            if  @@processed.include? filename
              skipped += 1
              next
            end
            data = process_file_data(f)
            if data == nil
              error_count+=1
              @logger.error "Unable to upload file #{f}"
            else
              res = post_data(data)
              if res.code != "200" 
                error_count+=1
                @logger.error "file #{f} . Response code: #{res.code}"
              else
                @@processed << filename
              end
            end
          end
        rescue Exception=>e
          @logger.error e
        end
        @logger.info "---------------"
        @logger.info "Missing sids"
        @logger.info @@unknown.to_a
        puts @@unknown.to_a
        @logger.info "Finished Upload"
        @logger.info "---------------"
        @logger.info " Total     : #{log_files.size}"
        @logger.info " Processed : #{log_files.size - skipped}"
        @logger.info " Success   : #{log_files.size - (skipped + error_count)}"
        @logger.info " Failed    : #{error_count}"
        update_processed_file
      end

      def self.update_processed_file
        CSV.open('./processed.csv', "wb") do |csv|
          csv << ["filename"]
          @@processed.to_a.each do |name|
            csv << [name]
          end
        end 
      end
    end
  end
end

