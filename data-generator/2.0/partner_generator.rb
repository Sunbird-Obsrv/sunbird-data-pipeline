
require 'digest/sha1'
require 'securerandom'
require 'pry'

API_ROOT = "http://#{ENV['API_HOST']||'localhost:8080'}"
API_USER="#{ENV['API_USER']||'ekstep'}"
API_PASS="#{ENV['API_PASS']||'i_dont_know'}"
TELEMETRY_SYNC_URL="#{API_ROOT}/v1/telemetry"

module PartnerGenerator
  class Partner

    attr_accessor :uid
    def initialize
      @sid = SecureRandom.uuid
      @uid = SecureRandom.uuid
    end

    def create_events(deviceId, partnerId)
      start_time = Time.now
      [
        {
          "tags": [],
          "uid": @uid,
          "sid": @sid,
          "ts": start_time.strftime('%Y-%m-%dT%H:%M:%S%z'),
          "edata": {
            "eks": {
              "loc": ""
            }
          },
          "did": deviceId,
          "ver": "1.0",
          "type": "events",
          "eid": "GE_SESSION_START",
          "@version": "1",
          "gdata": {
            "id": "genieservice.android",
            "ver": "1.0.166-sandbox"
          },
          "uuid": "e4e56b41-3d54-4877-a00e-039f370b00c282",
          "key": "d1541eb1-9457-430f-90eb-11f2c1a9a420"
        },
        {
          "eid": "GE_CREATE_USER",
          "ts": (start_time + 1).strftime('%Y-%m-%dT%H:%M:%S%z'),
          "ver": "1.0",
          "gdata": {
            "id": "genie.android",
            "ver": "1.0"
          },
          "sid": @sid,
          "uid": @uid,
          "did": deviceId,
          "edata": {
            "eks": {
              "uid": @uid,
            }
          }
        },
        {
          "tags": [
            {
              "partnerid": partnerId
            }
          ],
          "uid": "",
          "sid": "",
          "ts": (start_time + 2).strftime('%Y-%m-%dT%H:%M:%S%z'),
          "edata": {
            "eks": {
              "partnerid": partnerId,
              "sid": "e4d62fb0-2e99-49cb-9c12-5cec39379ad2"
            }
          },
          "did": deviceId,
          "ver": "1.0",
          "type": "events",
          "eid": "GE_START_PARTNER_SESSION",
          "@version": "1",
          "gdata": {
            "id": "genieservice.android",
            "ver": "1.0.166-sandbox"
          },
          "uuid": "e4e56b41-3d54-4877-a00e-039f370b00c285",
          "key": ""
        },
        {
          "tags": [
            {
            "survey_codes": "aser001"
            },
            {
              "activation_keys": "ptm001"
            },
            {
              "partnerid": partnerId
            }
          ],
          "uid": @uid,
          "sid": @sid,
          "ts": (start_time + 3).strftime('%Y-%m-%dT%H:%M:%S%z'),
          "edata": {
            "eks": {
              "extype": "",
              "id": "Next button pressed",
              "type": "TOUCH",
              "uri": ""
            }
          },
          "did": deviceId,
          "ver": "1.0",
          "type": "events",
          "eid": "OE_INTERACT",
          "@version": "1",
          "gdata": {
            "id": "org.ekstep.aser.lite",
            "ver": "5.6.7"
          },
          "uuid": "e4e56b41-3d54-4877-a00e-039f370b00c286",
          "key": "d1541eb1-9457-430f-90eb-11f2c1a9a420"
        },
        {
          "eid": "OE_ASSESS",
          "uid":  @uid,
          "sid": @sid,
          "ts": (start_time + 4).strftime('%Y-%m-%dT%H:%M:%S%z'),
          "edata": {
            "eks": {
                "atmpts": 1,
                "exlength": 0,
                "exres": [],
                "failedatmpts": 0,
                "length": 0,
                "maxscore": 1,
                "mc": [
                    "M92"
                ],
                "pass": "No",
                "qid": "q_2_sub",
                "qlevel": "",
                "qtype": "SUB",
                "res": [],
                "score": 0,
                "subj": "NUM",
                "uri": ""
            },
            "ext": {
                "Question": ""
            }
          },
          "did":  deviceId,
          "ver": "1.0",
          "gdata": {
            "id": "genie.android",
            "ver": "1.0"
          },
        },
        {
          "tags": [
            {
              "partnerid": partnerId
            }
          ],
          "uid": "",
          "sid": "",
          "ts": (start_time + 5).strftime('%Y-%m-%dT%H:%M:%S%z'),
          "edata": {
            "eks": {
              "data": "random string",
              "iv": "iv",
              "key": "private key",
              "partnerid": partnerId,
              "publickeyid": "id"
            }
          },
          "did": deviceId,
          "ver": "1.0",
          "type": "events",
          "eid": "GE_PARTNER_DATA",
          "@version": "1",
          "gdata": {
            "id": "genieservice.android",
            "ver": "1.0.166-sandbox"
          },
          "uuid": "e4e56b41-3d54-4877-a00e-039f370b00c291",
          "key": ""
        },
        {
          "tags": [
            {
              "partnerid": partnerId
            }
          ],
          "uid": "",
          "sid": "",
          "ts": (start_time + 6).strftime('%Y-%m-%dT%H:%M:%S%z'),
          "edata": {
            "eks": {
              "length": 55961,
              "partnerid": partnerId,
              "sid": "e4d62fb0-2e99-49cb-9c12-5cec39379ad2"
            }
          },
          "did": deviceId,
          "ver": "1.0",
          "type": "events",
          "eid": "GE_STOP_PARTNER_SESSION",
          "@version": "1",
          "gdata": {
            "id": "genieservice.android",
            "ver": "1.0.166-sandbox"
          },
          "uuid": "e4e56b41-3d54-4877-a00e-039f370b00c2105",
          "key": ""
        },
        {
          "tags": [],
          "uid": @uid,
          "sid": @sid,
          "ts": (start_time + 7).strftime('%Y-%m-%dT%H:%M:%S%z'),
          "edata": {
            "eks": {
              "length": 73
            }
          },
          "did": deviceId,
          "ver": "1.0",
          "type": "events",
          "eid": "GE_SESSION_END",
          "@version": "1",
          "gdata": {
            "id": "genieservice.android",
            "ver": "1.0.166-sandbox"
          },
          "uuid": "e4e56b41-3d54-4877-a00e-039f370b00c2108",
          "key": "d1541eb1-9457-430f-90eb-11f2c1a9a420"
        }
      ]
    end
  end
end