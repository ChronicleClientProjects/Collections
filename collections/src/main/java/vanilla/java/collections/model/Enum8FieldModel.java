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

public class Enum8FieldModel<E extends Enum<E>> extends AbstractFieldModel<E> {
  private final Class<E> type;
  private final E[] values;

  public Enum8FieldModel(String fieldName, int fieldNumber, Class<E> type, E[] values) {
    super(fieldName, fieldNumber);
    this.type = type;
    this.values = values;
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
  public E getAllocation(Object[] arrays, int index) {
    ByteBuffer array = (ByteBuffer) arrays[fieldNumber];
    return get(array, index);
  }

  public E get(ByteBuffer array, int index) {
    final byte b = array.get(index);
    return b == -1 ? null : values[b & 0xFF];
  }

  @Override
  public void setAllocation(Object[] arrays, int index, E value) {
    ByteBuffer array = (ByteBuffer) arrays[fieldNumber];
    set(array, index, value);
  }

  // mv.visitMethodInsn(INVOKEVIRTUAL, collections + "model/Enum8FieldModel", "set", "(Ljava/nio/ByteBuffer;ILjava/lang/Enum;)V");
  public void set(ByteBuffer array, int index, E value) {
    array.put(index, value == null ? -1 : (byte) value.ordinal());
  }

  @Override
  public String bcLSetType() {
    return "Ljava/lang/Enum;";
  }

  @Override
  public String bcLStoredType() {
    return "B";
  }

  @Override
  public Class<E> type() {
    return type;
  }

  @Override
  public BCType bcType() {
    return BCType.Reference;
  }


  @Override
  public boolean virtualGetSet() {
    return true;
  }

  @Override
  public boolean isCallsNotEquals() {
    return true;
  }

  @UsedFromByteCode
  public static <T extends Enum<T>> boolean notEquals(T t1, T t2) {
    return t1 == null ? t2 != null : !t1.equals(t2);
  }

  @UsedFromByteCode
  public static int hashCode(Enum elementType) {
    return elementType == null ? Integer.MIN_VALUE : elementType.ordinal();
  }

  @Override
  public short equalsPreference() {
    return 7;
  }

  public static <E extends Enum<E>> E read(ObjectInput in, Class<E> eClass) throws IOException, ClassNotFoundException {
    return (E) in.readObject();
  }

  public static <E extends Enum<E>> void write(ObjectOutput out, Class<E> eClass, E e) throws IOException {
    out.writeObject(e);
  }
}
