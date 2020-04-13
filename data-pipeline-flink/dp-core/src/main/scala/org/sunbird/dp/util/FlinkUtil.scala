package org.sunbird.dp.util

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.sunbird.dp.core.BaseJobConfig
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.api.common.restartstrategy.RestartStrategies

object FlinkUtil {

  def getExecutionContext(config: BaseJobConfig): StreamExecutionEnvironment = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.enableCheckpointing(config.checkpointingInterval)
    env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime)
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(config.restartAttempts, config.delayBetweenAttempts))
    env
  }
}