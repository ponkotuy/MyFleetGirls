package honor

import org.scalatest.FunSuite

class HonorsSuite extends FunSuite {
  test("Honor category ids are unique") {
    val categories = Honors.values.map(_.category)
    assert(categories.size === categories.distinct.size)
  }
}
