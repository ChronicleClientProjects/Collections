package vanilla.java.collections.impl;

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

import vanilla.java.collections.api.HugeAllocation;
import vanilla.java.collections.api.HugeElement;
import vanilla.java.collections.api.HugeElementType;

public abstract class AbstractHugeElement<T, TA extends HugeAllocation> implements HugeElement<T> {
  protected final AbstractHugeContainer<T, TA> container;
  protected long index;
  protected int offset;

  public AbstractHugeElement(AbstractHugeContainer<T, TA> container, long n) {
    this.container = container;
    final int allocationSize = container.allocationSize;
    index = n;
    offset(index, allocationSize);
    updateAllocation0(allocationSize);
  }

  private void offset(long index, int allocationSize) {
    offset = (int) (index % allocationSize);
    if (offset < 0) offset += allocationSize;
  }

  public void index(long n) {
    final int allocationSize = container.allocationSize;
    if (n / allocationSize != index() / allocationSize) {
      index = n;
      updateAllocation0(allocationSize);
    } else {
      index = n;
    }
    offset(index, allocationSize);
  }

  @Override
  public long index() {
    return index;
  }

  void next() {
    if (index >= container.longSize)
      container.ensureCapacity(index);
    index++;
    if (++offset >= container.allocationSize)
      updateAllocation();
  }

  void previous() {
    index--;
    if (offset > 0) {
      offset--;
    } else {
      updateAllocation();
    }
  }

  private void updateAllocation() {
    int allocationSize = container.allocationSize;
    if (index >= 0)
      updateAllocation0(allocationSize);
    offset(index, allocationSize);
  }

  @Override
  public HugeElementType hugeElementType() {
    return HugeElementType.Element;
  }

  protected abstract void updateAllocation0(int allocationSize);
}
