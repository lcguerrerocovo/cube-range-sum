package cubesum

import java.util.UUID

import com.twitter.app.Flag
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service}
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch._
import io.finch.circe.dropNullKeys._

/**
  * Created by luisguerrero
  */
object Server extends TwitterServer {

  val port: Flag[Int] = flag("port", 8081, "TCP port for HTTP server")

  def generateID = java.util.UUID.randomUUID.toString
  
  def postGrid: Endpoint[GridResource]
    = post("grids"
    :: jsonBody[String => GridResource].map(_(generateID))) { g: GridResource =>
      Grid.gridMap += g.id -> Grid(g.dimensionLength)
      Created(g)
  }

  def patchValues: Endpoint[MessageResource]
    = patch("grids" :: uuid :: "values" :: int :: int :: int :: long)
    { (id: UUID, x: Int, y: Int, z: Int, value: Long) =>

    Grid.gridMap.get(id.toString).get.update(x,y,z,value)
    Ok(MessageResource("grid " + id.toString + "updated succesfuly"))
  }

  def getSums: Endpoint[SumsResource]
    = get("grids" :: uuid :: "sums" :: int :: int :: int :: int :: int :: int)
    { (id: UUID, x: Int, y: Int, z: Int, x2: Int, y2: Int, z2: Int) =>

    val result = Grid.gridMap.get(id.toString).get.query(x,y,z,x2,y2,z2)
    Ok(SumsResource(result))
  }

  val api: Service[Request, Response] = (
    postGrid :+: patchValues :+: getSums
    ).handle({
    case e: Exception => BadRequest(e)
  }).toServiceAs[Application.Json]


  def main(): Unit = {
    log.info("Grid server running")

    val server = Http.server
      .withStatsReceiver(statsReceiver)
      .serve(s":${port()}", api)

    onExit { server.close() }

    Await.ready(adminHttpServer)
  }
}
