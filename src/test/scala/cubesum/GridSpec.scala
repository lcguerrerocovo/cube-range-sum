package cubesum

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by luisguerrero
  */
class GridSpec extends FlatSpec with Matchers {
  

 it should "change output of sum when grid is updated" in {
    val grid = Grid(10)
    
    grid.sumOfVolume(9,9,9) should equal (0L)
    
    grid.update(0,0,0,1)

    grid.sumOfVolume(9,9,9) should equal (1L)

    grid.update(1,2,2,1)
    grid.update(2,1,1,1)
    grid.update(1,3,1,1)
    grid.update(1,1,3,1)

    grid.sumOfVolume(9,9,9) should equal (5L)
  }

  it should "sum should stay the same in areas not affected by update" in {
    val grid = Grid(20)

    grid.sumOfVolume(15,15,15) should equal (0L)
    grid.sumOfVolume(5,5,5) should equal (0L)

    grid.update(12,12,12,1)
    grid.update(12,13,12,1)
    grid.update(13,12,12,1)
    grid.update(12,12,13,1)
    grid.update(13,13,13,1)

    grid.sumOfVolume(15,15,15) should equal (5L)
    grid.sumOfVolume(5,5,5) should equal (0L)
  }
}
