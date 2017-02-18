package cubesum

/**
  * Created by luisguerrero
  */

// resources for http server
case class GridResource(id: String, dimensionLength: Int)

case class MessageResource(msg: String)

case class SumsResource(result: Long)

case class GridNotFound(id: String) extends Exception {
  override def getMessage: String = "Grid with id:" + id +  " not found"
}

case class DimensionNotWithinRange(id: String) extends Exception {
  override def getMessage: String = "Grid with id:" + id +  " not found"
}

