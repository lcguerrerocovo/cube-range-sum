package cubesum

/**
  * Created by luisguerrero
  */

// runnable to test hackerrank console/string input

object Main extends App {
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

  val sc = new java.util.Scanner (System.in)
  var t = sc.nextInt()
  for (i <- 1 to t) {
    val n = sc.nextInt()
    val m = sc.nextInt()
    sc.nextLine()
    val lst = List.fill(m)("")

    val grid = new Grid(n)

    lst.foreach( s => {
      val split = sc.nextLine().split(" ")
      split(0) match {
        case "UPDATE" => {
          val ints = (split drop 1 dropRight 1).map(_.toInt)
          grid.update(ints(0),ints(1),ints(2),split.last.toLong)
        }
        case "QUERY" => {
          val ints = (split drop 1).map(_.toInt)
          println(grid.query(ints(0), ints(1), ints(2), ints(3), ints(4), ints(5)))
        }}})
  }

}
