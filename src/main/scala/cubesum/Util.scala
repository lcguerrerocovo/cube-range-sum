package cubesum

import java.util.concurrent.ThreadLocalRandom

/**
  * Created by luisguerrero
  */
object Util {

  def rand(m : Int): Int = rand(1, m)

  def rand(n: Int, m: Int): Int
    = ThreadLocalRandom.current().nextInt(n, m)

  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block   
    val t1 = System.nanoTime()
    println("Running time = " + (t1 - t0) + " ns")
    result
  }
}
