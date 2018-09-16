package ir.cht.tilecache

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.core.VertxOptions
import io.vertx.ext.web.RoutingContext
import ir.cht.tilecache.guavaCache.GuavaCache
import ir.cht.tilecache.util.TileMetrics
import com.typesafe.config.ConfigFactory


var vertx = Vertx.vertx(VertxOptions().setWorkerPoolSize(400))
val router = Router.router(vertx)

fun getMetrics(routingContext: RoutingContext, tilemetrics: TileMetrics) {

    val res = tilemetrics.getInfo()
    routingContext.response().setStatusCode(200).end(res.toString())
}

fun main(args: Array<String>) {

        val config = ConfigFactory.defaultApplication()
    val tilemetrice = TileMetrics()
    val tilecache = GuavaCache(tilemetrice, config)


    router.getWithRegex(".*").handler({ tilecache.getFromCache(it) })
    vertx.createHttpServer().requestHandler({ router.accept(it) }).listen(config.getInt("tileCache.bindPort"))

    val vertx2 = Vertx.vertx()
    val router2 = Router.router(vertx2)
    router2.get("/TileMetrics").handler({ getMetrics(it, tilemetrice) })
    vertx2.createHttpServer().requestHandler({ router2.accept(it) }).listen(config.getInt("tileCache.metricPort"))


    /*   vertx.createHttpClient().getNow(8080, "localhost", "/planet_osm/12/2632/1613.pbf",
               { response -> response.bodyHandler({ body -> println(body.toString()) }) })*/


}

