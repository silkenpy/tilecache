package ir.cht.tilecache.guavaCache

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.typesafe.config.Config
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.RoutingContext
import ir.cht.tilecache.tileSource.TileSource
import ir.cht.tilecache.util.TileMetrics
import java.util.concurrent.TimeUnit
import mu.KotlinLogging



class GuavaCache(val tileMetrics: TileMetrics, config: Config) {
    private val logger = KotlinLogging.logger {}
    val tileSource = TileSource(tileMetrics, config)
    val tileCache: LoadingCache<String?, ByteArray> = CacheBuilder.newBuilder()
            .maximumSize(9999999999999)
            .expireAfterWrite(24, TimeUnit.HOURS)
            .build(CacheLoader.from({ path -> tileSource.getFromSource(path) }))

    fun getFromCache(routingContext: RoutingContext) {

        tileMetrics.MarkTileRequest()

        val path = routingContext.request().path()
        routingContext.response()
                .setStatusCode(200)
                .end(Buffer.buffer(tileCache.get(path)))
        routingContext.clearUser()
    }

}