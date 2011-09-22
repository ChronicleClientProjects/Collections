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
import java.nio.LongBuffer;

public class LongFieldModel extends AbstractFieldModel<Long> {
  public LongFieldModel(String fieldName, int fieldNumber) {
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
    return elements * 8;
  }

  public static LongBuffer newArrayOfField(int size, MappedFileChannel mfc) {
    return acquireByteBuffer(mfc, sizeOf0(size)).asLongBuffer();
  }

  @Override
  public Class storeType() {
    return LongBuffer.class;
  }

  @Override
  public Long getAllocation(Object[] arrays, int index) {
    LongBuffer array = (LongBuffer) arrays[fieldNumber];
    return get(array, index);
  }

  public static long get(LongBuffer array, int index) {
    return array.get(index);
  }

  @Override
  public void setAllocation(Object[] arrays, int index, Long value) {
    LongBuffer array = (LongBuffer) arrays[fieldNumber];
    set(array, index, value);
  }

  public static void set(LongBuffer array, int index, long value) {
    array.put(index, value);
  }

  @Override
  public Class<Long> type() {
    return (Class) long.class;
  }

  @Override
  public int bcFieldSize() {
    return 2;
  }

  @Override
  public String bcLFieldType() {
    return "J";
  }

  @Override
  public BCType bcType() {
    return BCType.Long;
  }

  @Override
  public boolean isCallsNotEquals() {
    return true;
  }

  public static boolean equals(long l1, long l2) {
    return l1 == l2;
  }

  public static int hashCode(long l) {
    return (int) (l >>> 32 ^ l);
  }

  @Override
  public short equalsPreference() {
    return 30; // 64; lower due to the increased memory requirement
  }

  public static void write(ObjectOutput out, long l) throws IOException {
    out.writeLong(l);
  }

  public static long read(ObjectInput in) throws IOException {
    return in.readLong();
  }
}
