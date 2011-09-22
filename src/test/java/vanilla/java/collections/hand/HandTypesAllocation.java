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

import vanilla.java.collections.ObjectTypes;
import vanilla.java.collections.api.HugeAllocation;
import vanilla.java.collections.impl.MappedFileChannel;
import vanilla.java.collections.model.*;

import java.nio.*;
import java.util.Arrays;

import static vanilla.java.collections.impl.GenerateHugeArrays.clean;

public class HandTypesAllocation implements HugeAllocation {
  IntBuffer m_boolean;
  IntBuffer m_boolean2;
  ByteBuffer m_byte;
  ByteBuffer m_byte2;
  CharBuffer m_char;
  ShortBuffer m_short;
  IntBuffer m_int;
  FloatBuffer m_float;
  LongBuffer m_long;
  DoubleBuffer m_double;
  ByteBuffer m_elementType;
  CharBuffer m_string;
  ObjectTypes.A[] m_a;

  public HandTypesAllocation(int allocationSize, MappedFileChannel mfc) {
    m_boolean = BooleanFieldModel.newArrayOfField(allocationSize, mfc);
    m_boolean2 = Boolean2FieldModel.newArrayOfField(allocationSize, mfc);
    m_byte = ByteFieldModel.newArrayOfField(allocationSize, mfc);
    m_byte2 = Byte2FieldModel.newArrayOfField(allocationSize, mfc);
    m_char = CharFieldModel.newArrayOfField(allocationSize, mfc);
    m_short = ShortFieldModel.newArrayOfField(allocationSize, mfc);
    m_int = IntFieldModel.newArrayOfField(allocationSize, mfc);
    m_float = FloatFieldModel.newArrayOfField(allocationSize, mfc);
    m_long = LongFieldModel.newArrayOfField(allocationSize, mfc);
    m_double = DoubleFieldModel.newArrayOfField(allocationSize, mfc);
    m_elementType = Enum8FieldModel.newArrayOfField(allocationSize, mfc);
    m_string = Enumerated16FieldModel.newArrayOfField(allocationSize, mfc);
    m_a = new ObjectTypes.A[allocationSize];
  }

  public void clear() {
    Arrays.fill(m_a, null);
  }

  @Override
  public void destroy() {
    clean((Buffer) m_boolean);
    clean((Buffer) m_boolean2);
    final Object m = m_byte;
    clean((Buffer) m);
    // etc

  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    destroy();
  }


}
