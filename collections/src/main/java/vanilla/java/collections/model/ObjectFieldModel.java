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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;

public class ObjectFieldModel<T> extends AbstractFieldModel<T> {
  private final Class<T> type;

  public ObjectFieldModel(String fieldName, int fieldNumber, Class<T> type) {
    super(fieldName, fieldNumber);
    this.type = type;
  }

  @Override
  public Object arrayOfField(int size) {
    return newArrayOfField(type, size);
  }

  @Override
  public int sizeOf(int elements) {
    throw new UnsupportedOperationException("Cannot map object fields to disk yet.");
  }

  @Override
  public Class storeType() {
    return Object[].class;
  }

  @UsedFromByteCode
  public static <T> T[] newArrayOfField(Class<T> type, int size) {
    return (T[]) Array.newInstance(type, size);
  }

  @Override
  public T getAllocation(Object[] arrays, int index) {
    T[] array = (T[]) arrays[fieldNumber];
    return get(array, index);
  }

  @UsedFromByteCode
  public static <T> T get(T[] array, int index) {
    return array[index];
  }

  @Override
  public void setAllocation(Object[] arrays, int index, T value) {
    T[] array = (T[]) arrays[fieldNumber];
    set(array, index, value);
  }

  @UsedFromByteCode
  public static <T> void set(T[] array, int index, T value) {
    array[index] = value;
  }

  @Override
  public boolean copySimpleValue() {
    return false;
  }

  @Override
  public String bcStoreType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String bcLStoreType() {
    return "[Ljava/lang/Object;";
  }

  @Override
  public String bcFieldType() {
    return type().getName().replace('.', '/');
  }

  @Override
  public String bcLFieldType() {
    return "L" + bcFieldType() + ";";
  }

  @Override
  public String bcLSetType() {
    return "Ljava/lang/Object;";
  }

  @Override
  public Class<T> type() {
    return type;
  }

  @Override
  public BCType bcType() {
    return BCType.Reference;
  }

  @Override
  public boolean isCallsNotEquals() {
    return true;
  }

  @UsedFromByteCode
  public static <T> boolean notEquals(T t1, T t2) {
    return t1 == null ? t2 != null : !t1.equals(t2);
  }

  @UsedFromByteCode
  public static <T> int hashCode(T t) {
    return t == null ? 0 : t.hashCode();
  }

  @Override
  public boolean isBufferStore() {
    return true;
  }

  @Override
  public short equalsPreference() {
    return 0;
  }

  public static <T> void write(ObjectOutput out, Class<T> clazz, T t) throws IOException {
    if (clazz == String.class)
      out.writeUTF((String) t);
    else
      out.writeObject(t);
  }

  public static <T> T read(ObjectInput in, Class<T> aClass) throws IOException, ClassNotFoundException {
    return (T) (aClass == String.class ? in.readUTF() : in.readObject());
  }
}
