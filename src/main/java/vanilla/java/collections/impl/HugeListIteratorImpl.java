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
import vanilla.java.collections.api.HugeListIterator;

public class HugeListIteratorImpl<T, TA extends HugeAllocation, TE extends AbstractHugeElement<T, TA>> implements HugeListIterator<T> {
  private final AbstractHugeArrayList<T, TA, TE> list;
  private final TE mte;

  public HugeListIteratorImpl(AbstractHugeArrayList<T, TA, TE> list) {
    this.list = list;
    mte = list.acquireElement(-1);
  }

  @Override
  public boolean hasNext() {
    return mte.index + 1 < list.longSize;
  }

  @Override
  public T next() {
    mte.next();
    return (T) mte;
  }

  @Override
  public boolean hasPrevious() {
    return mte.index > 0;
  }

  @Override
  public T previous() {
    mte.previous();
    return (T) mte;
  }

  @Override
  public int nextIndex() {
    return (int) Math.min(nextLongIndex(), Integer.MAX_VALUE);
  }

  @Override
  public int previousIndex() {
    return (int) Math.min(previousLongIndex(), Integer.MAX_VALUE);
  }

  @Override
  public void remove() {
  }

  @Override
  public HugeListIterator<T> toStart() {
    mte.index(-1);
    return this;
  }

  @Override
  public HugeListIterator<T> toEnd() {
    mte.index(list.longSize);
    return this;
  }

  @Override
  public HugeListIterator<T> index(long n) {
    mte.index(n);
    return this;
  }

  @Override
  public long previousLongIndex() {
    return mte.index - 1;
  }

  @Override
  public long nextLongIndex() {
    return mte.index + 1;
  }

  @Override
  public void recycle() {
  }

  @Override
  public void index(int index) {
    mte.index(index);
  }

  @Override
  public void set(T t) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(T t) {
    throw new UnsupportedOperationException();
  }
}
