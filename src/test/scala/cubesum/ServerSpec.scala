package cubesum

import java.nio.charset.StandardCharsets

import io.circe.generic.auto._
import io.finch.circe._
import com.twitter.finagle.http.Status
import io.finch.{Application, Input}
import org.scalatest.prop.Checkers
import org.scalatest.{FlatSpec, Matchers}
import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by luisguerrero
  */
class ServerSpec extends FlatSpec with Matchers with Checkers  {

  import Server._

  behavior of "the postGrid endpoint"

  case class GridWithoutId(dimensionLength: Int)

  // generating random integer
  def genGridWithoutId: Gen[GridWithoutId] = GridWithoutId(Gen.choose(1, 10).sample.getOrElse(1))

  implicit def arbitraryGridWithoutId: Arbitrary[GridWithoutId] = Arbitrary(genGridWithoutId)

  it should "create a Grid" in {
    check { (gridWithout: GridWithoutId) =>
      val input = Input.post("/grids")
        .withBody[Application.Json](gridWithout, Some(StandardCharsets.UTF_8))
      val res = postGrid(input)
      res.awaitOutputUnsafe().map(_.status) === Some(Status.Created)
      res.awaitValueUnsafe().isDefined === true
      val Some(grid) = res.awaitValueUnsafe()
      grid.dimensionLength === gridWithout.dimensionLength
      Grid.gridMap.get(grid.id).isDefined === true
    }
  }

  it should "fail to create a Grid if dimension not in valid range" in {
      val invalidGrid = GridWithoutId(
        Gen.pick(1,Gen.choose(Int.MinValue, 0),Gen.choose(101,Int.MaxValue)).sample.get.head)
      val input = Input.post("/grids")
        .withBody[Application.Json](invalidGrid, Some(StandardCharsets.UTF_8))
      an [io.finch.Error.NotValid] should be thrownBy postGrid(input).awaitValueUnsafe()
  }

  val n = Gen.choose(1, 100).sample.getOrElse(1)
  val id = Gen.uuid.sample.getOrElse("2945bfc3-53a3-4980-bc2a-73f8f0412463").toString
  Grid.gridMap += id -> Grid(n)

  behavior of "the pathValues endpoint"

  it should "modify a value in the Grid identified by the UUID in the url parameter" in {

    val x = Gen.choose(1, n).sample.getOrElse(1)
    val y = Gen.choose(1, n).sample.getOrElse(1)
    val z = Gen.choose(1, n).sample.getOrElse(1)
    val value = Gen.choose(1L, 100L).sample.getOrElse(1L)

    val input = Input.patch(s"/grids/$id/values?x=$x&y=$y&z=$z&value=$value")

    patchValues(input).awaitValueUnsafe() shouldBe
      Some(MessageResource("grid " + id + "updated succesfuly"))
    Grid.gridMap.get(id).get.query(x,y,z,x,y,z) shouldBe value
  }

  it should "fail to update a grid's values if dimensions not in valid range" in {

    val x = Gen.choose(1, n).sample.getOrElse(1)
    val y = Gen.choose(1, n).sample.getOrElse(1)
    val z = Gen.choose(1, n).sample.getOrElse(1)

    val xError = Gen.choose(n+1, Int.MaxValue).sample.getOrElse(n+1)
    val yError = Gen.choose(n+1, Int.MaxValue).sample.getOrElse(n+1)
    val zError = Gen.choose(n+1, Int.MaxValue).sample.getOrElse(n+1)
    val value = Gen.choose(1L, 100L).sample.getOrElse(1L)

    var input = Input.patch(s"/grids/$id/values?x=$xError&y=$y&z=$z&value=$value")
    an [DimensionNotWithinRange] should be thrownBy patchValues(input).awaitValueUnsafe()

    input = Input.patch(s"/grids/$id/values?x=$x&y=$yError&z=$z&value=$value")
    an [DimensionNotWithinRange] should be thrownBy patchValues(input).awaitValueUnsafe()

    input = Input.patch(s"/grids/$id/values?x=$x&y=$y&z=$zError&value=$value")
    an [DimensionNotWithinRange] should be thrownBy patchValues(input).awaitValueUnsafe()
  }

  it should "fail to query sum of grid if dimensions not in valid range" in {

    val x = Gen.choose(1, n).sample.getOrElse(1)
    val y = Gen.choose(1, n).sample.getOrElse(1)
    val z = Gen.choose(1, n).sample.getOrElse(1)

    val x2 = Gen.choose(x, n).sample.getOrElse(1)
    val y2 = Gen.choose(y, n).sample.getOrElse(1)
    val z2 = Gen.choose(z, n).sample.getOrElse(1)

    val xError = Gen.choose(n+1, Int.MaxValue).sample.getOrElse(n+1)
    val yError = Gen.choose(n+1, Int.MaxValue).sample.getOrElse(n+1)
    val zError = Gen.choose(n+1, Int.MaxValue).sample.getOrElse(n+1)

    var input = Input.get(s"/grids/$id/sums?x=$xError&y=$y&z=$z&x2=$x2&y2=$y2&z2=$z2")
    an [DimensionNotWithinRange] should be thrownBy getSums(input).awaitValueUnsafe()

    input = Input.get(s"/grids/$id/sums?x=$x&y=$yError&z=$z&x2=$x2&y2=$y2&z2=$z2")
    an [DimensionNotWithinRange] should be thrownBy getSums(input).awaitValueUnsafe()

    input = Input.get(s"/grids/$id/sums?x=$x&y=$y&z=$zError&x2=$x2&y2=$y2&z2=$z2")
    an [DimensionNotWithinRange] should be thrownBy getSums(input).awaitValueUnsafe()

    input = Input.get(s"/grids/$id/sums?x=$x&y=$y&z=$z&x2=$xError&y2=$y2&z2=$z2")
    an [DimensionNotWithinRange] should be thrownBy getSums(input).awaitValueUnsafe()

    input = Input.get(s"/grids/$id/sums?x=$x&y=$y&z=$z&x2=$x2&y2=$yError&z2=$z2")
    an [DimensionNotWithinRange] should be thrownBy getSums(input).awaitValueUnsafe()

    input = Input.get(s"/grids/$id/sums?x=$x&y=$y&z=$z&x2=$x2&y2=$y2&z2=$zError")
    an [DimensionNotWithinRange] should be thrownBy getSums(input).awaitValueUnsafe()
  }

  behavior of "the getSums endpoint"

  it should "get sum of voxels of cube in between passed 3d coordinates" in {

      val x = Gen.choose(1, n).sample.getOrElse(1)
      val y = Gen.choose(1, n).sample.getOrElse(1)
      val z = Gen.choose(1, n).sample.getOrElse(1)

      val x2 = Gen.choose(1, x).sample.getOrElse(1)
      val y2 = Gen.choose(1, y).sample.getOrElse(1)
      val z2 = Gen.choose(1, z).sample.getOrElse(1)

      getSums(Input.get(s"/grids/$id/sums?x=$x&y=$y&z=$z&x2=$x2&y2=$y2&z2=$z2"))
        .awaitValueUnsafe() shouldBe
        Some(SumsResource(Grid.gridMap.get(id).get.query(x,y,z,x2,y2,z2)))
  }


}
