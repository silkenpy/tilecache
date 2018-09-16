package ir.cht.tilecache.util

import com.codahale.metrics.Meter
import com.codahale.metrics.MetricRegistry


data class MeterPojo(val count: Long,
                     val rate: Double,
                     val oneMinuteRate: Double,
                     val fiveMinuteRate: Double,
                     val fifteenMinuteRate: Double)


class TileMetrics {

    val metricRegistry = MetricRegistry()

    val TileRequest = metricRegistry.meter("TileRequest")
    val TileInGuava = metricRegistry.meter("TileInGuava")
    val TileNotInGuava = metricRegistry.meter("TileNotInGuava")



    fun MarkTileRequest(l: Long = 1) = TileRequest.mark(l)
    fun MarkTileInGuava(l: Long = 1) = TileInGuava.mark(l)
    fun MarkTileNotInGuava(l: Long = 1) = TileNotInGuava.mark(l)


    private fun sortMetersByCount(meters: Map<String, Meter>) =
            meters.toList().sortedBy { it.second.count }.reversed().map { Pair(it.first, it.second.toPojo()) }.toMap()

    private fun Meter.toPojo() = MeterPojo(count, meanRate, oneMinuteRate, fiveMinuteRate, fifteenMinuteRate)

    fun getInfo() = sortMetersByCount(metricRegistry.meters)


}