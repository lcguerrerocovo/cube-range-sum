package cubesum

import java.io.InputStream

import Util._
/**
  * Created by luisguerrero
  */

// runnable to test hackerrank console/string input

object Console extends App {

  consoleReader(System.in)

  def consoleReader(input: InputStream): Unit = {
    val sc = new java.util.Scanner (input)
    var t = sc.nextInt()
    println()

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
            println(
              time { grid.query(ints(0), ints(1), ints(2), ints(3), ints(4), ints(5)) })
          }}})}
  }
}


