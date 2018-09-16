package ir.cht.tilecache.tileSource


import com.typesafe.config.Config
import ir.cht.tilecache.util.TileMetrics
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit


class TileSource(val tilemetrics: TileMetrics, val config: Config) {

    private val logger = KotlinLogging.logger {}

    val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    fun getFromSource(path: String?): ByteArray {
        tilemetrics.MarkTileNotInGuava(1)

        val masterSource = config.getString("tileCache.masterSource")
        val tileCacheIpPort = config.getString("tileCache.bindIp") + ":" + config.getInt("tileCache.bindPort").toString()
        val request = Request.Builder().url("""http://${masterSource}${path}""").build()

        val response = client.newCall(request).execute()

        logger.info { "${path} is got from ${masterSource}"  }

        if (path == "/planet_osm.json" || path == "/planet_osm.style.json") {
            var res = response.body()!!.string()
            res = res.replace(masterSource, tileCacheIpPort)
            return res.toByteArray()
        }

        return response.body()!!.bytes()
    }
}