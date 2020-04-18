package org.sunbird.dp.core.util

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.sunbird.dp.core.job.BaseJobConfig

object FlinkUtil {

  def getExecutionContext(config: BaseJobConfig): StreamExecutionEnvironment = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.enableCheckpointing(config.checkpointingInterval)
    env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime)
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(config.restartAttempts, config.delayBetweenAttempts))
    env
  }
}