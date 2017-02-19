package cubesum

import java.util.UUID

import com.twitter.app.Flag
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service}
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch.{param, _}
import io.finch.circe.dropNullKeys._

/**
  * Created by luisguerrero
  */
object Server extends TwitterServer {

  val port: Flag[Int] = flag("port", 8081, "TCP port for HTTP server")

  def generateID = java.util.UUID.randomUUID.toString

  def postGrid: Endpoint[GridResource]
    = post("grids"
    :: jsonBody[String => GridResource].map(_(generateID))
      .should("have dimensionLength less than or equal to 100 and greater than 0")
        {x => x.dimensionLength <= 100 && x.dimensionLength > 0})
    { g: GridResource =>

      Grid.gridMap += g.id -> Grid(g.dimensionLength)
      Created(g)
  }

  def patchValues: Endpoint[MessageResource]
    = patch("grids" :: uuid :: "values" :: param("x").as[Int]
      :: param("y").as[Int] :: param("z").as[Int]
      :: param("value").as[Long].should("be a value in accepted range [-1*10^9,1*10^9]")
      {v => v >= -Math.pow(10,9) && v <= Math.pow(10,9)})
      {(id: UUID, x: Int, y: Int, z: Int, value: Long) =>

    Grid.gridMap.get(id.toString)
      .getOrElse(throw new GridNotFound(id.toString)).update(x,y,z,value)
    Ok(MessageResource("grid " + id.toString + " updated successfully"))
  }

  def getSums: Endpoint[SumsResource]
    = get("grids" :: uuid :: "sums" :: param("x").as[Int]
      :: param("y").as[Int] :: param("z").as[Int]
      :: param("x2").as[Int] :: param("y2").as[Int]
      :: param("z2").as[Int])
    { (id: UUID, x: Int, y: Int, z: Int, x2: Int, y2: Int, z2: Int) =>

    val result = Grid.gridMap.get(id.toString)
      .getOrElse(throw new GridNotFound(id.toString)).query(x,y,z,x2,y2,z2)
    Ok(SumsResource(result))
  } 

  val api: Service[Request, Response] = (
    postGrid :+: patchValues :+: getSums
    ).handle({
    case e: DimensionNotWithinRange => BadRequest(e)
    case e: GridNotFound => NotFound(e)
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
