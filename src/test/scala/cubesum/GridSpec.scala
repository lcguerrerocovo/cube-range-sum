package cubesum

import java.util.concurrent.ThreadLocalRandom

import org.scalacheck.Gen
import org.scalatest.{FlatSpec, Matchers, PrivateMethodTester}

/**
  * Created by luisguerrero
  */
class GridSpec extends FlatSpec with Matchers with PrivateMethodTester {

  import Util._

  it should "change output of sum when grid is updated" in {
    val grid = Grid(10)

    grid.query(1,1,1,10,10,10) should equal (0L)
    
    grid.update(1,1,1,1)

    grid.query(1,1,1,10,10,10) should equal (1L)

    grid.update(1,2,2,1)
    grid.update(2,1,1,1)
    grid.update(1,3,1,1)
    grid.update(1,1,3,1)

    grid.query(1,1,1,10,10,10) should equal (5L)
  }

  it should "sum should stay the same in areas not affected by update" in {
    val grid = Grid(20)

    grid.query(10,10,10,16,16,16) should equal (0L)
    grid.query(1,1,1,6,6,6) should equal (0L)

    grid.update(12,12,12,1)
    grid.update(12,13,12,1)
    grid.update(13,12,12,1)
    grid.update(12,12,13,1)
    grid.update(13,13,13,1)

    grid.query(10,10,10,16,16,16) should equal (5L)
    grid.query(1,1,1,6,6,6) should equal (0L)
  }

  it should "respond with correct sums for queries (hackerrank1 test data)" in {
    val grid = Grid(4)

    grid.update(2,2,2,4)
    grid.query(1,1,1,3,3,3) should equal (4L)

    grid.update(1,1,1,23)
    grid.query(2,2,2,4,4,4) should equal (4L)
    grid.query(1,1,1,3,3,3) should equal (27L)
  }

  it should "respond with correct sums for queries (hackerrank2 test data)" in {
    val grid = Grid(2)

    grid.update(2,2,2,1)
    grid.query(1,1,1,1,1,1) should equal (0L)
    grid.query(1,1,1,2,2,2) should equal (1L)
    grid.query(2,2,2,2,2,2) should equal (1L)
  }

  it should "respond with same sum as brute force method" in {
    val n = Gen.choose(1, 50).sample.get
    val grid = Grid(n)

    for (i <- 1 to 25; x = rand(n); y = rand(n); z = rand(n)
    ) yield grid.update(x,y,z,rand(100))

    val bruteForce = PrivateMethod[Long]('bruteForceSum)

    time {grid.query(1,1,1,n,n,n)} should
      equal (time {grid invokePrivate bruteForce((n-1,n-1,n-1))})
  }
}
