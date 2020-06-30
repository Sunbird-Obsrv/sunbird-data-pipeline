package org.sunbird.fixture

object EventFixture {

  val SAMPLE_EVENT_1: String =
    """{"actor":{"type":"User","id":"bc3be7ae-ad2b-4dee-ac4c-220c7db146b2"},"eid":"INTERACT",
      |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
      |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":1.579143065071E12,
      |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
      |"pdata":{"id":"prod.sunbird.portal","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272",
      |"did":"758e054a400f20f7677f2def76427dc13ad1f837"},
      |"mid":"321a6f0c-10c6-4cdc-9893-207bb64fea50","type":"events","object":{"id":"do_9574","type":"content",
      |"version":"","rollup":{}}}""".stripMargin


  val SAMPLE_EVENT_2: String =
    """
      |{"ver":"3.0","eid":"SHARE","ets":1577278681178,"actor":{"type":"User","id":"7c3ea1bb-4da1-48d0-9cc0-c4f150554149"},"context":{"channel":"505c7c48ac6dc1edc9b08f21db5a571d","pdata":{"id":"prod.sunbird.desktop","pid":"sunbird.app","ver":"2.3.162"},"env":"app","sid":"82e41d87-e33f-4269-aeae-d56394985599","did":"1b17c32bad61eb9e33df281eecc727590d739b2b"},"edata":{"dir":"In","type":"File","items":[{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_312785709424099328114191","type":"CONTENT","ver":"1","params":[{"transfers":0,"size":21084308}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31277435209002188818711","type":"CONTENT","ver":"18","params":[{"transfers":12,"size":"123"}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31278794857559654411554","type":"TextBook","ver":"1"}]},"object":{"id":"do_312528116260749312248818","type":"TextBook","version":"10","rollup":{}},"mid":"02ba33e5-15fe-4ec5-b32.1084308E760-3d03429fae84","syncts":1577278682630,"@timestamp":"2019-12-25T12:58:02.630Z","type":"events"}
      |""".stripMargin

  val SAMPLE_EVENT_3: String =
    """
      |{"ver":"3.0","eid":"SHARE", "actor":{"type":"User","id":"7c3ea1bb-4da1-48d0-9cc0-c4f150554149"},"context":{"channel":"505c7c48ac6dc1edc9b08f21db5a571d","pdata":{"id":"prod.sunbird.desktop","pid":"sunbird.app","ver":"2.3.162"},"env":"app","sid":"82e41d87-e33f-4269-aeae-d56394985599","did":"1b17c32bad61eb9e33df281eecc727590d739b2b"},"edata":{"dir":"In","type":"File","items":[{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_312785709424099328114191","type":"CONTENT","ver":"1","params":[{"transfers":0,"size":21084308}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31277435209002188818711","type":"CONTENT","ver":"18","params":[{"transfers":12,"size":"123"}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31278794857559654411554","type":"TextBook","ver":"1"}]},"object":{"id":"do_312528116260749312248818","type":"TextBook","version":"10","rollup":{}},"mid":"02ba33e5-15fe-4ec5-b32.1084308E760-3d03429fae84","type":"events"}
      |""".stripMargin

  val customConfig =
    """
      |kafka {
      |  map.input.topic = "local.telemetry.map.input"
      |  map.output.topic = "local.telemetry.map.output"
      |  event.input.topic = "local.telemetry.event.input"
      |  event.output.topic = "local.telemetry.event.output"
      |  string.input.topic = "local.telemetry.string.input"
      |  string.output.topic = "local.telemetry.string.output"
      |  broker-servers = "localhost:9093"
      |  zookeeper = "localhost:2183"
      |  groupId = "pipeline-preprocessor-group"
      |  auto.offset.reset = "earliest"
      |  producer {
      |     max-request-size = 102400
      |  }
      |}
      |
      |task {
      |  parallelism = 2
      |  consumer.parallelism = 1
      |  checkpointing.interval = 60000
      |  metrics.window.size = 100 # 3 min
      |  restart-strategy.attempts = 1 # retry once
      |  restart-strategy.delay = 1000 # in milli-seconds
      |}
      |
      |redisdb.connection.timeout = 30000
      |
      |redis {
      |  host = 127.0.0.1
      |  port = 6341
      |  database {
      |    duplicationstore.id = 12
      |    key.expiry.seconds = 3600
      |  }
      |}
      |
      |redis-meta {
      |  host = 127.0.0.1
      |  port = 6341
      |}
      |
      |postgress {
      |    host = localhost
      |    port = 5432
      |    maxConnection = 2
      |    user = "postgres"
      |    password = "postgres"
      |}
      |job {
      |  enable.distributed.checkpointing = true
      |  statebackend {
      |    blob {
      |      storage {
      |        account = "blob.storage.account"
      |        container = "telemetry-container"
      |        checkpointing.dir = "flink-jobs"
      |      }
      |    }
      |    base.url = "hdfs://testpath"
      |  }
      |}
    """.stripMargin


}
