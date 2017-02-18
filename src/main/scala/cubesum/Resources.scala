package cubesum

/**
  * Created by luisguerrero
  */

// resources for http server
case class GridResource(id: String, dimensionLength: Int)

case class MessageResource(msg: String)

case class SumsResource(result: Long)

