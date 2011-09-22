package vanilla.java.collections.model;

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

import vanilla.java.collections.impl.MappedFileChannel;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.IntBuffer;

public class IntFieldModel extends AbstractFieldModel<Integer> {
  public IntFieldModel(String fieldName, int fieldNumber) {
    super(fieldName, fieldNumber);
  }

  @Override
  public Object arrayOfField(int size) {
    return newArrayOfField(size, null);
  }

  @Override
  public int sizeOf(int elements) {
    return sizeOf0(elements);
  }

  private static int sizeOf0(int elements) {
    return elements * 4;
  }

  public static IntBuffer newArrayOfField(int size, MappedFileChannel mfc) {
    return acquireByteBuffer(mfc, sizeOf0(size)).asIntBuffer();
  }

  @Override
  public Class storeType() {
    return IntBuffer.class;
  }

  @Override
  public Integer getAllocation(Object[] arrays, int index) {
    IntBuffer array = (IntBuffer) arrays[fieldNumber];
    return get(array, index);
  }

  public static int get(IntBuffer array, int index) {
    return array.get(index);
  }

  @Override
  public void setAllocation(Object[] arrays, int index, Integer value) {
    IntBuffer array = (IntBuffer) arrays[fieldNumber];
    set(array, index, value);
  }

  public static void set(IntBuffer array, int index, int value) {
    array.put(index, value);
  }

  @Override
  public Class<Integer> type() {
    return (Class) int.class;
  }

  @Override
  public String bcLFieldType() {
    return "I";
  }

  @Override
  public short equalsPreference() {
    return 32;
  }

  public static void write(ObjectOutput out, int i) throws IOException {
    out.writeInt(i);
  }

  public static int read(ObjectInput in) throws IOException {
    return in.readInt();
  }
}
