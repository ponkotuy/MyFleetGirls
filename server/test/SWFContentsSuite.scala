import org.scalatest.FunSuite
import tool.SWFContents

/**
 * Created by yosuke on 15/01/06.
 */
class SWFContentsSuite extends FunSuite {
  test("swfextract parse result") {
    import tool.SWFType._
    val testCase = """ [-i] 6 Shapes: ID(s) 2, 4, 6, 8, 10, 12
                     | [-j] 6 JPEGs: ID(s) 1, 3, 5, 7, 9, 11
                     | [-f] 1 Frame: ID(s) 0""".stripMargin
    val results = Set(
      SWFContents(0, Frame),
      SWFContents(1, Jpeg),
      SWFContents(2, Shape),
      SWFContents(3, Jpeg),
      SWFContents(4, Shape),
      SWFContents(5, Jpeg),
      SWFContents(6, Shape),
      SWFContents(7, Jpeg),
      SWFContents(8, Shape),
      SWFContents(9, Jpeg),
      SWFContents(10, Shape),
      SWFContents(11, Jpeg),
      SWFContents(12, Shape)
    )
    val xs = SWFContents.parse(testCase).toSet
    assert(xs === results)
  }
}
