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
import vanilla.java.collections.HugeMapBuilder;

import java.util.Arrays;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class HandTypeMapTest {
  @Test
  public void putGetSize() {
    HugeMapBuilder<HandTypesKey, HandTypes> dummy = new HugeMapBuilder<HandTypesKey, HandTypes>() {{
      allocationSize = 64 * 1024;
      setRemoveReturnsNull = true;
    }};

    HandTypesMap map = new HandTypesMap(dummy);
    HandTypesKeyImpl key = new HandTypesKeyImpl();
    HandTypesImpl value = new HandTypesImpl();
    long start = System.nanoTime();
    final int size = 5000000;
    for (int i = 0; i < size; i += 2) {
      put(map, key, value, i, false);
      put(map, key, value, i, true);
    }
    for (int i = 0; i < size; i += 2) {
      get(map, key, i, false);
      get(map, key, i, true);
    }
    for (Map.Entry<HandTypesKey, HandTypes> entry : map.entrySet()) {
      assertEquals(entry.getKey().getInt(), entry.getValue().getInt());
      assertEquals(entry.getKey().getBoolean(), entry.getValue().getBoolean());
    }
    long time = System.nanoTime() - start;
    System.out.printf("Took an average of %,d ns to write/read", time / size);
    System.out.println(Arrays.toString(map.sizes()));
    System.out.println(Arrays.toString(map.capacities()));
  }

  private static void put(HandTypesMap map, HandTypesKeyImpl key, HandTypesImpl value, int i, boolean flag) {
    final int k = i;
    key.setBoolean(flag);
    key.setInt(k);
    value.setBoolean(flag);
    value.setInt(k);
    int size = map.size();
    map.put(key, value);
    if (size + 1 != map.size()) {
      map.put(key, value);
      assertEquals(size + 1, map.size());
    }
    HandTypes ht = map.get(key);
    if (ht == null)
      ht = map.get(key);
    assertEquals(i, ht.getInt());
    if (flag != ht.getBoolean())
      assertEquals(flag, ht.getBoolean());
  }

  private static void get(HandTypesMap map, HandTypesKeyImpl key, int i, boolean flag) {
    final int k = i;
    key.setBoolean(flag);
    key.setInt(i);
    HandTypes ht = map.get(key);
    assertEquals(i, ht.getInt());
    if (flag != ht.getBoolean())
      assertEquals(flag, ht.getBoolean());
  }
}
