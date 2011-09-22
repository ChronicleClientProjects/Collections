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

import vanilla.java.collections.api.HugeElement;
import vanilla.java.collections.api.HugeElementType;
import vanilla.java.collections.model.BooleanFieldModel;
import vanilla.java.collections.model.IntFieldModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class HandTypesKeyImpl implements HandTypesKey, HugeElement<HandTypes>, Externalizable {
  private boolean m_boolean;
  private int m_int;

  @Override
  public void setBoolean(boolean b) {
    this.m_boolean = b;
  }

  @Override
  public boolean getBoolean() {
    return m_boolean;
  }

  @Override
  public void setInt(int i) {
    this.m_int = i;
  }

  @Override
  public int getInt() {
    return m_int;
  }

  @Override
  public void index(long n) {
    throw new UnsupportedOperationException();
  }

  @Override
  public long index() {
    return -1;
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    BooleanFieldModel.write(out, getBoolean());
    IntFieldModel.write(out, getInt());
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    setBoolean(BooleanFieldModel.read(in));
    setInt(IntFieldModel.read(in));
  }

  @Override
  public void copyOf(HandTypes t) {
    setBoolean(t.getBoolean());
    setInt(t.getInt());
  }

  @Override
  public HugeElementType hugeElementType() {
    return HugeElementType.KeyImpl;
  }

  @Override
  public String toString() {
    return "HandTypesElement{" +
        "int=" + getInt() +
        ", boolean=" + getBoolean() +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || !(o instanceof HandTypesKey)) return false;

    HandTypesKey that = (HandTypesKey) o;

    if (getInt() != that.getInt()) return false;
    if (getBoolean() != that.getBoolean()) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return (int) longHashCode();
  }

  @Override
  public long longHashCode() {
    return getInt() * 31L + (getBoolean() ? 1 : 0);
  }
}
