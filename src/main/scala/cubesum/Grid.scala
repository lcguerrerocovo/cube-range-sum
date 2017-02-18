package cubesum

/**
  * Created by luisguerrero
  */

class Grid(dimensionLength: Int) {

  type VecGrid = Vector[Vector[Vector[Long]]]

  var grid: VecGrid = Vector.fill (dimensionLength, dimensionLength, dimensionLength) (0l)

  var cache = scala.collection.concurrent.TrieMap.empty[(Int,Int,Int), Long]

  sumOfVolume(dimensionLength-1,dimensionLength-1,dimensionLength-1)

  lazy val sumOfVolume: ((Int,Int,Int)) => Long = {
    case (x,y,z) if (x < 0 || y < 0 || z < 0) => 0L
    case (x,y,z) => {
      val (a,b,c,d,e,f,h) = (getVolume((x-1,y-1,z-1),sumOfVolume), getVolume((x,y,z-1),sumOfVolume),
        getVolume((x,y-1,z),sumOfVolume), getVolume((x-1,y,z),sumOfVolume), getVolume((x-1,y-1,z),sumOfVolume),
        getVolume((x,y-1,z-1),sumOfVolume), getVolume((x-1,y,z-1),sumOfVolume))
      (grid(x)(y)(z) + a + b + c + d - e - f - h)
    }
  }

  def getVolume(pos: (Int,Int,Int), f: ((Int,Int,Int)) => Long)
  = cache getOrElseUpdate (pos, f(pos))
  
  def update(x: Int, y: Int, z: Int, value: Long): Unit = {
    val prev = grid(x)(y)(z)
    grid = grid.updated(x, grid(x).updated(y, grid(x)(y).updated(z, value)))
    for {
      i <- x until dimensionLength
      j <- y until dimensionLength
      k <- z until dimensionLength
      sum = getVolume((i,j,k),sumOfVolume) - prev + value
    } yield cache.put((i,j,k),sum)
  }
}


object Grid {
 def apply(dimensionLength: Int) = new Grid(dimensionLength)
}
