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
import java.nio.CharBuffer;

public class CharFieldModel extends AbstractFieldModel<Character> {
  public CharFieldModel(String fieldName, int fieldNumber) {
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
    return elements * 2;
  }

  public static CharBuffer newArrayOfField(int size, MappedFileChannel mfc) {
    return acquireByteBuffer(mfc, sizeOf0(size)).asCharBuffer();
  }

  @Override
  public Class storeType() {
    return CharBuffer.class;
  }

  @Override
  public Character getAllocation(Object[] arrays, int index) {
    CharBuffer array = (CharBuffer) arrays[fieldNumber];
    return get(array, index);
  }

  public static char get(CharBuffer array, int index) {
    return array.get(index);
  }

  @Override
  public void setAllocation(Object[] arrays, int index, Character value) {
    CharBuffer array = (CharBuffer) arrays[fieldNumber];
    set(array, index, value);
  }

  public static void set(CharBuffer array, int index, char value) {
    array.put(index, value);
  }

  @Override
  public Class<Character> type() {
    return (Class) char.class;
  }

  @Override
  public String bcLFieldType() {
    return "C";
  }

  @Override
  public short equalsPreference() {
    return 16;
  }

  public static void write(ObjectOutput out, char ch) throws IOException {
    out.writeChar(ch);
  }

  public static char read(ObjectInput in) throws IOException {
    return in.readChar();
  }
}
