/**
  * Created by keshen on 2016/9/2.
  */
package tinyrtpl
object Test {
  def main(args: Array[String]) {
    val src = scala.io.Source.fromFile("data/test-list.html")
    val iter = src.getLines()
    while(iter.hasNext) {
      println(iter.next())
    }
  }
}
