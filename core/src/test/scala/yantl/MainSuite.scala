/*
 * Copyright 2025 arturaz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yantl

import yantl.util.YantlSuite

class MainSuite extends YantlSuite {

  test("validated: success") {
    assertRight(Age.make(10), Age.makeUnsafe(10))
  }

  test("validated: failure") {
    assertLeft(
      Age.make(-1),
      Vector(ValidatorRule.SmallerThan(minimum = 0, actual = -1))
    )
  }

  test("unvalidated: success") {
    assertEquals(Name("Arturas"), Name.makeUnsafe("Arturas"))
  }
}
