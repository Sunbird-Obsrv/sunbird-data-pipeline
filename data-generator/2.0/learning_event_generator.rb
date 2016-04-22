
require 'securerandom'
require 'json'

module LearningEventGenerator
  class Learning

    attr_accessor :uid, :event, :event_json
    
    def initialize
      @uid = SecureRandom.uuid
    end

    def event
      start_time = DateTime.now.strftime('%Q').to_i
      {
        "eid": "ME_SESSION_SUMMARY",
        "ets": start_time,
        "syncts": start_time,
        "ver": "2.0",
        "mid": SecureRandom.uuid,
        "uid": @uid,
        "context": {
            "pdata": {
                "id": "AnalyticsDataPipeline",
                "model": "LearnerSessionSummary",
                "ver": "1.0"
            },
            "granularity": "SESSION",
            "date_range": {
                "from": DateTime.now.strftime('%Q').to_i,
                "to": DateTime.now.strftime('%Q').to_i
            }
        },
        "dimensions": {
            "did": SecureRandom.uuid,
            "gdata": {
                "id": "org.ekstep.delta",
                "ver": "1.0"
            },
            "loc": "9.5410983,78.5960427"
        },
        "edata": {
            "eks": {
                "start_time": start_time,
                "noOfLevelTransitions": -1,
                "levels": [],
                "activitySummary": [],
                "noOfAttempts": 1,
                "screenSummary": [],
                "end_time": start_time,
                "timeSpent": 0,
                "interactEventsPerMin": 0,
                "mimeType": "application/vnd.android.package-archive",
                "syncDate": start_time,
                "contentType": "Game",
                "timeDiff": 0,
                "eventsSummary": [
                    {
                        "id": "OE_START",
                        "count": 1
                    }
                ],
                "currentLevel": {},
                "noOfInteractEvents": 0,
                "interruptTime": 0,
                "itemResponses": [],
                "telemetryVersion": "1.0"
            }
        }
      }
    end

    def event_json
      JSON.generate(event)
    end
  end
end