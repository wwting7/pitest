/*
 * Copyright 2010 Henry Coles
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.pitest.testapi;

import java.util.List;

public interface TestUnitFinder {

  /**
   * Discover test units for a class. Most implementations can ignore the listener
   * parameter, it is only required if tests are executed as part of discovery
   * (eg JUnit 5).
   */
  List<TestUnit> findTestUnits(Class<?> clazz, TestUnitExecutionListener listener);

}
