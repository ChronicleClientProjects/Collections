package vanilla.java.collections.hand;

/*
 * Copyright 2011 Peter Lawrey
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import org.junit.Test;
import vanilla.java.collections.HugeArrayBuilder;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class HandTypeArrayListTest {
  @Test
  public void testHTAL() throws IOException {
    HugeArrayBuilder<HandTypes> hab = new HugeArrayBuilder<HandTypes>() {
      {
        allocationSize = 4096;
      }
    };
    HandTypesArrayList htal = new HandTypesArrayList(hab);
    htal.setSize(5);
    for (int i = 0, htalSize = htal.size(); i < htalSize; i++) {
      HandTypes handTypes = htal.get(i);
      handTypes.setString("" + (i + 1));
    }
    assertEquals(6, htal.stringEnumerated16FieldModel.map().size());
    assertEquals("[null, 1, 2, 3, 4, 5]", htal.stringEnumerated16FieldModel.list().toString());

    htal.get(1).setString("1");
    htal.get(3).setString("3");
    assertEquals("5", htal.get(4).getString());
    htal.compact();
    assertEquals("5", htal.get(4).getString());
    assertEquals(4, htal.stringEnumerated16FieldModel.map().size());
    assertEquals("[null, 1, null, 3, null, 5]", htal.stringEnumerated16FieldModel.list().toString());
    assertEquals("5", htal.get(4).getString());
    htal.get(1).setString("6");
    assertEquals("5", htal.get(4).getString());
    htal.get(4).setString("7");
    htal.compact();

    assertEquals(5, htal.stringEnumerated16FieldModel.map().size());
    assertEquals("[null, 1, 6, 3, 7, null]", htal.stringEnumerated16FieldModel.list().toString());
  }
}
