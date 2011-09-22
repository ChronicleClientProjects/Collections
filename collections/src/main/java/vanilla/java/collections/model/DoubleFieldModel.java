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
import java.nio.DoubleBuffer;

public class DoubleFieldModel extends AbstractFieldModel<Double> {
  public DoubleFieldModel(String fieldName, int fieldNumber) {
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

  public static DoubleBuffer newArrayOfField(int size, MappedFileChannel mfc) {
    return acquireByteBuffer(mfc, sizeOf0(size)).asDoubleBuffer();
  }

  @Override
  public Class storeType() {
    return DoubleBuffer.class;
  }

  @Override
  public Double getAllocation(Object[] arrays, int index) {
    DoubleBuffer array = (DoubleBuffer) arrays[fieldNumber];
    return get(array, index);
  }

  public static double get(DoubleBuffer array, int index) {
    return array.get(index);
  }

  @Override
  public void setAllocation(Object[] arrays, int index, Double value) {
    DoubleBuffer array = (DoubleBuffer) arrays[fieldNumber];
    set(array, index, value);
  }

  public static void set(DoubleBuffer array, int index, double value) {
    array.put(index, value);
  }

  @Override
  public Class<Double> type() {
    return (Class) double.class;
  }

  @Override
  public int bcFieldSize() {
    return 2;
  }

  @Override
  public String bcLFieldType() {
    return "D";
  }

  @Override
  public BCType bcType() {
    return BCType.Double;
  }

  @Override
  public boolean isCallsNotEquals() {
    return true;
  }

  @UsedFromByteCode
  public static boolean notEquals(double d1, double d2) {
    return Double.doubleToLongBits(d1) != Double.doubleToLongBits(d2);
  }

  @UsedFromByteCode
  public static int hashCode(double d) {
    return LongFieldModel.hashCode(Double.doubleToLongBits(d));
  }

  @Override
  public short equalsPreference() {
    return 29; // 63, lower due to the increase memory requirement.
  }

  public static void write(ObjectOutput out, double d) throws IOException {
    out.writeDouble(d);
  }

  public static double read(ObjectInput in) throws IOException {
    return in.readDouble();
  }
}
