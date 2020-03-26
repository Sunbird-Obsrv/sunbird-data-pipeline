package org.ekstep.dp.fixture

object EventFixture {

  val EVENT_WITH_MID: String =
    """{"actor":{"type":"User","id":"bc3be7ae-ad2b-4dee-ac4c-220c7db146b2"},"eid":"INTERACT",
      |"edata":{"type":"OTHER","subtype":"sheen-animation-ended","id":"library","pageid":"library","extra":{"pos":[]}},
      |"ver":"3.0","syncts":1.579564974098E12,"@timestamp":"2020-01-21T00:02:54.098Z","ets":1.579143065071E12,
      |"context":{"cdata":[],"env":"home","channel":"505c7c48ac6dc1edc9b08f21db5a571d",
      |"pdata":{"id":"sunbird.app","pid":"sunbird.app","ver":"2.3.144"},"sid":"df936f82-e982-41ec-8412-70d414458272",
      |"did":"758e054a400f20f7677f2def76427dc13ad1f837"},"flags":{"dd_processed":true},
      |"mid":"321a6f0c-10c6-4cdc-9893-207bb64fea50","type":"events","object":{"id":"","type":"",
      |"version":"","rollup":{}}}""".stripMargin

}
