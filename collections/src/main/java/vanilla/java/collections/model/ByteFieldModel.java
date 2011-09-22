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
import java.nio.ByteBuffer;

public class ByteFieldModel extends AbstractFieldModel<Byte> {
  public ByteFieldModel(String fieldName, int fieldNumber) {
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
    return elements;
  }

  public static ByteBuffer newArrayOfField(int size, MappedFileChannel mfc) {
    return acquireByteBuffer(mfc, sizeOf0(size));
  }


  @Override
  public Class storeType() {
    return ByteBuffer.class;
  }


  @Override
  public Byte getAllocation(Object[] arrays, int index) {
    ByteBuffer array = (ByteBuffer) arrays[fieldNumber];
    return get(array, index);
  }

  public static byte get(ByteBuffer array, int index) {
    return array.get(index);
  }

  @Override
  public void setAllocation(Object[] arrays, int index, Byte value) {
    ByteBuffer array = (ByteBuffer) arrays[fieldNumber];
    set(array, index, value);
  }

  public static void set(ByteBuffer array, int index, byte value) {
    array.put(index, value);
  }

  @Override
  public Class<Byte> type() {
    return (Class) byte.class;
  }

  @Override
  public String bcLFieldType() {
    return "B";
  }

  @Override
  public short equalsPreference() {
    return 8;
  }

  public static void write(ObjectOutput out, byte b) throws IOException {
    out.writeByte(b);
  }

  public static byte read(ObjectInput in) throws IOException {
    return in.readByte();
  }
}
