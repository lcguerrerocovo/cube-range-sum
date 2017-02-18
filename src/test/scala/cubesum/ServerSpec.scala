package cubesum

import java.nio.charset.StandardCharsets

import io.circe.generic.auto._
import io.finch._
import io.finch.circe._
import com.twitter.finagle.http.Status
import com.twitter.io.Buf
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
  def genGridWithoutId: Gen[GridWithoutId] = GridWithoutId(Gen.choose(1, 100).sample.getOrElse(1))

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

  behavior of "the pathValues endpoint"

  it should "modify a value in the Grid identified by the UUID in the url parameter" in {

    val id = Gen.uuid.sample.getOrElse("2945bfc3-53a3-4980-bc2a-73f8f0412463").toString
    val n = Gen.choose(1, 100).sample.getOrElse(1)

    val x = Gen.choose(1, n).sample.getOrElse(1)
    val y = Gen.choose(1, n).sample.getOrElse(1)
    val z = Gen.choose(1, n).sample.getOrElse(1)
    val value = Gen.choose(1L, 100L).sample.getOrElse(1L)

    Grid.gridMap += id -> Grid(n)
    val input = Input.patch(s"/grids/$id/values/$x/$y/$z/$value")

    patchValues(input).awaitValueUnsafe() shouldBe
      Some(MessageResource("grid " + id + "updated succesfuly"))
    Grid.gridMap.get(id).get.query(x,y,z,x,y,z) shouldBe value
  }


}
