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
import java.nio.FloatBuffer;

public class FloatFieldModel extends AbstractFieldModel<Float> {
  public FloatFieldModel(String fieldName, int fieldNumber) {
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

  public static FloatBuffer newArrayOfField(int size, MappedFileChannel mfc) {
    return acquireByteBuffer(mfc, sizeOf0(size)).asFloatBuffer();
  }

  @Override
  public Class storeType() {
    return FloatBuffer.class;
  }

  @Override
  public Float getAllocation(Object[] arrays, int index) {
    FloatBuffer array = (FloatBuffer) arrays[fieldNumber];
    return get(array, index);
  }

  public static float get(FloatBuffer array, int index) {
    return array.get(index);
  }

  @Override
  public void setAllocation(Object[] arrays, int index, Float value) {
    FloatBuffer array = (FloatBuffer) arrays[fieldNumber];
    set(array, index, value);
  }

  public static void set(FloatBuffer array, int index, float value) {
    array.put(index, value);
  }

  @Override
  public Class<Float> type() {
    return (Class) float.class;
  }

  @Override
  public String bcLFieldType() {
    return "F";
  }

  @Override
  public BCType bcType() {
    return BCType.Float;
  }

  @Override
  public boolean isCallsNotEquals() {
    return true;
  }

  @UsedFromByteCode
  public static boolean notEquals(float d1, float d2) {
    return Float.floatToIntBits(d1) != Float.floatToIntBits(d2);
  }

  @UsedFromByteCode
  public static int hashCode(float f) {
    return Float.floatToIntBits(f);
  }

  @Override
  public short equalsPreference() {
    return 31;
  }

  public static void write(ObjectOutput out, float f) throws IOException {
    out.writeFloat(f);
  }

  public static float read(ObjectInput in) throws IOException {
    return in.readFloat();
  }
}
