package cubesum

import java.io.{ByteArrayInputStream, InputStream}

import org.scalatest.{FlatSpec, Matchers, PrivateMethodTester}

/**
  * Created by luisguerrero on 2/18/17.
  */
class ConsoleSpec extends FlatSpec with Matchers with PrivateMethodTester {

  it should "correctly print to console the sums based on string passed by system in" in {
    val input = """2
                  |4 5
                  |UPDATE 2 2 2 4
                  |QUERY 1 1 1 3 3 3
                  |UPDATE 1 1 1 23
                  |QUERY 2 2 2 4 4 4
                  |QUERY 1 1 1 3 3 3
                  |2 4
                  |UPDATE 2 2 2 1
                  |QUERY 1 1 1 1 1 1
                  |QUERY 1 1 1 2 2 2
                  |QUERY 2 2 2 2 2 2""".stripMargin

    val stream = new java.io.ByteArrayOutputStream()
    
    scala.Console.withOut(stream) {
      Console.consoleReader(new ByteArrayInputStream(input.getBytes))
    }

    val str = stream.toString.split("\n")
    str.filterNot(_.startsWith("Running")) mkString " " should equal (" 4 4 27 0 1 1")
  }
  
}
