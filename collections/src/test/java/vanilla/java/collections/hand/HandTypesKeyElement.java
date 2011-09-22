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

import vanilla.java.collections.api.HugeElementType;
import vanilla.java.collections.impl.AbstractHugeContainer;
import vanilla.java.collections.impl.AbstractHugeElement;
import vanilla.java.collections.impl.AbstractHugeMap;
import vanilla.java.collections.model.BooleanFieldModel;
import vanilla.java.collections.model.IntFieldModel;

public class HandTypesKeyElement extends AbstractHugeElement<HandTypesKey, HandTypesAllocation> implements HandTypesKey {
  HandTypesAllocation allocation;

  public HandTypesKeyElement(AbstractHugeMap<HandTypesKey, HandTypesKeyElement, HandTypes, HandTypesValueElement, HandTypesAllocation> map, long n) {
    super((AbstractHugeContainer) map, n);
  }

  @Override
  public void setBoolean(boolean b) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean getBoolean() {
    return BooleanFieldModel.get(allocation.m_boolean, offset);
  }

  @Override
  public void setInt(int i) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getInt() {
    return IntFieldModel.get(allocation.m_int, offset);
  }

  @Override
  protected void updateAllocation0(int allocationSize) {
    allocation = container.getAllocation(index);
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
  public long longHashCode() {
    return getInt() * 31L + (getBoolean() ? 1 : 0);
  }

  @Override
  public int hashCode() {
    return (int) longHashCode();
  }

  @Override
  public void copyOf(HandTypesKey t) {
    throw new UnsupportedOperationException();
  }

  public HugeElementType hugeElementType() {
    return HugeElementType.KeyElement;
  }
}
