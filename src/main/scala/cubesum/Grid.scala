package cubesum

/**
  * Created by luisguerrero
  */

object Grid {
  var gridMap = collection.Map.empty[String,Grid]
  def apply(dimensionLength: Int) = new Grid(dimensionLength)
}

class Grid(dimensionLength: Int) {

  type VecGrid = Vector[Vector[Vector[Long]]]

  private var grid: VecGrid = Vector.fill (dimensionLength, dimensionLength, dimensionLength) (0l)

  // cache for meoization of recursive function calls
  private var cache = scala.collection.concurrent.TrieMap.empty[(Int,Int,Int), Long]

  sumOfVolume(dimensionLength-1,dimensionLength-1,dimensionLength-1)

  // 3d implementation of summed area table
  // described here: https://en.wikipedia.org/wiki/Summed_area_table
  private lazy val sumOfVolume: ((Int,Int,Int)) => Long = {
    case (x,y,z) if (x < 0 || y < 0 || z < 0) => 0L
    case (x,y,z) => {
      val (a,b,c,d,e,f,h) = (getVolume((x-1,y-1,z-1),sumOfVolume), getVolume((x,y,z-1),sumOfVolume),
        getVolume((x,y-1,z),sumOfVolume), getVolume((x-1,y,z),sumOfVolume), getVolume((x-1,y-1,z),sumOfVolume),
        getVolume((x,y-1,z-1),sumOfVolume), getVolume((x-1,y,z-1),sumOfVolume))
      (grid(x)(y)(z) + a + b + c + d - e - f - h)
    }
  }

  private def getVolume(pos: (Int,Int,Int), f: ((Int,Int,Int)) => Long)
    = cache getOrElseUpdate (pos, f(pos))
  
  private def updateVector(x: Int, y: Int, z: Int, value: Long): Unit = {
      val prev = grid(x)(y)(z)
      grid = grid.updated(x, grid(x).updated(y, grid(x)(y).updated(z, value)))
      for {
        i <- x until dimensionLength
        j <- y until dimensionLength
        k <- z until dimensionLength
        sum = getVolume((i, j, k), sumOfVolume) - prev + value
      } yield cache.put((i, j, k), sum)
  }

  private def queryVector(x: Int, y: Int, z: Int, x2: Int, y2: Int, z2: Int): Long = {
      (sumOfVolume((x2, y2, z2)) - sumOfVolume((x2, y-1, z2)) - sumOfVolume((x-1, y2, z2)) -
        sumOfVolume((x2, y2, z-1)) + sumOfVolume((x-1, y-1, z2)) + sumOfVolume((x2, y-1, z-1)) +
        sumOfVolume((x-1, y2, z-1)) - sumOfVolume((x-1, y-1, z-1)))
  }

  private def validateInputRange(x: Int, y: Int, z: Int): Unit = {
    require(x > 0 && x <= dimensionLength && y > 0 && y <= dimensionLength &&
      z > 0 && z <= dimensionLength, throw new DimensionNotWithinRange("dimension not within range"))
  }
  
  // public methods to expose vector of vectors of vectors grid as non-zero indexed, starting at (1,1,1)
  def update(x: Int, y: Int, z: Int, value: Long)
    = {
    validateInputRange(x,y,z)
    updateVector(x-1,y-1,z-1,value)
  }

  def query(x: Int, y: Int, z: Int, x2: Int, y2: Int, z2: Int)
    = {
    validateInputRange(x,y,z)
    validateInputRange(x2,y2,z2)
    queryVector(x-1,y-1,z-1,x2-1,y2-1,z2-1)
  }



  def dim = dimensionLength
}
